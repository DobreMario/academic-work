package entities;

import com.fasterxml.jackson.databind.node.ObjectNode;

import entities.animals.Animal;
import entities.plants.Plant;
import entities.soils.Soil;
import entities.airs.Air;

/**
 * Abstract base class for all simulation entities.
 */
public abstract class Entity {

    private static final double MAX_SCORE = 100.0;
    private static final double ROUNDING_FACTOR = 100.0;
    private static final double THRESHOLD_GOOD = 70.0;
    private static final double THRESHOLD_MODERATE = 40.0;

    /**
     * Enum for Entity Quality.
     */
    public enum Quality {
        GOOD, MODERATE, POOR
    }

    protected String name;
    protected double mass;
    protected double robotProbability;
    protected Quality quality;

    /**
     * Default constructor.
     */
    public Entity() {
        this.name = "";
        this.mass = 0.0;
        this.robotProbability = 0.0;
        this.quality = Quality.GOOD;
    }

    /**
     * Parameterized constructor.
     *
     * @param name             Name of the entity.
     * @param mass             Mass of the entity.
     * @param robotProbability Probability of being a robot.
     */
    public Entity(final String name, final double mass, final double robotProbability) {
        this.name = name;
        this.mass = mass;
        this.robotProbability = robotProbability;
        this.quality = Quality.GOOD;
    }

    /**
     * Copy constructor.
     *
     * @param other Entity to copy.
     */
    public Entity(final Entity other) {
        this.name = other.name;
        this.mass = other.mass;
        this.robotProbability = other.robotProbability;
        this.quality = other.quality;
    }

    /**
     * Abstract interaction method.
     *
     * @param other Other entity.
     */
    public abstract void interactWith(Entity other);

    /**
     * Interaction with Plant.
     *
     * @param plant Plant entity.
     */
    public void interactWithPlant(final Plant plant) {
    }

    /**
     * Interaction with Animal.
     *
     * @param other Animal entity.
     */
    public void interactWithAnimal(final Animal other) {
    }

    /**
     * Interaction with Air.
     *
     * @param other Air entity.
     */
    public void interactWithAir(final Air other) {
    }

    /**
     * Interaction with Water.
     *
     * @param other Water entity.
     */
    public void interactWithWater(final Water other) {
    }

    /**
     * Interaction with Soil.
     *
     * @param other Soil entity.
     */
    public void interactWithSoil(final Soil other) {
    }

    /**
     * Gets JSON representation.
     *
     * @return ObjectNode.
     */
    public abstract ObjectNode getEntityNode();

    /**
     * Gets memory key.
     *
     * @return int key.
     */
    public abstract int getMemoryKey();

    /**
     * Updates entity status.
     */
    public void updateStatus() {
    }

    /**
     * Returns string representation of quality.
     *
     * @return String quality.
     */
    public String toStringQuality() {
        return switch (this.quality) {
            case GOOD -> "good";
            case MODERATE -> "moderate";
            case POOR -> "poor";
        };
    }

    /**
     * Normalizes score between 0 and 100.
     *
     * @param score Raw score.
     * @return Normalized score.
     */
    protected double normalizeScore(final double score) {
        double normalizedScore = Math.max(0, Math.min(MAX_SCORE, score));
        return Math.round(normalizedScore * ROUNDING_FACTOR) / ROUNDING_FACTOR;
    }

    /**
     * Determines quality based on score.
     *
     * @param score Input score.
     * @return Quality enum.
     */
    protected Quality determineQuality(final double score) {
        double finalScore = normalizeScore(score);
        if (finalScore >= THRESHOLD_GOOD) {
            return Quality.GOOD;
        } else if (finalScore >= THRESHOLD_MODERATE) {
            return Quality.MODERATE;
        } else {
            return Quality.POOR;
        }
    }

    /**
     * Gets name.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name New name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets mass.
     *
     * @return mass.
     */
    public double getMass() {
        return mass;
    }

    /**
     * Sets mass.
     *
     * @param mass New mass.
     */
    public void setMass(final double mass) {
        this.mass = Math.round(mass * ROUNDING_FACTOR) / ROUNDING_FACTOR;
    }

    /**
     * Gets robot probability.
     *
     * @return probability.
     */
    public double getRobotProbability() {
        return robotProbability;
    }

    /**
     * Sets robot probability.
     *
     * @param robotProbability New probability.
     */
    public void setRobotProbability(final double robotProbability) {
        this.robotProbability = robotProbability;
    }

    /**
     * Gets quality.
     *
     * @return quality.
     */
    public Quality getQuality() {
        return quality;
    }

    /**
     * Sets quality.
     *
     * @param quality New quality.
     */
    public void setQuality(final Quality quality) {
        this.quality = quality;
    }

    /**
     * Gets type string.
     *
     * @return Class simple name.
     */
    public String getType() {
        return getClass().getSimpleName();
    }

    /**
     * Gets main class identifier.
     *
     * @return Identifier string.
     */
    public abstract String getMainClass();
}
