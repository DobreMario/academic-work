# Ticket Management System

Un sistem backend de gestiune a tichetelor inspirat de funcționalitățile GitHub (Issues, Milestones, Assignees), implementat în Java. Aplicația simulează un server care procesează comenzi, gestionează o ierarhie de utilizatori și generează rapoarte complexe de performanță și stabilitate.

## 🏗️ Arhitectură și Organizare

Proiectul este structurat pe pachete ce separă clar responsabilitățile (Separation of Concerns), asigurând un cod modular, extensibil și ușor de testat:

* **`main.commands`**: Implementarea efectivă a acțiunilor sistemului. Conține logica de validare și execuție, decuplată de apelant.
* **`main.databases` & `main.engines`**: Stratul de persistență (în memorie) și logica de business. `Server` acționează ca punct central de acces, în timp ce `TicketEngine` și `MilestoneEngine` gestionează operațiunile complexe.
* **`main.metrics`**: Un modul dedicat calculelor statistice. Include algoritmi pentru evaluarea riscului, eficienței și performanței developerilor.
* **`main.tickets`, `main.users`, `main.milestones`**: Clasele de entități (Model) care definesc datele.
* **`main.fileio`**: DTO-uri (Data Transfer Objects) utilizate pentru maparea datelor de intrare (JSON).

---

## 🎨 Design Patterns Utilizate

Implementarea se bazează pe principii **SOLID, DRY, KISS** și utilizează extensiv Design Patterns pentru a rezolva probleme recurente de arhitectură.

### 1. Command Pattern
* **Unde:** Pachetul `main.commands`.
* **Rol:** Decuplează obiectul care invocă operația (`CommandRunner`) de logica de execuție.
* **Implementare:**
    * Interfața `Command` definește contractul generic `execute(...)`.
    * `CommandRunner` acționează ca *Invoker*, mapând string-uri de input la instanțe concrete (ex: `AddComment`, `AssignTicket`).
    * Permite adăugarea de noi comenzi fără a modifica codul existent ("Open/Closed Principle").

### 2. Strategy Pattern
Utilizat intens în patru contexte diferite pentru a permite schimbarea dinamică a algoritmilor:
* **Rapoarte (`main.metrics`):** Calculul scorurilor pentru tichete (Risk, Impact, Efficiency) este încapsulat în strategii, permițând `MetricCalculator` să rămână agnostic la formula matematică specifică.
* **Performanță (`main.metrics.performance`):** Formulele de calcul pentru developeri diferă în funcție de senioritate (`JuniorPerformanceStrategy`, `Mid...`, `Senior...`).
* **Căutare (`main.commands.searchHelper.strategies`):** Logica de căutare este separată pentru Tichete vs. Utilizatori.
* **Acces Date (`main.users.Strategy`):** Modul în care un Manager vede tichetele diferă de cel al unui Developer.

### 3. Singleton Pattern
* **Unde:** `main.databases.Server`.
* **Rol:** Asigură existența unei singure instanțe a serverului care deține starea globală a aplicației (baze de date pentru utilizatori, tichete și milestone-uri) pe durata execuției setului de comenzi.

### 4. Builder Pattern
* **Unde:** `main.tickets.TicketBuilder`.
* **Rol:** Simplifică crearea instanțelor complexe de `Ticket`. Deoarece un tichet are mulți parametri (dintre care unii opționali sau cu valori implicite), Builder-ul oferă o metodă fluidă și lizibilă de construcție a obiectelor.

### 5. Factory Pattern
* **Unde:** Clasele `Factory` din pachetele `users`, `tickets`, `milestones`, `strategies`.
* **Rol:** Centralizează logica de instanțiere. Clientul cere un "Tichet" sau o "Strategie" bazată pe un input String (ex: "BUG"), iar Factory returnează instanța corectă a subclasei, ascunzând detaliile de implementare.

### 6. Observer Pattern
* **Unde:** Sistemul de notificări.
* **Rol:** `ServerUser` implementează funcționalitatea de `Observer`. Când starea unei entități observabile se schimbă (ex: un tichet este alocat, un milestone este creat), utilizatorii relevanți sunt notificați automat, fără ca entitatea care emite notificarea să cunoască detalii despre useri.

### 7. Composite Pattern
* **Unde:** `main.commands.searchHelper.FilterSet`.
* **Rol:** Gestionează filtrarea avansată în cadrul comenzii `Search`. `FilterSet` implementează interfața `SearchFilter` și conține o listă de alte filtre. Acest lucru permite compunerea ierarhică a filtrelor (logica AND) și tratarea unui grup de filtre la fel ca un filtru individual.

---

## 🔄 Fluxul de Execuție

1.  **Input Parsing:** Datele sunt citite din fișiere JSON și deserializate în obiecte `CommandInput` folosind Jackson.
2.  **Invoker (Routing):** Clasa `App` pasează inputul către `CommandRunner`, care identifică comanda corespunzătoare.
3.  **Command Execution:**
    * Comanda validează permisiunile utilizatorului apelând `CommandUtils`.
    * Interacționează cu `Server`, `TicketEngine` sau `MilestoneEngine` pentru a modifica starea sistemului.
    * Dacă este necesar, declanșează notificări prin mecanismul Observer.
