#lang racket
(require racket/match)
(require "queue.rkt")

(provide (all-defined-out))

(define ITEMS 5)

;; ATENȚIE: Este necesar să implementați întâi
;;          TDA-ul queue în fișierul queue.rkt.
;; Reveniți la acest fișier după ce ați implementat tipul 
;; queue și ați verificat implementarea folosind checker-ul.


; Structura counter nu se modifică.
; Se modifică însă implementarea câmpului queue:
; - în loc de listă, acesta va fi o structură de tip queue
; - modificarea nu este vizibilă în definiția structurii,
;   ci în implementarea operațiilor tipului counter
(define-struct counter (index tt et queue) #:transparent)


; TODO 6 (20p)
; Actualizați funcțiile de mai jos conform cu 
; noua reprezentare a cozii de persoane.
; Elementele cozii rămân perechi (nume . nr_produse).
; RESTRICȚII (5p per abatere)
;  - Respectați "bariera de abstractizare", adică 
;    operați cu coada folosind exclusiv interfața:
;    - empty-queue
;    - queue-empty?
;    - enqueue
;    - dequeue
;    - top
; Obs: Doar câteva funcții necesită actualizări.
(define (empty-counter index)           ; testată de checker
  (make-counter index 0 0 empty-queue))

(define (update f counters index)
  (map (λ (C) (if (= (counter-index C) index) (f C) C)) counters))

(define (tt+ minutes)
  (λ (C) (struct-copy counter C [tt (+ (counter-tt C) minutes)])))

(define (et+ minutes)
  (λ (C) (struct-copy counter C [et (+ (counter-et C) minutes)])))

(define ((add-to-counter name items) C)                                 ; testată de checker
  (struct-copy counter C
               [tt (+ (counter-tt C) items)]
               [et (if (queue-empty? (counter-queue C))
                       (+ (counter-et C) items)
                       (counter-et C))]
               [queue (enqueue (cons name items) (counter-queue C))]))  ; nu modificați signatura!
    

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

(define (remove-first-from-counter C)   ; testată de checker
  (struct-copy counter C
               [tt (- (counter-tt C) (counter-et C))]
               [et (if (queue-empty? (dequeue (counter-queue C)))
                       0
                       (cdr (top (dequeue (counter-queue C)))))]
               [queue (dequeue (counter-queue C))]))



; TODO 7 (10p)
; Implementați o funcție care calculează starea
; unei case după un număr dat de minute.
; Funcția presupune, fără să verifice, că în acest timp
; nu iese nimeni din coadă, deci se modifică
; doar câmpurile tt și et.
; Este responsabilitatea utilizatorului să nu apeleze
; funcția cu minutes > et și coadă nevidă.
; La casele fără clienți, este responsabilitatea
; voastră să nu produceți timpi negativi.
(define ((pass-time-through-counter minutes) C)
  (struct-copy counter C
               [tt (max 0 (- (counter-tt C) minutes))]
               [et (max 0 (- (counter-et C) minutes))]))

; TODO 8 (60p)
; Implementați funcția care simulează fluxul clienților pe la case.
; ATENȚIE: Față de etapa 2, apar modificări în:
; - formatul listei de cereri (requests)
; - formatul rezultatului funcției (explicat mai jos)
; requests conține 4 tipuri de cereri:
;   3 moștenite din etapa 2:
;   - (<name> <n-items>) - așază persoana <name> la coadă la o casă
;   - (delay <index> <minutes>) - întârzie casa <index> cu <minutes> minute
;   - (ensure <average>) - cât timp tt-ul mediu al tuturor caselor depășește 
;                          <average>, adaugă case fără restricții (case slow)
;   plus noutatea:
;   - <x> - actualizează starea caselor conform cu trecerea a <x> minute
;           de la ultima cerere (afectează câmpurile tt, et, queue)
; Obs: Cererile (remove-first) din etapa 2 sunt înlocuite de un mecanism  
; mai sofisticat de a scoate clienții din coadă (pe măsură ce trece timpul).
; Sistemul procesează cererile în ordine, astfel:
; - nicio modificare pentru cererile moștenite din etapa 2
; - când timpul prin sistem avansează cu <x> minute, starea caselor
;   se actualizează pentru a reflecta trecerea timpului;
;   ieșirile clienților din coadă se rețin în ordine cronologică.
; Funcția serve întoarce o pereche cu punct între:
; - lista clienților care au părăsit magazinul, sortată cronologic
;   - elementele listei au forma (index_casă . nume)
;   - când mai mulți clienți ies simultan, sortați după indexul casei
; - lista caselor în starea finală (ca rezultatul din etapele 1 și 2)
; Sugestii:
; - gestionați cronologia folosind în mod repetat funcția min-et 
; - pentru a menține lista clienților plecați, definiți o funcție ajutătoare
; (cu un parametru în plus față de serve), pe care serve doar o apelează.
; RESTRICȚII (5p per abatere)
;  - Folosiți minim un let și un let* (care nu ar putea fi let). (2*5p)
;  - Respectați "bariera de abstractizare" oricând operați cu tipul queue.
(define (serve requests fast-counters slow-counters)
 
  (define (serve-with-exits requests fast-counters slow-counters exits)
    
    (define (__serve_helper__ requests fast slow  current-exits func idx)
      (serve-with-exits requests (update func fast idx) (update func slow idx) current-exits))

    (define (__has_customers?__ C)
      (not (queue-empty? (counter-queue C))))

    (define (__delayer__ mins C)
      ((et+ mins) ((tt+ mins) C)))

    (if (null? requests)
        (cons exits (append fast-counters slow-counters))

        (match (car requests)
          [(list 'ensure average)
           (if (>= average (/ (apply + (map counter-tt (append fast-counters slow-counters)))
                               (length (append fast-counters slow-counters))))
               (serve-with-exits (cdr requests) fast-counters slow-counters exits)
               (serve-with-exits requests fast-counters
                                 (append slow-counters (list (empty-counter (+ 1 (length (append fast-counters slow-counters))))))
                                 exits))]
          
          [(list name n-items)
           (__serve_helper__ (cdr requests) fast-counters slow-counters exits
                             (add-to-counter name n-items)
                             (car (min-tt (if (<= n-items ITEMS)
                                              (append fast-counters slow-counters) 
                                              slow-counters))))]

          [(list 'delay index minutes)
           (__serve_helper__ (cdr requests)
                             fast-counters
                             slow-counters
                             exits
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
