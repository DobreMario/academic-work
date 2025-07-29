
# Proiect: Detectarea Depresiei folosind Machine Learning

**Autor:** Dobre Mario-Sebastian  
**Grup:** 314CA  
**Facultatea:** Automatica si Calculatoare  
**Sursa dataset:** [Kaggle - Depression Dataset](https://www.kaggle.com/code/ayanpub16/depression/input)

---

## 1. Introducere

Acest proiect își propune să detecteze prezența depresiei pe baza unui set de date socio-economice, folosind tehnici de machine learning. Analiza include preprocesarea datelor, vizualizări, echilibrarea claselor, antrenarea unui model și evaluarea performanței.

## 2. Structura codului

- `depressed.py` – Scriptul principal care realizează toți pașii: încărcare, analiză, preprocesare, modelare și evaluare.
- `datasets/` – Conține seturile de date de antrenare și testare generate.
- `plots/` – Conține graficele generate pentru analiză și evaluare.

## 3. Preprocesarea datelor

- Încărcarea datelor din fișierul CSV.
- Tratarea valorilor lipsă (imputare cu media pentru anumite coloane).
- Analiza statistică descriptivă a variabilelor.

**Notă:** Am testat și tehnici de normalizare și standardizare a variabilelor numerice, însă pentru modelul Extra Trees acestea nu au adus îmbunătățiri semnificative ale performanței. Deoarece acest tip de model nu este sensibil la scara variabilelor, am preferat să exclud aceste etape din pipeline-ul final pentru claritate și simplitate.

## 4. Vizualizări exploratorii

Pentru a înțelege mai bine datele, au fost generate mai multe grafice:

### a) Distribuția variabilelor față de depresie

![overview_countplots_vs_depressed](plots/overview_countplots_vs_depressed.png)

**Comentariu și interpretare:**
Graficul prezintă modul în care principalele variabile categorice (precum sexul, starea civilă, numărul de copii, nivelul de educație sau sursele de venit) se distribuie în funcție de prezența depresiei. Se observă diferențe între grupuri, de exemplu anumite categorii de venit sau nivel de educație pot fi asociate cu o incidență mai mare a depresiei. Acest lucru sugerează că factori socio-economici și demografici pot influența riscul de depresie. Pentru preprocesare, este important să codificăm corect aceste variabile și să verificăm dacă există dezechilibre majore între clase, ceea ce poate necesita tehnici de echilibrare a datelor. De exemplu, dacă observăm că o anumită categorie (precum persoanele necăsătorite) are o rată mai mare de depresie, putem formula ipoteze privind influența suportului social sau a veniturilor asupra sănătății mintale.

### b) Histograme pentru variabilele numerice

![histograme_variabile_numerice](plots/histograme_variabile_numerice.png)

**Comentariu și interpretare:**
Histogramele arată distribuția variabilelor numerice precum vârsta, numărul de copii, cheltuielile sau veniturile. Se pot observa distribuții asimetrice, valori extreme (outliers) sau grupări neașteptate. De exemplu, anumite variabile pot avea o concentrație mare de valori mici și câteva valori foarte mari. Acest lucru poate influența modul în care modelul învață relațiile din date. În cazul unor algoritmi sensibili la scară, ar fi utilă normalizarea sau standardizarea, însă pentru Extra Trees acest pas nu a fost necesar. Totuși, identificarea outlierilor rămâne importantă pentru calitatea datelor. Dacă observăm o variabilă cu mulți outlieri, putem decide să aplicăm transformări suplimentare sau să analizăm sursa acestor valori.

### c) Countplot pentru variabilele categorice

![countplot_variabile_categorice](plots/countplot_variabile_categorice.png)

**Comentariu și interpretare:**
Acest grafic evidențiază frecvența fiecărei categorii pentru variabilele discrete (ex: sex, stare civilă, surse de venit). Se pot observa dezechilibre între categorii, de exemplu dacă o anumită categorie este mult mai prezentă decât altele. Acest dezechilibru poate influența modul de împărțire a datelor și performanța modelului, motiv pentru care am folosit SMOTE pentru echilibrarea claselor. De asemenea, ajută la identificarea unor posibile probleme de reprezentativitate în date. Dacă o categorie este subreprezentată, putem decide să colectăm mai multe date sau să folosim tehnici de oversampling.

### d) Detectarea outlierilor

![boxplot_age](plots/boxplot_age.png)

**Comentariu și interpretare:**
Boxplot-ul pentru variabila Age evidențiază prezența unor valori extreme (outliers) la vârste foarte mici sau foarte mari. Aceste valori pot influența analiza statistică și performanța anumitor modele. Pentru Extra Trees nu este critic, dar pentru alte modele ar putea fi necesară tratarea outlierilor (prin eliminare sau transformare). Identificarea outlierilor ajută la creșterea robusteții modelului și la evitarea unor concluzii eronate.

### e) Analiza relațiilor cu variabila țintă

![violinplot_age_vs_depressed](plots/violinplot_age_vs_depressed.png)

**Comentariu și interpretare:**
Violin plot-ul arată distribuția vârstei pentru fiecare clasă a variabilei țintă (depressed). Se observă că distribuția vârstei diferă între persoanele cu și fără depresie, ceea ce sugerează că vârsta poate avea o influență asupra riscului de depresie. Acest tip de analiză ajută la identificarea caracteristicilor relevante pentru model și la formularea unor ipoteze suplimentare despre date.

## 5. Preprocesare și echilibrare

- Separarea variabilei țintă (`depressed`) de restul caracteristicilor.
- Echilibrarea claselor folosind SMOTE pentru a preveni bias-ul modelului.

## 6. Împărțirea datelor

- Împărțirea setului de date în seturi de antrenare și testare (80% train, 20% test).
- Salvarea acestor seturi pentru reproducibilitate.

**Notă:** Inițial am folosit o împărțire 60% train / 40% test, dar pentru o evaluare mai robustă am modificat ulterior la 80% train / 20% test, pentru a asigura un set de antrenare mai mare și rezultate mai stabile.

## 7. Modelare

- Antrenarea unui model Extra Trees Classifier cu ponderare pentru clase.
- Evaluarea modelului pe setul de testare.

## 8. Evaluare și rezultate

- Calcularea metrcilor: acuratețe, precizie, recall, F1-score.
- Generarea unui raport de clasificare detaliat.

### Rezultate obținute. Teste comparative: selecția numărului optim de variabile (feature selection)

Pentru a evalua impactul selecției variabilelor asupra performanței, am antrenat și testat modelul Extra Trees folosind doar cele mai importante variabile (feature importance): top 3, top 5, top 8, top 10 și toate variabilele disponibile. Rezultatele comparative sunt prezentate mai jos:

| Variante de feature selection | Acuratețe | Precizie | Recall | F1-score |
|------------------------------|------------|----------|--------|----------|
| Top 3 variabile              | 0.62       | 0.63     | 0.53   | 0.57     |
| Top 5 variabile              | 0.73       | 0.72     | 0.72   | 0.72     |
| Top 8 variabile              | 0.82       | 0.83     | 0.80   | 0.81     |
| Top 10 variabile             | 0.83       | 0.83     | 0.82   | 0.83     |
| Toate variabilele            | 0.83       | 0.82     | 0.84   | 0.83     |

> **Observație:**
> Performanța modelului cu top 10 variabile este practic identică cu cea a modelului full, dar cu avantajul unui model mai simplu, mai rapid și mai ușor de interpretat. Pentru raportarea finală am ales varianta cu top 10 variabile.


### Testarea numărului de estimatori (iterații) pentru Extra Trees

Am testat performanța modelului Extra Trees folosind diferite valori pentru numărul de estimatori (iterații): 100, 500, 1000 și 5000. Am observat că cea mai bună acuratețe a fost obținută pentru 500 de estimatori, chiar mai bună decât pentru 1000 sau 5000. Acest lucru sugerează că, pentru acest set de date, creșterea excesivă a numărului de estimatori nu aduce beneficii suplimentare și poate chiar duce la scăderea ușoară a performanței din cauza supraînvățării sau a zgomotului introdus.

> **Notă:** Pentru raportarea finală și interpretări, am folosit rezultatele cu 500 estimatori și top 10 variabile, deoarece acestea au oferit cel mai bun echilibru între performanță și simplitate.

### Matrice de confuzie și comparație: model cu top 10 variabile vs model cu toate variabilele

![confusion_matrix_top10](plots/confusion_matrix_top10.png)
![confusion_matrix](plots/confusion_matrix.png)

#### Rezultate model (doar top 10 variabile, 500 estimatori):

- **Acuratețe:** 0.85
- **Precizie:** 0.85
- **Recall:** 0.85
- **F1-score:** 0.85

| Clasa | Precizie | Recall | F1-score | Support |
|-------|----------|--------|----------|---------|
|   0   |   0.86   |  0.85  |   0.85   |   244   |
|   1   |   0.85   |  0.85  |   0.85   |   233   |
|accuracy|          |        |   0.85   |   477   |
|macro avg| 0.85    |  0.85  |   0.85   |   477   |
|weighted avg|0.85  |  0.85  |   0.85   |   477   |

#### Rezultate model (toate variabilele, 500 estimatori):

- **Acuratețe:** 0.83
- **Precizie:** 0.82
- **Recall:** 0.84
- **F1-score:** 0.83

| Clasa | Precizie | Recall | F1-score | Support |
|-------|----------|--------|----------|---------|
|   0   |   0.85   |  0.83  |   0.84   |   244   |
|   1   |   0.82   |  0.84  |   0.83   |   233   |
|accuracy|          |        |   0.83   |   477   |
|macro avg| 0.83    |  0.83  |   0.83   |   477   |
|weighted avg|0.83  |  0.83  |   0.83   |   477   |

**Comparație și concluzie:**

Modelul antrenat doar cu top 10 variabile relevante (selectate automat) a obținut rezultate ușor mai bune decât modelul antrenat pe toate variabilele disponibile, atât ca acuratețe, cât și ca scoruri de precizie, recall și F1. Acest lucru arată că selecția automată a caracteristicilor nu doar simplifică modelul și reduce riscul de overfitting, dar poate chiar îmbunătăți performanța generală. Un model mai simplu este mai rapid, mai ușor de interpretat și mai robust la zgomotul din date. Pentru raportarea finală și interpretări, recomand utilizarea modelului cu top 10 variabile.

### b) Matricea de corelații pentru variabile numerice

![correlation_matrix](plots/correlation_matrix.png)

**Comentariu și interpretare:**
Matricea de corelații evidențiază relațiile dintre variabilele numerice. Se pot observa perechi de variabile puternic corelate (pozitiv sau negativ), ceea ce poate indica redundanță sau influență comună asupra variabilei țintă. De exemplu, cheltuielile și veniturile pot fi corelate, ceea ce poate influența modul în care modelul interpretează importanța acestor variabile. Pentru preprocesare, se poate lua în considerare eliminarea variabilelor foarte corelate sau aplicarea unor tehnici de reducere a dimensionalității (PCA), mai ales dacă se folosesc algoritmi sensibili la multicoliniaritate. Analiza corelațiilor ajută și la identificarea unor relații ascunse între factori socio-economici.

## 9. Concluzii

Modelul Extra Trees a oferit rezultate competitive pentru detecția depresiei pe baza datelor socio-economice. Vizualizările și analiza corelațiilor ajută la interpretarea factorilor relevanți, iar pașii de preprocesare și echilibrare au contribuit la obținerea unor rezultate robuste și echilibrate.

---

### Instrucțiuni rapide pentru rulare (creare mediu virtual și instalare pachete)

1. **Creează un mediu virtual Python (recomandat):**

```bash
python3 -m venv venv
```

2. **Activează mediul virtual:**

- Pe Linux/macOS:
  ```bash
  source venv/bin/activate
  ```
- Pe Windows:
  ```bash
  venv\Scripts\activate
  ```

3. **Instalează pachetele necesare:**

```bash
pip3 install pandas numpy matplotlib seaborn scikit-learn imbalanced-learn
```

4. **Rulează scriptul principal:**

```bash
python3 depression_project_structured.py
```

---

