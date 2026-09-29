package entities.airs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.AirInput;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Polar Air entity.
 * Manages ice crystal concentration and polar storms.
 */
public final class PolarAir extends Air {

    private static final int MAX_SCORE = 142;
    private static final double OXYGEN_WEIGHT = 2.0;
    private static final double TEMP_OFFSET = 100.0;
    private static final double CRYSTAL_WEIGHT = 0.05;
    private static final double WIND_WEIGHT = 0.2;

    // Static list to track instances
    private static final List<PolarAir> INSTANCES = new ArrayList<>();

    private static int polarStormCount = -1;
    private static double windSpeed = 0.0;

    private double iceCrystalConcentration;

    /**
     * Parameterized constructor.
     *
     * @param name                    Name of the air.
     * @param mass                    Mass of the air.
     * @param humidity                Humidity level.
     * @param temperature             Temperature level.
     * @param oxygenLevel             Oxygen level.
     * @param iceCrystalConcentration Concentration of ice crystals.
     */
    public PolarAir(final String name,
            final double mass,
            final double humidity,
            final double temperature,
            final double oxygenLevel,
            final double iceCrystalConcentration) {
        super(name, mass, humidity, temperature, oxygenLevel);
        this.iceCrystalConcentration = iceCrystalConcentration;
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Constructor based on file input.
     *
     * @param airInput The input data object.
     */
    public PolarAir(final AirInput airInput) {
        super(airInput.getName(),
                airInput.getMass(),
                airInput.getHumidity(),
                airInput.getTemperature(),
                airInput.getOxygenLevel());
        this.iceCrystalConcentration = airInput.getIceCrystalConcentration();
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Triggers a polar storm across all instances.
     * Increments the storm counter.
     */
    public static void applyPolarStorm() {
        if (polarStormCount < 0) {
            return;
        }
        for (PolarAir polarAir : INSTANCES) {
            polarAir.changeWeather();
        }
        polarStormCount++;
        if (polarStormCount > 1) {
            polarStormCount = -1;
            windSpeed = 0.0;
        }
    }

    /**
     * Resets the static tracking and weather variables.
     */
    public static void reset() {
        INSTANCES.clear();
        polarStormCount = -1;
        windSpeed = 0.0;
    }

    @Override
    public double calculateAirQuality() {
        return (oxygenLevel * OXYGEN_WEIGHT)
                + (TEMP_OFFSET - Math.abs(temperature))
                - (iceCrystalConcentration * CRYSTAL_WEIGHT);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("iceCrystalConcentration", this.iceCrystalConcentration);
        return node;
    }

    @Override
    public void changeWeather() {
        if (polarStormCount >= 0) {
            if (polarStormCount == 0) {
                airQualityScore = airQualityScore - (windSpeed * WIND_WEIGHT);
                normalizeScore(airQualityScore);
            } else if (polarStormCount == 1) {
                updateStatus();
            }
        }
    }

    /**
     * Gets the ice crystal concentration.
     *
     * @return concentration value.
     */
    public double getIceCrystalConcentration() {
        return iceCrystalConcentration;
    }

    /**
     * Sets the ice crystal concentration.
     *
     * @param iceCrystalConcentration New concentration value.
     */
    public void setIceCrystalConcentration(final double iceCrystalConcentration) {
        this.iceCrystalConcentration = iceCrystalConcentration;
        updateStatus();
    }

    /**
     * Gets the polar storm counter.
     *
     * @return counter value.
     */
    public static int getPolarStormCount() {
        return polarStormCount;
    }

    /**
     * Sets the polar storm counter.
     *
     * @param count New counter value.
     */
    public static void setPolarStormCount(final int count) {
        polarStormCount = count;
    }

    /**
     * Gets the wind speed.
     *
     * @return wind speed.
     */
    public static double getWindSpeed() {
        return windSpeed;
    }

    /**
     * Sets the wind speed.
     *
     * @param speed New wind speed.
     */
    public static void setWindSpeed(final double speed) {
        windSpeed = speed;
    }
}
