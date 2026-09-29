package entities.plants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import entities.Entity;
import entities.Water;
import entities.soils.Soil;
import entities.animals.Animal;

/**
 * Abstract class representing a Plant entity.
 * Manages maturity stages, growth rates, and interactions.
 */
public abstract class Plant extends Entity {

    /**
     * Enum representing the maturity stages of a plant.
     */
    public enum Maturity {
        YOUNG, MATURE, OLD, DEAD
    }

    private static final double GROWTH_INCREMENT = 0.2;
    private static final double GROWTH_THRESHOLD = 1.0;
    private static final double OXYGEN_BONUS_YOUNG = 0.2;
    private static final double OXYGEN_BONUS_MATURE = 0.7;
    private static final double OXYGEN_BONUS_OLD = 0.4;
    private static final int MEMORY_ID = 3;

    protected Maturity maturity;
    protected double oxygenFromPlant;
    private double growthRate;

    /**
     * Default constructor.
     */
    public Plant() {
        super();
        this.maturity = Maturity.YOUNG;
        this.oxygenFromPlant = 0.0;
    }

    /**
     * Parameterized constructor.
     *
     * @param name                  Name of the plant.
     * @param mass                  Mass of the plant.
     * @param robotStuckProbability Probability of a robot getting stuck.
     */
    public Plant(final String name,
            final double mass,
            final double robotStuckProbability) {
        super(name, mass, robotStuckProbability);
        this.maturity = Maturity.YOUNG;
    }

    /**
     * Copy constructor.
     *
     * @param other Plant to copy from.
     */
    public Plant(final Plant other) {
        super(other);
        this.maturity = other.maturity;
        this.oxygenFromPlant = other.oxygenFromPlant;
    }

    /**
     * Double dispatch interaction.
     *
     * @param other The interacting entity.
     */
    @Override
    public void interactWith(final Entity other) {
        other.interactWithPlant(this);
    }

    /**
     * Interaction with soil. Increases growth rate.
     *
     * @param soil The soil entity.
     */
    @Override
    public void interactWithSoil(final Soil soil) {
        this.growthRate += GROWTH_INCREMENT;
    }

    /**
     * Interaction with water. Increases growth rate.
     *
     * @param water The water entity.
     */
    @Override
    public void interactWithWater(final Water water) {
        this.growthRate += GROWTH_INCREMENT;
    }

    /**
     * Interaction with animal. The plant dies (is eaten or trampled).
     *
     * @param animal The animal entity.
     */
    @Override
    public void interactWithAnimal(final Animal animal) {
        this.maturity = Maturity.DEAD;
    }

    /**
     * Exports entity data to JSON.
     *
     * @return JSON ObjectNode.
     */
    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("type", this.getType());
        node.put("name", this.name);
        node.put("mass", this.mass);
        return node;
    }

    /**
     * Gets the main class category.
     *
     * @return "plants".
     */
    @Override
    public String getMainClass() {
        return "plants";
    }

    /**
     * Gets the memory key identifier.
     *
     * @return The memory key.
     */
    @Override
    public int getMemoryKey() {
        return MEMORY_ID;
    }

    /**
     * Calculates the oxygen bonus based on maturity.
     *
     * @return The bonus value.
     */
    protected final double getMaturityBonus() {
        return switch (this.maturity) {
            case YOUNG -> OXYGEN_BONUS_YOUNG;
            case MATURE -> OXYGEN_BONUS_MATURE;
            case OLD -> OXYGEN_BONUS_OLD;
            default -> 0.0;
        };
    }

    /**
     * Calculates total generated oxygen.
     *
     * @return Oxygen amount.
     */
    public final double generatedOxygen() {
        return this.oxygenFromPlant + getMaturityBonus();
    }

    /**
     * Advances the plant to the next maturity stage if growth threshold is met.
     */
    public final void nextMaturityStage() {
        if (this.growthRate >= GROWTH_THRESHOLD) {
            switch (this.maturity) {
                case YOUNG -> {
                    this.maturity = Maturity.MATURE;
                    growthRate = 0.0;
                }
                case MATURE -> {
                    this.maturity = Maturity.OLD;
                    growthRate = 0.0;
                }
                case OLD -> {
                    this.maturity = Maturity.DEAD;
                    growthRate = 0.0;
                }
                default -> {
                }
            }
        }
    }

    /**
     * Gets current maturity.
     *
     * @return Maturity enum.
     */
    public final Maturity getMaturity() {
        return this.maturity;
    }

    /**
     * Sets maturity.
     *
     * @param maturity New maturity.
     */
    public final void setMaturity(final Maturity maturity) {
        this.maturity = maturity;
    }

    /**
     * Gets base oxygen from plant.
     *
     * @return Oxygen value.
     */
    public final double getOxygenFromPlant() {
        return this.oxygenFromPlant;
    }

    /**
     * Sets base oxygen from plant.
     *
     * @param oxygenFromPlant New oxygen value.
     */
    public final void setOxygenFromPlant(final double oxygenFromPlant) {
        this.oxygenFromPlant = oxygenFromPlant;
    }

    /**
     * Gets growth rate.
     *
     * @return Growth rate.
     */
    public final double getGrowthRate() {
        return this.growthRate;
    }

    /**
     * Sets growth rate.
     *
     * @param growthRate New growth rate.
     */
    public final void setGrowthRate(final double growthRate) {
        this.growthRate = growthRate;
    }

    /**
     * Checks if the plant is dead.
     *
     * @return True if dead.
     */
    public final boolean isDead() {
        return this.maturity == Maturity.DEAD;
    }
}
