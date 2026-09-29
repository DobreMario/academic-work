package entities.airs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.AirInput;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Temperate Air entity.
 * Manages pollen levels and seasonal changes.
 */
public final class TemperateAir extends Air {

    private static final int MAX_SCORE = 84;
    private static final double OXYGEN_WEIGHT = 2.0;
    private static final double HUMIDITY_WEIGHT = 0.7;
    private static final double POLLEN_WEIGHT = 0.1;
    private static final double SEASON_PENALTY = 15.0;

    // Static list to track instances
    private static final List<TemperateAir> INSTANCES = new ArrayList<>();

    private static int newSeasonCount = -1;
    private static String season = "";

    private double pollenLevel;

    /**
     * Parameterized constructor.
     *
     * @param name        Name of the air.
     * @param mass        Mass of the air.
     * @param humidity    Humidity level.
     * @param temperature Temperature level.
     * @param oxygenLevel Oxygen level.
     * @param pollenLevel Pollen level.
     */
    public TemperateAir(final String name,
            final double mass,
            final double humidity,
            final double temperature,
            final double oxygenLevel,
            final double pollenLevel) {
        super(name, mass, humidity, temperature, oxygenLevel);
        this.pollenLevel = pollenLevel;
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Constructor based on file input.
     *
     * @param airInput The input data object.
     */
    public TemperateAir(final AirInput airInput) {
        super(airInput.getName(),
                airInput.getMass(),
                airInput.getHumidity(),
                airInput.getTemperature(),
                airInput.getOxygenLevel());
        this.pollenLevel = airInput.getPollenLevel();
        this.maxScore = MAX_SCORE;
        updateStatus();
        INSTANCES.add(this);
    }

    /**
     * Triggers a new season event across all instances.
     * Increments the season counter.
     */
    public static void applyNewSeason() {
        if (newSeasonCount < 0) {
            return;
        }
        for (TemperateAir temperateAir : INSTANCES) {
            temperateAir.changeWeather();
        }
        newSeasonCount++;
        if (newSeasonCount > 1) {
            newSeasonCount = -1;
            season = "";
        }
    }

    /**
     * Resets the static tracking and season variables.
     */
    public static void reset() {
        INSTANCES.clear();
        newSeasonCount = -1;
        season = "";
    }

    @Override
    public double calculateAirQuality() {
        return (oxygenLevel * OXYGEN_WEIGHT)
                + (humidity * HUMIDITY_WEIGHT)
                - (pollenLevel * POLLEN_WEIGHT);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("pollenLevel", this.pollenLevel);
        return node;
    }

    @Override
    public void changeWeather() {
        if (newSeasonCount >= 0) {
            if (newSeasonCount == 0) {
                double penalty = ("Spring".equalsIgnoreCase(season)) ? SEASON_PENALTY : 0;
                airQualityScore = airQualityScore - penalty;
                normalizeScore(airQualityScore);
            } else if (newSeasonCount == 1) {
                updateStatus();
            }
        }
    }

    /**
     * Gets the pollen level.
     *
     * @return pollen level.
     */
    public double getPollenLevel() {
        return pollenLevel;
    }

    /**
     * Sets the pollen level.
     *
     * @param pollenLevel New pollen level.
     */
    public void setPollenLevel(final double pollenLevel) {
        this.pollenLevel = pollenLevel;
        updateStatus();
    }

    /**
     * Gets the new season counter.
     *
     * @return counter value.
     */
    public static int getNewSeasonCount() {
        return newSeasonCount;
    }

    /**
     * Sets the new season counter.
     *
     * @param count New counter value.
     */
    public static void setNewSeasonCount(final int count) {
        newSeasonCount = count;
    }

    /**
     * Gets the current season name.
     *
     * @return season name.
     */
    public static String getSeason() {
        return season;
    }

    /**
     * Sets the current season name.
     *
     * @param seasonName New season name.
     */
    public static void setSeason(final String seasonName) {
        season = seasonName;
    }
}
