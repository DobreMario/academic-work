package entities.soils;

import fileio.SoilInput;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents the Swamp Soil entity.
 * Characterized by high water logging affecting robot movement and soil
 * quality.
 */
public final class SwampSoil extends Soil {

    private static final double NITROGEN_WEIGHT = 1.1;
    private static final double ORGANIC_WEIGHT = 2.2;
    private static final double LOGGING_PENALTY = 5.0;
    private static final double ROBOT_STUCK_FACTOR = 10.0;

    private double waterLogging;

    /**
     * Parameterized constructor.
     *
     * @param name           Name of the soil.
     * @param mass           Mass of the soil.
     * @param nitrogen       Nitrogen level.
     * @param waterRetention Water retention level.
     * @param soilpH         pH level.
     * @param organicMatter  Organic matter level.
     * @param waterLogging   Water logging level.
     */
    public SwampSoil(final String name,
            final double mass,
            final double nitrogen,
            final double waterRetention,
            final double soilpH,
            final double organicMatter,
            final double waterLogging) {
        super(name, mass, nitrogen, waterRetention, soilpH, organicMatter);
        this.waterLogging = waterLogging;
        updateStatus();
    }

    /**
     * Constructor based on file input.
     *
     * @param soilInput The input data object.
     */
    public SwampSoil(final SoilInput soilInput) {
        super(soilInput.getName(),
                soilInput.getMass(),
                soilInput.getNitrogen(),
                soilInput.getWaterRetention(),
                soilInput.getSoilpH(),
                soilInput.getOrganicMatter());
        this.waterLogging = soilInput.getWaterLogging();
        updateStatus();
    }

    @Override
    protected double calculateSoilQuality() {
        return (nitrogen * NITROGEN_WEIGHT)
                + (organicMatter * ORGANIC_WEIGHT)
                - (waterLogging * LOGGING_PENALTY);
    }

    @Override
    protected double calculateRobotStuckProbability() {
        double rawProb = waterLogging * ROBOT_STUCK_FACTOR;
        return this.normalizeScore(rawProb);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("waterLogging", this.waterLogging);
        return node;
    }

    /**
     * Gets the water logging level.
     *
     * @return water logging value.
     */
    public double getWaterLogging() {
        return waterLogging;
    }

    /**
     * Sets the water logging level and updates status.
     *
     * @param waterLogging new water logging value.
     */
    public void setWaterLogging(final double waterLogging) {
        this.waterLogging = waterLogging;
        updateStatus();
    }
}
