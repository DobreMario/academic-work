package entities.animals;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import entities.Entity;
import entities.ScannedEntity;
import entities.Water;
import entities.airs.Air;
import entities.plants.Plant;
import entities.plants.Plant.Maturity;
import grid.Grid;
import grid.GridEngine.ScannedLayer;
import grid.ScannedGrid;

/**
 * Abstract class representing general Animal behaviors.
 * Manages eating, moving, and interaction logic.
 */
public abstract class Animal extends Entity {

    /**
     * Enum for Animal State.
     */
    public enum State {
        HUNGRY, WELL_FED, SICK
    }

    private static final double ORGANIC_MATTER_PLANT_WATER = 0.8;
    private static final double ORGANIC_MATTER_PLANT = 0.5;
    private static final double ORGANIC_MATTER_WATER = 0.5;
    private static final double INTAKE_RATE = 0.08;
    private static final double PROBABILITY_BASE = 100.0;
    private static final double PROBABILITY_DIVISOR = 10.0;
    private static final int DIRECTIONS_COUNT = 4;
    private static final int PRIORITY_LEVELS = 4;
    private static final int PRIORITY_NONE = 3;
    private static final int MEMORY_ID = 4;

    protected State state;
    protected double organicMatterProduced;

    // Use List of Lists for Type Safety instead of List[]
    protected List<List<Pair>> neighborPriority;

    /**
     * Default constructor.
     */
    public Animal() {
        super();
        this.state = State.HUNGRY;
        this.organicMatterProduced = 0.0;
        initNeighborPriority();
    }

    /**
     * Parameterized constructor.
     *
     * @param name                     Name of the animal.
     * @param mass                     Mass of the animal.
     * @param robotAttackedProbability Probability of NOT being attacked.
     */
    public Animal(final String name,
            final double mass,
            final double robotAttackedProbability) {
        super(name, mass, robotAttackedProbability);
        this.state = State.HUNGRY;
        this.organicMatterProduced = 0.0;
        initNeighborPriority();
    }

    private void initNeighborPriority() {
        neighborPriority = new ArrayList<>(PRIORITY_LEVELS);
        for (int i = 0; i < PRIORITY_LEVELS; i++) {
            neighborPriority.add(new ArrayList<>());
        }
    }

    /**
     * Logic for animal eating behavior.
     * Consumes available resources (Plant, Water) and updates stats.
     *
     * @param scannedGrid The grid memory to check resources.
     * @param x          Current X position.
     * @param y          Current Y position.
     */
    public void eat(final ScannedGrid scannedGrid, final int x, final int y) {
        Plant plant = scannedGrid.getPlantAt(x, y);
        Water water = scannedGrid.getWaterAt(x, y);

        double massGained = 0.0;
        double organicGain = 0.0;

        if (water != null) {
            massGained += drinkWater(water);
            organicGain = (plant != null) ? ORGANIC_MATTER_PLANT_WATER : ORGANIC_MATTER_WATER;
        }

        if (plant != null) {
            massGained += eatPlant(plant);
            if (organicGain == 0.0) {
                organicGain = ORGANIC_MATTER_PLANT;
            }
        }

        if (massGained > 0) {
            applyMetabolicChanges(massGained, organicGain);
        }
    }

    private double drinkWater(final Water water) {
        double waterConsumed = Math.min(this.mass * INTAKE_RATE, water.getMass());
        water.setMass(water.getMass() - waterConsumed);
        return waterConsumed;
    }

    private double eatPlant(final Plant plant) {
        double plantMass = plant.getMass();
        plant.setMaturity(Maturity.DEAD);
        return plantMass;
    }

    private void applyMetabolicChanges(final double massGained, final double organicGain) {
        this.mass += massGained;
        this.mass = Math.round(this.mass * PROBABILITY_BASE) / PROBABILITY_BASE;
        this.organicMatterProduced += organicGain;
        this.state = State.WELL_FED;
    }

    /**
     * Logic for animal movement.
     * Scans surroundings, prioritizes moves, and executes the best move.
     *
     * @param grid       The main grid.
     * @param scannedGrid The scanned grid memory.
     * @param x          Current X position.
     * @param y          Current Y position.
     */
    public void animalMove(final Grid grid,
            final ScannedGrid scannedGrid,
            final int x,
            final int y) {
        this.state = State.HUNGRY;

        List<Pair> neighbors = getAdjacentCoordinates(x, y);
        prioritizeNeighbors(scannedGrid, neighbors);

        Pair bestPair = bestMove(scannedGrid, grid);

        if (bestPair != null) {
            performMoveTransaction(grid, scannedGrid, x, y, bestPair);
        }
        clearNeighborPriority();

        this.eat(scannedGrid, bestPair.getX(), bestPair.getY());

    }

    private List<Pair> getAdjacentCoordinates(final int x, final int y) {
        List<Pair> pairs = new ArrayList<>(DIRECTIONS_COUNT);
        pairs.add(new Pair(x, y + 1));
        pairs.add(new Pair(x + 1, y));
        pairs.add(new Pair(x, y - 1));
        pairs.add(new Pair(x - 1, y));
        return pairs;
    }

