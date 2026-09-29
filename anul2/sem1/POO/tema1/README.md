# Project 1: Ecosystem Simulation 🌱🛰️

**Name:** Mario-Sebastian Dobre  
**Group:** 324CA

## 1. Description and Objectives 🎯

This project implements a complex simulation of an ecosystem structured into multiple layers (soil, plants, animals, water, air), where entities interact according to domain-specific rules, and the results are exported in JSON format. The main objective was to design a modular, extensible architecture that respects OOP principles (encapsulation, polymorphism, abstraction).

## 2. Architecture and Design 🧱

The system is built on a polymorphic class hierarchy, with the abstract class `Entity` at the base, defining the shared protocol (identity, mass, robot probability, `Quality`, JSON export).

### A. Design Patterns 🧩

1. **Double Dispatch (Interaction Mechanism)**  
   * **Motivation:** In a heterogeneous grid, interaction between two entities depends on the concrete type of *both*. Using `instanceof` would make the code rigid and unscalable.  
   * **Implementation:** The method `Entity.interactWith(Entity other)` forwards the call to the appropriate visitor-specific method (e.g., `interactWithAnimal`, `interactWithPlant`).  
   * **Benefit:** Runtime polymorphism chooses the correct method dynamically, eliminating type checks and preserving extensibility.

2. **Factory Pattern**  
   * Used in the `factory` package (`AnimalFactory`, `PlantFactory`, etc.) to map input types to concrete instances, separating parsing logic from object creation.

3. **Hybrid Singleton–Classic Design (Registry Pattern)**  
   * **Context:** Implemented inside the `airs/Air` hierarchy.  
   * **Implementation:** Although each air cell on the map is a distinct instance (classic instantiation), each subclass maintains a static list of its instances (behaving like a Singleton registry).  
   * **Purpose:** This hybrid approach enables centralized management of global meteorological phenomena while preserving the individuality of each grid cell.

### B. Data Structures and Optimizations ⚙️

* **Map and Memory Storage (`TreeMap`):**  
  The main map (`Grid`) and the robot’s memory (`ScannedGrid`) both use `TreeMap<Layer, Entity>[][]`.  
  * **Determinism and Ordering:** The Red–Black Tree structure behind `TreeMap` guarantees ordered iteration of keys. This ensures deterministic simulations: entities are always processed in the same order (e.g., from (0,0) to (N,N)), regardless of insertion order.

## 3. Implementation Details (Entities)

The business logic is located in the `entities` package, following separation of concerns:

* **`animals/Animal` (abstract):**  
  Handles internal states (`HUNGRY`, `WELL_FED`, `SICK`) and basic interactions (feeding on plants/animals, drinking water, producing `organicMatter` for soil).  
  * **Subclasses:** `Carnivores`, `Herbivores`, `Omnivores`, `Parasites`, `Detritivores`.  
  * Each subclass specifies survival behavior (the `bestMove` algorithm for prioritizing food/water) and parameters such as diet and attack-avoidance probability.

* **`plants/Plant` (abstract):**  
  Defines lifecycle logic based on maturity (`YOUNG` → `MATURE` → `DEAD`). Growth is influenced by interactions with `Water` and `Soil`.  
  * **Subclasses:** `Algae`, `Ferns`, `FloweringPlants`, `GymnospermsPlants`, `Mosses`.  
  * Each type adjusts oxygen production and the probability of blocking robots (`ROBOT_STUCK_PROBABILITY`).

* **`soils/Soil`:**  
  Determines fertility and water retention.  
  * **Subclasses:** `DesertSoil`, `ForestSoil`, `GrasslandSoil`, `SwampSoil`, `TundraSoil`.  
  * Each subclass calculates a robot-stuck probability based on unique properties (e.g., `SwampSoil` depends on `waterLogging`, `TundraSoil` on `permafrostDepth`).

* **`airs/Air`:**  
  Models climatic characteristics.  
  * **Subclasses:** `DesertAir`, `MountainAir`, `PolarAir`, `TemperateAir`, `TropicalAir`.  
  * **Optimization (Static Registry):**  
    To efficiently handle global weather events (storms, frost), each subclass keeps a static list (`static List<Instance>`).  
    This allows applying weather effects only to relevant instances, avoiding full-grid scans.  
    Complexity is reduced from `O(GridSize)` to `O(AirInstances)`.

* **`Water` (final):**  
  Manages chemical properties (pH, salinity, purity, turbidity). Computes a composite score mapped to a `Quality` enum.

## 4. Execution Flow & Commands (Command-Driven Architecture)

Program execution is not automatic; it is strictly controlled by a sequence of commands processed by `SimulationController`, with the Robot as the main exploration agent.

### A. Implemented Commands

1. **Simulation Control:**  
   * `startSimulation` – Initializes the grid, loads entities, prepares the robot.  
   * `endSimulation` – Stops execution and finalizes output.

2. **Robot Actions (Exploration & Energy):**  
   * `moveRobot` – Moves the robot to a new coordinate.  
   * `scanObject` – Scans an entity and stores it in the memory (`ScannedGrid`). Required for most interactions.  
   * `rechargeBattery` – Recharges the battery.  
   * `getEnergyStatus` – Prints battery level and usage.  
   * `learnFact` – Adds new abstract knowledge to the robot’s knowledge base.

3. **Environment Manipulation:**  
   * `changeWeatherConditions` – Triggers global weather events, using the Air Registry for instant propagation.  
   * `improveEnvironment` – Robot improves environmental quality (e.g., water purification).

4. **Reporting & Debugging:**  
   * `printMap` – Prints the real map (Ground Truth).  
   * `printEnvConditions` – Shows abiotic factors (weather, soil).  
   * `printKnowledgeBase` – Exports the robot’s full knowledge base.

### B. Interaction Logic (Scanning Rule)

A distinctive feature of this project is that interactions depend on the robot’s memory.

* **General Rule:**  
  Most interactions occur only between entities that have been scanned and stored in `ScannedGrid`.

* **Exception (Animal Instinct):**  
  * **Predation:** Animals can hunt and eat other animals even if they have not been scanned (simulating instinctual behavior).  
  * **Resource Consumption:** Animals can eat plants or drink water only if those entities have been scanned.

## 5. AI Resources and Usage 🤖

AI tools were used responsibly and transparently:

* **Google Gemini:** Used for idea testing and validating formulas/algorithms.  
* **ChatGPT:** Used for architectural guidance; suggested Double Dispatch.  
* **GitHub Copilot:** Used for autocomplete, constructor generation, and refactoring constants.

## 6. Running & Testing the Simulation 🧪

Testing and verification are centralized in the `TestRunner` class.

It automates scenario testing by iterating through input files and comparing simulation output with `.ref` reference files.

**Run methods:**

* **In IDE:** Right-click `TestRunner` → Run.  
* **Command line:**  
  ```bash
  java TestRunner
