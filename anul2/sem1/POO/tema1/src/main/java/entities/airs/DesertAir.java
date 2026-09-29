package entities.airs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.AirInput;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Desert Air entity.
 * It manages specific weather conditions like desert storms.
 */
public final class DesertAir extends Air {

    private static final int MAX_SCORE = 65;
    private static final double STORM_PENALTY = 30.0;
    private static final double OXYGEN_WEIGHT = 2.0;
    private static final double DUST_WEIGHT = 0.2;
    private static final double TEMP_WEIGHT = 0.3;

    // Static list to track instances for global weather effects
    private static final List<DesertAir> INSTANCES = new ArrayList<>();

    private static int desertStormCount = -1;
    private static boolean isDesertStorm = false;

    private double dustParticles;

    /**
     * Parameterized constructor.
     *
     * @param name          Name of the air.
     * @param mass          Mass of the air.
     * @param humidity      Humidity level.
     * @param temperature   Temperature level.
     * @param oxygenLevel   Oxygen level.
     * @param dustParticles Dust particle concentration.
     */
    public DesertAir(final String name,
            final double mass,
            final double humidity,
            final double temperature,
            final double oxygenLevel,
            final double dustParticles) {
        super(name, mass, humidity, temperature, oxygenLevel);
        this.dustParticles = dustParticles;
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Constructor based on file input.
     *
     * @param airInput The input data object.
     */
    public DesertAir(final AirInput airInput) {
        super(airInput.getName(),
                airInput.getMass(),
                airInput.getHumidity(),
                airInput.getTemperature(),
                airInput.getOxygenLevel());
        this.dustParticles = airInput.getDustParticles();
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Applies the desert storm effect to all tracked instances.
     * Increments the storm counter.
     */
    public static void applyDesertStorm() {
        if (!isDesertStorm) {
            return;
        }
        for (DesertAir desertAir : INSTANCES) {
            desertAir.changeWeather();
        }
        desertStormCount++;
        if (desertStormCount > 1) {
            desertStormCount = -1;
            isDesertStorm = false;
        }
    }

    /**
     * Resets the static tracking of DesertAir instances.
     */
    public static void reset() {
        INSTANCES.clear();
        desertStormCount = -1;
        isDesertStorm = false;
    }

    @Override
    public double calculateAirQuality() {
        return (oxygenLevel * OXYGEN_WEIGHT)
                - (dustParticles * DUST_WEIGHT)
                - (temperature * TEMP_WEIGHT);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("desertStorm", isDesertStorm);
        return node;
    }

    @Override
    public void changeWeather() {
        if (desertStormCount >= 0) {
            if (desertStormCount == 0) {
                double penalty = isDesertStorm ? STORM_PENALTY : 0.0;
                airQualityScore = airQualityScore - penalty;
                airQualityScore = normalizeScore(airQualityScore);
            } else if (desertStormCount == 1) {
                updateStatus();
            }
        }
    }

    /**
     * Gets the dust particle level.
     *
     * @return The amount of dust particles.
     */
    public double getDustParticles() {
        return dustParticles;
    }

    /**
     * Sets the dust particle level.
     *
     * @param dustParticles The new dust particle value.
     */
    public void setDustParticles(final double dustParticles) {
        this.dustParticles = dustParticles;
        updateStatus();
    }

    /**
     * Checks if a desert storm is active.
     *
     * @return true if storm is active.
     */
    public static boolean isDesertStorm() {
        return isDesertStorm;
    }

    /**
     * Sets the desert storm status.
     *
     * @param desertStormState The new storm state.
     */
    public static void setDesertStorm(final boolean desertStormState) {
        isDesertStorm = desertStormState;
    }

    /**
     * Gets the current storm duration counter.
     *
     * @return The counter value.
     */
    public static int getDesertStormCount() {
        return desertStormCount;
    }

    /**
     * Sets the storm duration counter.
     *
     * @param count The new counter value.
     */
    public static void setDesertStormCount(final int count) {
        desertStormCount = count;
    }
}