    private void prioritizeNeighbors(final ScannedGrid scannedGrid, final List<Pair> neighbors) {
        for (Pair pos : neighbors) {
            addToPriorityList(scannedGrid, pos);
        }
    }

    private void addToPriorityList(final ScannedGrid grid, final Pair pos) {
        if (!grid.isValidPosition(pos.getX(), pos.getY())) {
            return;
        }

        boolean hasPlant = grid.getPlantAt(pos.getX(), pos.getY()) != null;
        boolean hasWater = grid.getWaterAt(pos.getX(), pos.getY()) != null;

        int priorityIndex;
        if (hasPlant && hasWater) {
            priorityIndex = 0;
        } else if (hasPlant) {
            priorityIndex = 1;
        } else if (hasWater) {
            priorityIndex = 2;
        } else {
            priorityIndex = PRIORITY_NONE;
        }
        neighborPriority.get(priorityIndex).add(pos);
    }

    private void performMoveTransaction(final Grid grid,
            final ScannedGrid scannedGrid,
            final int currX,
            final int currY,
            final Pair targetPos) {
        int targetX = targetPos.getX();
        int targetY = targetPos.getY();

        grid.addAnimal(targetX, targetY, this);

        ScannedEntity scanned = scannedGrid.getEntity(currX, currY, ScannedLayer.ANIMAL);
        scannedGrid.addScannedEntity(targetX, targetY, ScannedLayer.ANIMAL, scanned);

        grid.removeAnimal(currX, currY);
        scannedGrid.removeAnimal(currX, currY);
    }

    private void clearNeighborPriority() {
        for (List<Pair> list : neighborPriority) {
            list.clear();
        }
    }

    /**
     * Double dispatch interaction.
     *
     * @param other Interacting entity.
     */
    @Override
    public void interactWith(final Entity other) {
        other.interactWithAnimal(this);
    }

    /**
     * Interaction with air. Checks for toxicity.
     *
     * @param air Air entity.
     */
    @Override
    public void interactWithAir(final Air air) {
        if (air.isToxic()) {
            this.state = State.SICK;
        }
    }

    /**
     * Exports the animal's data to a JSON object.
     *
     * @return The JSON node representing the animal.
     */
    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("type", this.getType());
        node.put("name", this.getName());
        node.put("mass", this.getMass());
        return node;
    }

    /**
     * Gets the main category for the entity registry.
     *
     * @return The string identifier "animals".
     */
    @Override
    public String getMainClass() {
        return "animals";
    }

    /**
     * Gets the unique memory identifier for animals.
     *
     * @return The memory key (4).
     */
    @Override
    public int getMemoryKey() {
        return MEMORY_ID;
    }

    /**
     * Gets the current physiological state of the animal.
     *
     * @return The current State.
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the physiological state of the animal.
     *
     * @param state The new state to set.
     */
    public void setState(final State state) {
        this.state = state;
    }

    /**
     * Calculates the probability of the animal interacting with a robot.
     *
     * @return The normalized probability value.
     */
    @Override
    public double getRobotProbability() {
        return (PROBABILITY_BASE - robotProbability) / PROBABILITY_DIVISOR;
    }

    /**
     * Gets the intake rate for consumption.
     *
     * @return The intake rate constant.
     */
    public static double getIntakeRate() {
        return INTAKE_RATE;
    }

    /**
     * Sorts a list of positions based on the quality of water at those locations.
     *
     * @param grid      The scanned grid containing water data.
     * @param positions The list of positions to sort.
     */
    protected void sortByWaterQuality(final ScannedGrid grid, final List<Pair> positions) {
        positions.sort((p1, p2) -> {
            Water w1 = grid.getWaterAt(p1.getX(), p1.getY());
            Water w2 = grid.getWaterAt(p2.getX(), p2.getY());

            if (w1 == null && w2 == null) {
                return 0;
            }
            if (w1 == null) {
                return -1;
            }
            if (w2 == null) {
                return 1;
            }
            return Double.compare(w1.calculateWaterQuality(), w2.calculateWaterQuality());
        });
    }

    /**
     * Determines the best move based on prioritized neighbor lists.
     *
     * @param scannedGrid Scanned memory grid.
     * @param grid        Main grid.
     * @return Best coordinate pair or null if no move is valid.
     */
    protected Pair bestMove(final ScannedGrid scannedGrid, final Grid grid) {
        sortByWaterQuality(scannedGrid, neighborPriority.get(0));
        sortByWaterQuality(scannedGrid, neighborPriority.get(2));

        for (int i = 0; i < PRIORITY_LEVELS; i++) {
            for (Pair pos : neighborPriority.get(i)) {
                if (!grid.isValidPosition(pos.getX(), pos.getY())) {
                    continue;
                }
                if (grid.getAnimalAt(pos.getX(), pos.getY()) == null) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Gets the amount of organic matter produced by the animal.
     *
     * @return The amount of organic matter.
     */
    public double getOrganicMatterProduced() {
        return this.organicMatterProduced;
    }

    /**
     * Sets the amount of organic matter produced by the animal.
     *
     * @param organicMatterProduced The new amount.
     */
    public void setOrganicMatterProduced(final double organicMatterProduced) {
        this.organicMatterProduced = organicMatterProduced;
    }
}
