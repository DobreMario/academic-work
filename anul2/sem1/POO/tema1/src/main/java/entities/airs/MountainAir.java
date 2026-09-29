package entities.airs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.AirInput;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Mountain Air entity.
 * Manages altitude effects and hiking events.
 */
public final class MountainAir extends Air {

    private static final int MAX_SCORE = 78;
    private static final double ALTITUDE_DIVISOR = 1000.0;
    private static final double ALTITUDE_PENALTY_WEIGHT = 0.5;
    private static final double HUMIDITY_WEIGHT = 0.6;
    private static final double OXYGEN_WEIGHT = 2.0;
    private static final double HIKER_PENALTY = 5.0;

    // Static list to track instances
    private static final List<MountainAir> INSTANCES = new ArrayList<>();

    private static int hikingEventCount = -1;
    private static int numberOfHikers = 0;

    private double altitude;

    /**
     * Parameterized constructor.
     *
     * @param name        Name of the air.
     * @param mass        Mass of the air.
     * @param humidity    Humidity level.
     * @param temperature Temperature level.
     * @param oxygenLevel Oxygen level.
     * @param altitude    Altitude level.
     */
    public MountainAir(final String name,
            final double mass,
            final double humidity,
            final double temperature,
            final double oxygenLevel,
            final double altitude) {
        super(name, mass, humidity, temperature, oxygenLevel);
        this.altitude = altitude;
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Constructor based on file input.
     *
     * @param airInput The input data object.
     */
    public MountainAir(final AirInput airInput) {
        super(airInput.getName(),
                airInput.getMass(),
                airInput.getHumidity(),
                airInput.getTemperature(),
                airInput.getOxygenLevel());
        this.altitude = airInput.getAltitude();
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Triggers a hiking event across all instances.
     * Increments the event counter.
     */
    public static void applyHiking() {
        if (hikingEventCount < 0) {
            return;
        }
        for (MountainAir mountainAir : INSTANCES) {
            mountainAir.changeWeather();
        }
        hikingEventCount++;
        if (hikingEventCount > 1) {
            hikingEventCount = -1;
            numberOfHikers = 0;
        }
    }

    /**
     * Resets the static tracking and event variables.
     */
    public static void reset() {
        INSTANCES.clear();
        hikingEventCount = -1;
        numberOfHikers = 0;
    }

    @Override
    public double calculateAirQuality() {
        double oxygenFactor = oxygenLevel - (altitude / ALTITUDE_DIVISOR * ALTITUDE_PENALTY_WEIGHT);
        return (oxygenFactor * OXYGEN_WEIGHT) + (humidity * HUMIDITY_WEIGHT);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("altitude", this.altitude);
        return node;
    }

    @Override
    public void changeWeather() {
        if (hikingEventCount >= 0) {
            if (hikingEventCount == 0) {
                airQualityScore = airQualityScore - (numberOfHikers * HIKER_PENALTY);
                normalizeScore(airQualityScore);
            } else if (hikingEventCount == 1) {
                updateStatus();
            }
        }
    }

    /**
     * Gets the altitude.
     *
     * @return altitude value.
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Sets the altitude.
     *
     * @param altitude The new altitude value.
     */
    public void setAltitude(final double altitude) {
        this.altitude = altitude;
        updateStatus();
    }

    /**
     * Gets the hiking event counter.
     *
     * @return counter value.
     */
    public static int getHikingEventCount() {
        return hikingEventCount;
    }

    /**
     * Sets the hiking event counter.
     *
     * @param count New counter value.
     */
    public static void setHikingEventCount(final int count) {
        hikingEventCount = count;
    }

    /**
     * Gets the number of hikers.
     *
     * @return number of hikers.
     */
    public static int getNumberOfHikers() {
        return numberOfHikers;
    }

    /**
     * Sets the number of hikers.
     *
     * @param count New number of hikers.
     */
    public static void setNumberOfHikers(final int count) {
        numberOfHikers = count;
    }
}
