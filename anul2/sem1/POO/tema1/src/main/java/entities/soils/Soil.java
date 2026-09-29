package entities.soils;

import entities.Entity;
import entities.Water;
import entities.animals.Animal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Abstract base for soil entities.
 * Holds common soil properties and utilities used by concrete soil types.
 */
public abstract class Soil extends Entity {

    private static final double WATER_RETENTION_GAIN = 0.1;
    private static final double ROUNDING_FACTOR = 100.0;
    private static final int MEMORY_ID = 1;

    protected double nitrogen;
    protected double waterRetention;
    protected double soilpH;
    protected double organicMatter;
    protected double soilScore;

    /**
     * Default constructor.
     */
    public Soil() {
        super();
        this.nitrogen = 0.0;
        this.waterRetention = 0.0;
        this.soilpH = 0.0;
        this.organicMatter = 0.0;
        this.soilScore = 0.0;
    }

    /**
     * Parameterized constructor.
     *
     * @param name           Name of the soil.
     * @param mass           Mass of the soil.
     * @param nitrogen       Nitrogen level.
     * @param waterRetention Water retention level.
     * @param soilpH         pH level of the soil.
     * @param organicMatter  Organic matter content.
     */
    public Soil(final String name,
            final double mass,
            final double nitrogen,
            final double waterRetention,
            final double soilpH,
            final double organicMatter) {
        super(name, mass, 0.0);
        this.nitrogen = nitrogen;
        this.waterRetention = waterRetention;
        this.soilpH = soilpH;
        this.organicMatter = organicMatter;
        updateStatus();
    }

    protected abstract double calculateSoilQuality();

    protected abstract double calculateRobotStuckProbability();

    /**
     * Recalculates soil metrics:
     * - normalizes `soilScore` (0..100)
     * - updates `quality` based on `soilScore`
     * - updates `robotProbability` using the concrete calculation
     */
    @Override
    public void updateStatus() {
        this.soilScore = normalizeScore(calculateSoilQuality());
        this.quality = determineQuality(this.soilScore);
        this.robotProbability = normalizeScore(calculateRobotStuckProbability());
    }

    /**
     * Double dispatch interaction.
     *
     * @param other The interacting entity.
     */
    @Override
    public void interactWith(final Entity other) {
        other.interactWithSoil(this);
    }

    /**
     * Interaction when water is applied to soil: increases water retention
     * and updates dependent status fields.
     *
     * @param water Water entity interacting with this soil
     */
    @Override
    public void interactWithWater(final Water water) {
        this.waterRetention += WATER_RETENTION_GAIN;
        this.waterRetention = normalizeScore(this.waterRetention);
        updateStatus();
    }

    /**
     * Interaction with an animal: if the animal is well fed it deposits
     * organic matter into the soil and resets the animal's produced value.
     *
     * @param animal the interacting animal
     */
    @Override
    public void interactWithAnimal(final Animal animal) {
        if (animal.getState() == Animal.State.WELL_FED) {
            this.organicMatter += animal.getOrganicMatterProduced();
            animal.setOrganicMatterProduced(0.0);
            this.organicMatter = normalizeScore(this.organicMatter);
            updateStatus();
        }
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
        node.put("nitrogen", this.nitrogen);
        node.put("waterRetention", this.waterRetention);
        node.put("soilpH", this.soilpH);
        node.put("organicMatter", this.organicMatter);
        node.put("soilQuality", this.soilScore);
        return node;
    }

    /**
     * Gets the main class category.
     *
     * @return "soil".
     */
    @Override
    public String getMainClass() {
        return "soil";
    }

    /**
     * Calculates and gets the robot stuck probability.
     *
     * @return probability value.
     */
    @Override
    public double getRobotProbability() {
        return calculateRobotStuckProbability();
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
     * Gets nitrogen level.
     *
     * @return nitrogen value.
     */
    public double getNitrogen() {
        return nitrogen;
    }

    /**
     * Sets nitrogen content and refreshes derived status fields.
     *
     * @param nitrogen new nitrogen value
     */
    public void setNitrogen(final double nitrogen) {
        this.nitrogen = nitrogen;
        updateStatus();
    }

    /**
     * Gets water retention level.
     *
     * @return water retention value.
     */
    public double getWaterRetention() {
        return waterRetention;
    }

    /**
     * Sets water retention (rounded to 2 decimals) and updates status.
     *
     * @param waterRetention new water retention value
     */
    public void setWaterRetention(final double waterRetention) {
        this.waterRetention = waterRetention;
        this.waterRetention = Math.round(this.waterRetention * ROUNDING_FACTOR) / ROUNDING_FACTOR;
        updateStatus();
    }

    /**
     * Gets soil pH.
     *
     * @return pH value.
     */
    public double getSoilpH() {
        return soilpH;
    }

    /**
     * Sets soil pH (rounded to 2 decimals) and updates status.
     *
     * @param soilpH new soil pH value
     */
    public void setSoilpH(final double soilpH) {
        this.soilpH = soilpH;
        this.soilpH = Math.round(this.soilpH * ROUNDING_FACTOR) / ROUNDING_FACTOR;
        updateStatus();
    }

    /**
     * Gets organic matter level.
     *
     * @return organic matter value.
     */
    public double getOrganicMatter() {
        return organicMatter;
    }

    /**
     * Sets organic matter (rounded to 2 decimals) and updates status.
     *
     * @param organicMatter new organic matter value
     */
    public void setOrganicMatter(final double organicMatter) {
        this.organicMatter = organicMatter;
        this.organicMatter = Math.round(this.organicMatter * ROUNDING_FACTOR) / ROUNDING_FACTOR;
        updateStatus();
    }

    /**
     * Gets the calculated soil score.
     *
     * @return soil score.
     */
    public double getSoilScore() {
        return soilScore;
    }

    /**
     * Sets the soil score manually.
     *
     * @param soilScore new score.
     */
    public void setSoilScore(final double soilScore) {
        this.soilScore = soilScore;
    }

    /**
     * Gets the current quality enum.
     *
     * @return Quality enum.
     */
    @Override
    public Quality getQuality() {
        return quality;
    }

    /**
     * Sets the quality enum.
     *
     * @param quality new Quality.
     */
    @Override
    public void setQuality(final Quality quality) {
        this.quality = quality;
    }
}
