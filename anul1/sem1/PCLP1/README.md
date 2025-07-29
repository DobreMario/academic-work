# 🖥️ PCLP1 – Programarea Calculatoarelor și Limbaje de Programare 1

Acest folder conține cele trei teme realizate în cadrul cursului **PCLP1**, desfășurat în semestrul I al anului I (2024). Proiectele au fost dezvoltate în limbajul **C**, folosind stil procedural, fără biblioteci externe.

---

## 📅 Calendarul temelor și scoruri

| Temă   | Perioadă             | Scor / Maximum | Descriere sumară                                  |
|--------|----------------------|----------------|---------------------------------------------------|
| Tema 1 | 28 oct – 13 nov 2024 | 96 / 100       | 4 probleme introductive – structuri, vectori, matrici |
| Tema 2 | 11 nov – 20 dec 2024 | 105 / 100      | Algoritm pe grid cu `struct` și memorie dinamică |
| Tema 3 | 9 dec – 20 dec 2024  | 100 / 100      | Procesare imagini `.pgm` cu fișiere text/binare  |

---

## 📌 Descriere temă cu temă

### 📁 Tema 1 – 4 probleme introductive

- **Subiect:** Scrierea a 4 mini-programe pentru exersarea structurii codului în C: utilizarea structurilor liniare și decizionale, vectori și matrici.
- **Perioadă:** 28 oct – 13 nov 2024
- **Scor:** 96/100 *(checker: 100/100)*
- **Feedback primit:**
  ```
  -3.0: Readme cam sumar. El trebuie să fie util cui preia codul tău sau cui folosește programul tău. 
  În el ar trebui să vorbești despre cum ai rezolvat cerințele, care sunt limitările implementării și care au fost 
  dificultățile întâlnite și cum le-ai depășit.
  -1.0: Cod comentat (//return).
  ```
- **Notițe personale:**
  - Inițial README-ul a fost sumar; am învățat să documentez mai bine rezolvările.
  - Comentariul `//return;` a fost folosit pentru că la laborator ni s-a recomandat ca orice funcție să aibă `return`, chiar și când nu era necesar.

---

### 📁 Tema 2 – simulare de algoritm pe imagine (grid)

- **Subiect:** Rezolvare a unei probleme mari care simulează un algoritm pe un grid de pixeli reprezentând o imagine. Lucrarea a implicat:
  - utilizarea de **memorie dinamică**,
  - definirea de `struct`,
  - citirea și interpretarea de comenzi,
  - prelucrare pe matrice.
- **Perioadă:** 11 nov – 20 dec 2024
- **Scor:** 105/100 *(inclusiv punctaj bonus)*
- **Feedback primit:**
  ```
  Good job!
  ```
- **Tehnologii/cerințe:** verificare cu **Valgrind** (obligatorie pentru validarea temei).

---

### 📁 Tema 3 – procesare imagini `.pgm`

- **Subiect:** Implementarea unor algoritmi de procesare a imaginilor în format `.pgm` folosind fișiere text și binare. Codul a fost modular și scalabil.
- **Perioadă:** 9 dec – 20 dec 2024
- **Scor:** 100/100
- **Feedback primit:**
  ```
  100/100
  -0 cand folosesti snakecase, ar trebui sa separi cu _ orice 2 cuvinte 
  - identify_format_for_editing() nu identifyformat_for_editing()
  +0 ai compilat cu -Wall -Wextra
  +0 bun README, dar incearca sa nu te concentrezi asa de mult pe cod
  +0 folosirea de enum
  +0 excelenta impartire pe fisiere si structura a codului, felicitari
  ```
- **Tehnici folosite:**
  - `enum`, `union`
  - separare logică a codului în fișiere,
  - utilizarea `snake_case`,
  - compilare riguroasă cu `-Wall -Wextra`.

---

## 🧠 Ce am învățat pe parcursul temelor

- Organizarea logică a codului (modularizare, separare pe fișiere)
- Scrierea unui `README.md` clar și informativ
- Utilizarea corectă a stilului de programare (`snake_case`, `enum`, `union`)
- Diagnosticarea memoriei cu Valgrind
- Îmbunătățirea treptată a stilului de cod și a documentației pe baza feedbackului
