#lang racket
(require racket/match)

(provide (all-defined-out))

(define ITEMS 5)

;; C1, C2, C3, C4 sunt case într-un magazin.
;; C1 acceptă doar clienți care au cumpărat maxim ITEMS produse
;; (ITEMS este definit mai sus).
;; C2 - C4 nu au restricții.
;; Considerăm că procesarea fiecărui produs la casă durează un minut.
;; Casele pot suferi întârzieri (delay).
;; La un moment dat, la fiecare casă există
;; 0 sau mai mulți clienți care stau la coadă.
;; Timpul total (tt) al unei case reprezintă
;; timpul de procesare al celor aflați la coadă,
;; adică numărul de produse cumpărate de ei +
;; întârzierile suferite de casa respectivă (dacă există).
;; Ex:
;; la C3 sunt Ana cu 3 produse și Geo cu 7 produse,
;; și C3 nu are întârzieri => tt pentru C3 este 10.


; Definim o structură care descrie o casă prin:
; - index (de la 1 la 4)
; - tt (timpul total descris mai sus)
; - queue (coada cu persoanele care așteaptă)
(define-struct counter (index tt queue) #:transparent)


; TODO 1 (10p)
; Implementați o funcție care întoarce o structură counter goală.
; tt este 0 si coada este vidă.
; Obs: la definirea structurii counter se creează automat
; o funcție make-counter pentru a construi date de acest tip
(define (empty-counter index)
  (make-counter index 0 null)
)


; TODO 2 (10p)
; Implementați o funcție care crește tt-ul unei case
; cu un număr dat de minute.
(define (tt+ C minutes)
  (struct-copy counter C [tt (+ (counter-tt C) minutes)])
)


; TODO 3 (20p)
; Implementați o funcție care primește o listă nevidă 
; de case și întoarce o pereche dintre:
; - indexul casei (din listă) care are cel mai mic tt
; - tt-ul acesteia
; Obs: când mai multe case au același tt,
; este preferată casa cu indexul cel mai mic
; RESTRICȚII (20p):
;  - Folosiți recursivitate pe coadă.

(define (__min_t lcr min)
  (if (null? lcr)
      (cons (counter-index min)
            (counter-tt min))

      (if (or (< (counter-tt (car lcr)) (counter-tt min))
              (and (= (counter-tt (car lcr)) (counter-tt min))
                   (< (counter-index (car lcr)) (counter-index min))))
          (__min_t (cdr lcr) (car lcr))
          (__min_t (cdr lcr) min))
   )
)

(define (min-tt counters)
  (__min_t (cdr counters) (car counters))
)


; TODO 4 (20p)
; Implementați aceeași funcționalitate de mai sus,
; cu recursivitate pe stivă.
; RESTRICȚII (20p):
;  - Folosiți recursivitate pe stivă.
(define (min-tt-stack counters)
  (if (null? (cdr counters))
      (cons (counter-index (car counters))
            (counter-tt (car counters)))

      (match (min-tt-stack (cdr counters))
        [(cons min-idx min-tt)
         
         (if (or (< (counter-tt (car counters)) min-tt)
                 (and (= (counter-tt (car counters)) min-tt)
                      (< (counter-index (car counters)) min-idx)))
             (cons (counter-index (car counters))
                   (counter-tt (car counters)))
             (cons min-idx
                   min-tt))
        ]
      )
   )
)


; TODO 5 (10p)
; Implementați o funcție care adaugă o persoană la o casă.
; C = casa, name = numele persoanei,
; n-items = numărul de produse cumpărate
; Veți întoarce o nouă structură obținută prin așezarea perechii
; (name . n-items) la sfârșitul cozii de așteptare.
(define (add-to-counter C name n-items)
  (struct-copy counter (tt+ C n-items)
               [queue (append (counter-queue C) (list (cons name n-items)))]
  )
)


; TODO 6 (50p)
; Implementați funcția care simulează fluxul clienților pe la case.
; requests = listă de cereri care pot fi de 2 tipuri:
; - (<name> <n-items>) - așază persoana <name> la coadă la o casă
; - (delay <index> <minutes>) - întârzie casa <index> cu <minutes> minute
; C1, C2, C3, C4 = structuri corespunzătoare celor 4 case
; Sistemul procesează cererile în ordine, astfel:
; - așază persoana la casa cu tt minim la care are voie
;   (conform logicii implementate de min-tt)
; - când o casă suferă o întârziere, tt-ul ei crește
(define (serve requests C1 C2 C3 C4)
  
  ; Puteți să vă definiți aici funcții ajutătoare (define în define)
  ; - avantaj: aveți acces la variabilele
  ;   requests, C1, C2, C3, C4 fără a le retrimite ca parametri
  ; Puteți să vă definiți funcții ajutătoare în exteriorul lui "serve"
  ; - avantaj: puteți testa fiecare funcție imediat ce ați implementat-o
  ; Nu este obligatoriu să definiți funcții ajutătoare.

  (define (__update__continue idx opr)
    (cond
      [(= idx 1) (serve (cdr requests) (opr C1) C2 C3 C4)]
      [(= idx 2) (serve (cdr requests) C1 (opr C2) C3 C4)]
      [(= idx 3) (serve (cdr requests) C1 C2 (opr C3) C4)]
      [(= idx 4) (serve (cdr requests) C1 C2 C3 (opr C4))]
    )
   )
  
  (if (null? requests)
      (list C1 C2 C3 C4)
      (match (car requests)
        [(list 'delay index minutes)
         (__update__continue index (λ (C) (tt+ C minutes)))
        ]
        [(list name n-items)
         (__update__continue (car (min-tt (if (<= n-items ITEMS)
                                               (list C1 C2 C3 C4)  
                                               (list C2 C3 C4))))
                              (λ (C) (add-to-counter C name n-items)))
        ]
      )
  )
)
