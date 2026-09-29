#lang racket
(require racket/match)
(require "queue.rkt")

(provide (all-defined-out))

(define ITEMS 5)


; TODO (0p)
; Aveți libertatea să vă structurați programul cum doriți
; (dar cu restricțiile de mai jos), astfel încât
; funcția serve să funcționeze conform specificației.
;
; Restricții (impuse de checker):
; - va exista în continuare funcția (empty-counter index)
; - veți reprezenta cozile folosind noul TDA queue

(define-struct counter (index tt et queue open) #:transparent)
(define (empty-counter index)
  (make-counter index 0 0 empty-queue #t))

(define (update f counters index)
  (map (λ (C) (if (= (counter-index C) index) (f C) C)) counters))

(define (tt+ minutes)
  (λ (C) (struct-copy counter C [tt (+ (counter-tt C) minutes)])))

(define (et+ minutes)
  (λ (C) (struct-copy counter C [et (+ (counter-et C) minutes)])))

(define ((add-to-counter name items) C)
  (struct-copy counter C
               [tt (+ (counter-tt C) items)]
               [et (if (queue-empty? (counter-queue C))
                       (+ (counter-et C) items)
                       (counter-et C))]
               [queue (enqueue (cons name items) (counter-queue C))]))

(define (get-min opr counters)
  (if (null? (cdr counters))
      (cons (counter-index (car counters)) (opr (car counters)))
      (match (get-min opr (cdr counters))
        [(cons min-idx min-el)
         (if (or (< (opr (car counters)) min-el)
                 (and (= (opr (car counters)) min-el)
                      (< (counter-index (car counters)) min-idx)))
             (cons (counter-index (car counters)) (opr (car counters)))
             (cons min-idx min-el))])))

(define min-tt (curry get-min counter-tt))
(define min-et (curry get-min counter-et))

(define (remove-first-from-counter C)
  (struct-copy counter C
               [tt (- (counter-tt C) (counter-et C))]
               [et (if (queue-empty? (dequeue (counter-queue C)))
                       0
                       (cdr (top (dequeue (counter-queue C)))))]
               [queue (dequeue (counter-queue C))]))

(define ((pass-time-through-counter minutes) C)
  (struct-copy counter C
               [tt (max 0 (- (counter-tt C) minutes))]
               [et (max 0 (- (counter-et C) minutes))]))


; TODO 7 (70p)
; Implementați funcția care simulează fluxul clienților pe la case.
; ATENȚIE: Față de etapa 3, apar modificări în:
; - formatul listei de cereri (requests)
; - formatul rezultatului funcției (explicat mai jos)
; requests conține 6 tipuri de cereri:
;   4 moștenite din etapa 3:
;   - (<name> <n-items>) - așază persoana <name> la coadă la o casă deschisă
;   - (delay <index> <minutes>) - întârzie casa <index> cu <minutes> minute
;   - (ensure <average>) - cât timp tt-ul mediu al caselor deschise depășește
;                          <average>, adaugă case fără restricții (case slow)
;   - <x> - actualizează starea caselor conform cu trecerea a <x> minute
;           de la ultima cerere (afectează câmpurile tt, et, queue)
;   plus 2 noi:
;   - (close <index>) - închide casa cu indexul <index> (casa există deja)
;   - (open <index>) - deschide casa cu indexul <index> (casa există deja)
; Sistemul procesează cererile în ordine, astfel:
; - așază persoana la casa DESCHISĂ cu tt minim la care are voie;
;   se garantează că persoana poate fi distribuită la o casă
; - nicio modificare pentru situația când o casă suferă o întârziere
; - dacă tt-ul mediu pentru toate casele DESCHISE > <average>,
;   adaugă case slow până când media <= <average>
; - nicio modificare în modelarea trecerii timpului
; - o casă care se închide nu mai primește clienți noi și:
;   - primul client (dacă există) își continuă treaba la această casă
;   - restul clienților se redistribuie la celelalte case,
;     în ordinea în care erau așezați la coadă
; - o casă care se deschide redevine disponibilă pentru clienți
; Funcția serve întoarce o pereche cu punct între:
; - lista clienților care au părăsit magazinul, sortată cronologic
;   - elementele listei au forma (index_casă . nume)
;   - când mai mulți clienți ies simultan, sortați după indexul casei
; - lista cozilor nevide în starea finală, sortată după indexul casei
;   - elementele listei au forma (index_casă . coadă) (coada este de tip queue)
(define (serve requests fast-counters slow-counters)

  (define (serve-with-exits requests fast-counters slow-counters exits)

    (define (__serve_helper__ requests fast slow current-exits func idx)
      (serve-with-exits requests (update func fast idx) (update func slow idx) current-exits))

    (define (__has_customers?__ C)
      (not (queue-empty? (counter-queue C))))

    (define (__delayer__ mins C)
      ((et+ mins) ((tt+ mins) C)))

    (define (__bring-out-customers__ q)
      (if (queue-empty? q)
          '()
          (cons (list (car (top q)) (cdr (top q)))
                (__bring-out-customers__ (dequeue q)))))

    (define (__open-counters__ all-counters)
      (filter counter-open all-counters))

    (if (null? requests)
        (let* ([all-counters (append fast-counters slow-counters)]
               [non-empty (filter __has_customers?__ all-counters)])
          (cons exits (map (λ (C) (cons (counter-index C) (counter-queue C))) non-empty)))

        (match (car requests)
          [(list 'ensure average)
           (if (>= average (if (null? (__open-counters__ (append fast-counters slow-counters)))
                               0
                               (/ (apply + (map counter-tt (__open-counters__ (append fast-counters slow-counters))))
                                  (length (__open-counters__ (append fast-counters slow-counters))))))
               (serve-with-exits (cdr requests) fast-counters slow-counters exits)
               (serve-with-exits requests fast-counters
                                 (append slow-counters (list (empty-counter (+ 1 (length (append fast-counters slow-counters))))))
                                 exits))]

          [(list 'open index)
           (__serve_helper__ (cdr requests) fast-counters slow-counters exits
                             (λ (C) (struct-copy counter C [open #t]))
                             index)]

          [(list 'close index)
           (let* ([all-counters (append fast-counters slow-counters)]
                  [target-c (findf (λ (C) (= (counter-index C) index)) all-counters)]
                  [q (counter-queue target-c)])

             (if (queue-empty? q)
                 (__serve_helper__ (cdr requests) fast-counters slow-counters exits
                                   (λ (C) (struct-copy counter C [open #f]))
                                   index)

                 (__serve_helper__ (append (__bring-out-customers__ (dequeue q))
                                           (cdr requests))
                                   fast-counters slow-counters exits
                                   (λ (C) (struct-copy counter C
                                                       [tt (counter-et C)]
                                                       [queue (enqueue (top q) empty-queue)]
                                                       [open #f]))
                                   index)))]

          [(list name n-items)
           (__serve_helper__ (cdr requests) fast-counters slow-counters exits
                             (add-to-counter name n-items)
                             (car (min-tt (if (<= n-items ITEMS)
                                              (__open-counters__ (append fast-counters slow-counters))
                                              (__open-counters__ slow-counters)))))]
          [(list 'delay index minutes)
           (__serve_helper__ (cdr requests) fast-counters slow-counters exits
                             (curry __delayer__ minutes)
                             index)]

          [x
           (let* ([all-counters (append fast-counters slow-counters)]
                  [active-counters (filter __has_customers?__ all-counters)])
             (if (null? active-counters)
                 (serve-with-exits (cdr requests)
                                   (map (pass-time-through-counter x) fast-counters)
                                   (map (pass-time-through-counter x) slow-counters)
                                   exits)

                 (let* ([tmp-min (min-et active-counters)]
                        [min-idx (car tmp-min)]
                        [min-time (cdr tmp-min)])
                   (if (<= min-time x)
                       (let ([name (car (top (counter-queue
                                              (findf (λ (C) (= (counter-index C) min-idx)) all-counters))))])
                         (__serve_helper__ (cons (- x min-time) (cdr requests))
                                           (map (pass-time-through-counter min-time) fast-counters)
                                           (map (pass-time-through-counter min-time) slow-counters)
                                           (append exits (list (cons min-idx name)))
                                           remove-first-from-counter
                                           min-idx))
                       (serve-with-exits (cdr requests)
                                         (map (pass-time-through-counter x) fast-counters)
                                         (map (pass-time-through-counter x) slow-counters)
                                         exits)))))])))

  (serve-with-exits requests fast-counters slow-counters '()))