4.  **Result Aggregation:** Rezultatul (sau mesajul de eroare) este scris într-o structură JSON (`ArrayNode`), care este serializată la finalul execuției.

---

## 📊 Funcționalități Principale

### Managementul Tichetelor
* **Ciclu de viață:** OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED.
* **Istoric:** Fiecare acțiune pe tichet este logată pentru audit.
* **Validări:** Verificări stricte de permisiuni (Role-based), expertiză și senioritate la asignare.

### Milestone-uri
* **Dependențe:** Un milestone poate bloca alt milestone. Deblocarea se face automat la finalizarea celui părinte.
* **Tracking:** Calcul automat al procentului de finalizare și detectarea întârzierilor.

### Rapoarte Avansate
* **Performance Report:** Evaluează developerii pe baza unui algoritm complex ce ia în calcul numărul de tichete, prioritatea, timpul de rezolvare și factorul de diversitate (pentru juniori).
* **App Stability:** Determină stabilitatea aplicației (STABLE, UNSTABLE) corelând riscul tichetelor deschise cu impactul asupra clienților.
* **Risk & Efficiency:** Analizează riscul potențial al bug-urilor și eficiența echipei în rezolvarea lor.

### Căutare și Filtrare Avansată
* **Strategii Dedicate:** Motoare de căutare separate pentru Tichete și Utilizatori, implementate prin Strategy Pattern.
* **Filtrare Compozită:** Suport pentru criterii multiple simultane (AND logic).
    * *Tichete:* Filtrare după status, prioritate, assignee, dată, sau cuvinte cheie în descriere.
    * *Developeri:* Identificare rapidă a experților (ex: senioritate + domeniu de expertiză) pentru alocare optimă.

### Sistem de Notificări și Sincronizare
* **Evenimente Automate:** Utilizatorii primesc notificări contextuale (ex: "Milestone Unblocked", "Ticket Assigned", "Deadline Warning").
* **Time Travel Simulation:** Serverul sincronizează automat starea sistemului la fiecare comandă, simulând trecerea timpului pentru a actualiza progresul milestone-urilor și a detecta întârzierile.

### Controlul Accesului (RBAC)
* **Ierarhie de Roluri:** Sistem bazat pe roluri: **Manager** (organizare, rapoarte), **Developer** (execuție tichete), **Reporter** (creare ticketelor).
* **Securitate:** Fiecare comandă trece printr-un strat de validare a permisiunilor înainte de execuție, asigurând integritatea datelor (cine are dreptul sa execute comanta 'y').

## 🧠 Gândirea din Spate (Notă către Corector)

Am încercat să privesc enunțul puțin diferit față de tema anterioară. Am adăugat acele clase speciale de tip `Server...` (Wrappers), deoarece m-am gândit că, la locul de muncă, nu voi putea întotdeauna să modific codul primit (Legacy/External DTOs). Așa că am vrut să mă antrenez să nu îmi las mintea să modifice structura obiectelor exact cum sunt date în enunț.

Totuși, problema cea mai mare a fost raportarea la baremul de corectare. M-am tot gândit dacă ceea ce fac este conform principiilor POO sau nu.

**De aceea, doresc un feedback din acest punct de vedere:**
1.  Obiectele "plate" (fără logică de business în ele) sunt permise/încurajate în astfel de teme? Este această abordare (Data vs. Logic) considerată POO validă sau am încălcat regulile lăsând obiectele de bază simple?
2.  Am reușit să sparg dependențele circulare din cod prin această arhitectură? (Simt că am rezolvat o mare parte, dar nu sunt sigur dacă am reușit pe toate).
3.  Ce aș putea să îmbunătățesc la această abordare?

Mulțumesc!

## 🤖 Folosirea AI / LLM

În elaborarea acestei teme am utilizat modele AI, în special **GeminiAI**, pe care l-am folosit pentru validarea ideilor și îmbunătățirea calității codului.

Contribuția concretă a AI-ului a fost în următoarele arii:
1.  **Alegerea Design Pattern-urilor:** Pentru implementarea sistemului de filtrare (`Search`), inițial intenționam să folosesc un *Builder Pattern*. AI-ul mi-a sugerat **Composite Pattern** ca fiind o soluție mai naturală pentru combinarea filtrelor, idee pe care am implementat-o.
2.  **Rezolvarea problemelor de Coding Style:** M-a ajutat să rezolv rapid erorile semnalate de Checkstyle, în special extragerea valorilor hardcodate ("Magic Numbers") în constante (`static final`).
3.  **Feedback pe Arhitectură (POO):** Am folosit modelul pentru a primi feedback constant pe codul scris, asigurându-mă că direcția aleasă (cu Wrappers și Engines) respectă principiile Programării Orientate pe Obiecte.
4.  **Refactorizare și Optimizare:** Am primit sugestia de a înlocui buclele `for` clasice cu **Java Streams API** (`.stream()`), ceea ce a făcut codul mai curat.