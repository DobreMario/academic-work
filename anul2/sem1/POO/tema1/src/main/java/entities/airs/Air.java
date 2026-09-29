package entities.airs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import entities.Entity;
import entities.Water;
import entities.plants.Plant;
import entities.plants.Plant.Maturity;

/**
 * Abstract class representing the Air entity.
 * Defines interaction logic and status updates.
 */
public abstract class Air extends Entity {

    private static final double HUMIDITY_GAIN = 0.10;
    private static final double ROUNDING_FACTOR = 100.0;
    private static final double MAX_PERCENTAGE = 100.0;
    private static final double TOXICITY_THRESHOLD_RATIO = 0.8;

    protected double humidity;
    protected double temperature;
    protected double oxygenLevel;
    protected double airQualityScore;
    protected double toxicAQ;
    protected int maxScore;

    /**
     * Default constructor.
     */
    public Air() {
        super();
        this.humidity = 0.0;
        this.temperature = 0.0;
        this.oxygenLevel = 0.0;
    }

    /**
     * Parameterized constructor.
     *
     * @param name        Name of the air entity.
     * @param mass        Mass of the air volume.
     * @param humidity    Humidity percentage.
     * @param temperature Temperature value.
     * @param oxygenLevel Oxygen concentration.
     */
    public Air(final String name,
            final double mass,
            final double humidity,
            final double temperature,
            final double oxygenLevel) {
        super(name, mass, 0.0);
        this.humidity = humidity;
        this.temperature = temperature;
        this.oxygenLevel = oxygenLevel;
    }

    /**
     * Copy constructor.
     *
     * @param other The Air object to copy from.
     */
    public Air(final Air other) {
        super(other);
        this.humidity = other.humidity;
        this.temperature = other.temperature;
        this.oxygenLevel = other.oxygenLevel;
        this.airQualityScore = other.airQualityScore;
        this.toxicAQ = other.toxicAQ;
        this.maxScore = other.maxScore;
    }

    /**
     * Changes weather conditions.
     */
    public abstract void changeWeather();

    /**
     * Generic interaction with another entity.
     *
     * @param other The entity to interact with.
     */
    @Override
    public void interactWith(final Entity other) {
        other.interactWithAir(this);
    }

    /**
     * Interaction with a plant.
     * Increases oxygen level and advances plant maturity.
     *
     * @param plant The plant entity.
     */
    @Override
    public void interactWithPlant(final Plant plant) {
        plant.nextMaturityStage();
        if (plant.getMaturity() != Maturity.DEAD) {
            oxygenLevel += plant.generatedOxygen();
            oxygenLevel = Math.round(oxygenLevel * ROUNDING_FACTOR) / ROUNDING_FACTOR;
            updateStatus();
        }
    }

    /**
     * Interaction with water.
     * Increases humidity.
     *
     * @param water The water entity.
     */
    @Override
    public void interactWithWater(final Water water) {
        this.humidity += HUMIDITY_GAIN;
        this.humidity = normalizeScore(this.humidity);
        updateStatus();
    }

    /**
     * Exports the entity data to a JSON ObjectNode.
     *
     * @return The JSON representation.
     */
    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("type", this.getType());
        node.put("name", this.name);
        node.put("mass", this.mass);
        node.put("humidity", this.humidity);
        node.put("temperature", this.temperature);
        node.put("oxygenLevel", this.oxygenLevel);
        node.put("airQuality", this.airQualityScore);
        return node;
    }

    /**
     * Calculates air quality.
     *
     * @return the score.
     */
    public abstract double calculateAirQuality();

    /**
     * Updates the air quality score, toxicity and internal status.
     * Recalculates based on current parameters.
     */
    @Override
    public void updateStatus() {
        airQualityScore = calculateAirQuality();
        airQualityScore = normalizeScore(airQualityScore);

        this.quality = determineQuality(airQualityScore);

        double toxicity = MAX_PERCENTAGE * (1.0 - (airQualityScore / this.maxScore));
        this.toxicAQ = normalizeScore(toxicity);
        this.robotProbability = this.toxicAQ;
    }

    @Override
    public final int getMemoryKey() {
        return 0;
    }

    @Override
    public final String getMainClass() {
        return "air";
    }

    /**
     * Gets humidity.
     *
     * @return humidity.
     */
    public final double getHumidity() {
        return humidity;
    }

    /**
     * Sets humidity.
     *
     * @param humidity value.
     */
    public final void setHumidity(final double humidity) {
        this.humidity = humidity;
        updateStatus();
    }

    /**
     * Gets temperature.
     *
     * @return temperature.
     */
    public final double getTemperature() {
        return temperature;
    }

    /**
     * Sets temperature.
     *
     * @param temperature value.
     */
    public final void setTemperature(final double temperature) {
        this.temperature = temperature;
        updateStatus();
    }

    /**
     * Gets oxygen level.
     *
     * @return oxygen level.
     */
    public final double getOxygenLevel() {
        return oxygenLevel;
    }

    /**
     * Sets oxygen level.
     *
     * @param oxygenLevel value.
     */
    public final void setOxygenLevel(final double oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
        updateStatus();
    }

    /**
     * Gets air quality score.
     *
     * @return score.
     */
    public final double getAirQualityScore() {
        return airQualityScore;
    }

    /**
     * Sets air quality score.
     *
     * @param airQualityScore value.
     */
    public final void setAirQualityScore(final double airQualityScore) {
        this.airQualityScore = airQualityScore;
    }

    /**
     * Gets toxic index.
     *
     * @return toxic index.
     */
    public final double getToxicAQ() {
        return toxicAQ;
    }

    /**
     * Sets toxic index.
     *
     * @param toxicAQ value.
     */
    public final void setToxicAQ(final double toxicAQ) {
        this.toxicAQ = toxicAQ;
    }

    /**
     * Gets max score.
     *
     * @return max score.
     */
    public final int getMaxScore() {
        return maxScore;
    }

    /**
     * Sets max score.
     *
     * @param maxScore value.
     */
    public final void setMaxScore(final int maxScore) {
        this.maxScore = maxScore;
        updateStatus();
    }

    /**
     * Checks if the air is considered toxic based on max score threshold.
     *
     * @return true if toxic, false otherwise.
     */
    public boolean isToxic() {
        return (this.toxicAQ > (TOXICITY_THRESHOLD_RATIO * this.maxScore));
    }
}
