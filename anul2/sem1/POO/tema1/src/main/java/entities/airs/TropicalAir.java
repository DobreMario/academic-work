package entities.airs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.AirInput;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Tropical Air entity.
 * Manages humidity, CO2 levels, and rainfall events.
 */
public final class TropicalAir extends Air {

    private static final int MAX_SCORE = 82;
    private static final double OXYGEN_WEIGHT = 2.0;
    private static final double HUMIDITY_WEIGHT = 0.5;
    private static final double CO2_WEIGHT = 0.01;
    private static final double RAINFALL_WEIGHT = 0.3;

    // Static list to track instances
    private static final List<TropicalAir> INSTANCES = new ArrayList<>();

    private static int rainfallCount = -1;
    private static double rainfall = 0.0;

    private double co2Level;

    /**
     * Parameterized constructor.
     *
     * @param name        Name of the air.
     * @param mass        Mass of the air.
     * @param humidity    Humidity level.
     * @param temperature Temperature level.
     * @param oxygenLevel Oxygen level.
     * @param co2Level    CO2 concentration.
     */
    public TropicalAir(final String name,
            final double mass,
            final double humidity,
            final double temperature,
            final double oxygenLevel,
            final double co2Level) {
        super(name, mass, humidity, temperature, oxygenLevel);
        this.co2Level = normalizeScore(co2Level);
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Constructor based on file input.
     *
     * @param airInput The input data object.
     */
    public TropicalAir(final AirInput airInput) {
        super(airInput.getName(),
                airInput.getMass(),
                airInput.getHumidity(),
                airInput.getTemperature(),
                airInput.getOxygenLevel());
        this.co2Level = normalizeScore(airInput.getCo2Level());
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Triggers a rainfall event across all instances.
     * Increments the rainfall counter.
     */
    public static void applyRainfall() {
        if (rainfallCount < 0) {
            return;
        }
        for (TropicalAir tropicalAir : INSTANCES) {
            tropicalAir.changeWeather();
        }
        rainfallCount++;
        if (rainfallCount > 1) {
            rainfallCount = -1;
            rainfall = 0.0;
        }
    }

    /**
     * Resets the static tracking and weather variables.
     */
    public static void reset() {
        INSTANCES.clear();
        rainfallCount = -1;
        rainfall = 0.0;
    }

    @Override
    public double calculateAirQuality() {
        return (oxygenLevel * OXYGEN_WEIGHT)
                + (humidity * HUMIDITY_WEIGHT)
                - (co2Level * CO2_WEIGHT);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("co2Level", this.co2Level);
        return node;
    }

    @Override
    public void changeWeather() {
        if (rainfallCount >= 0) {
            if (rainfallCount == 0) {
                airQualityScore = airQualityScore + (rainfall * RAINFALL_WEIGHT);
                normalizeScore(airQualityScore);
            } else if (rainfallCount == 1) {
                updateStatus();
            }
        }
    }

    /**
     * Gets the CO2 level.
     *
     * @return CO2 value.
     */
    public double getCo2Level() {
        return co2Level;
    }

    /**
     * Sets the CO2 level.
     *
     * @param co2Level The new CO2 value.
     */
    public void setCo2Level(final double co2Level) {
        this.co2Level = co2Level;
        updateStatus();
    }

    /**
     * Gets the rainfall counter.
     *
     * @return counter value.
     */
    public static int getRainfallCount() {
        return rainfallCount;
    }

    /**
     * Sets the rainfall counter.
     *
     * @param count New counter value.
     */
    public static void setRainfallCount(final int count) {
        rainfallCount = count;
    }

    /**
     * Gets the rainfall amount.
     *
     * @return rainfall amount.
     */
    public static double getRainfall() {
        return rainfall;
    }

    /**
     * Sets the rainfall amount.
     *
     * @param amount New rainfall amount.
     */
    public static void setRainfall(final double amount) {
        rainfall = amount;
    }
}
