package entities.soils;

import fileio.SoilInput;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents the Tundra Soil entity.
 * Characterized by permafrost depth which affects quality and robot traversal.
 */
public final class TundraSoil extends Soil {

    private static final double NITROGEN_WEIGHT = 0.7;
    private static final double ORGANIC_WEIGHT = 0.5;
    private static final double PERMAFROST_WEIGHT = 1.5;
    private static final double DEPTH_THRESHOLD = 50.0;
    private static final double PROB_DIVISOR = 50.0;
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    private double permafrostDepth;

    /**
     * Parameterized constructor.
     *
     * @param name            Name of the soil.
     * @param mass            Mass of the soil.
     * @param nitrogen        Nitrogen level.
     * @param waterRetention  Water retention level.
     * @param soilpH          pH level.
     * @param organicMatter   Organic matter level.
     * @param permafrostDepth Depth of the permafrost.
     */
    public TundraSoil(final String name,
            final double mass,
            final double nitrogen,
            final double waterRetention,
            final double soilpH,
            final double organicMatter,
            final double permafrostDepth) {
        super(name, mass, nitrogen, waterRetention, soilpH, organicMatter);
        this.permafrostDepth = permafrostDepth;
        updateStatus();
    }

    /**
     * Constructor based on file input.
     *
     * @param soilInput The input data object.
     */
    public TundraSoil(final SoilInput soilInput) {
        super(soilInput.getName(),
                soilInput.getMass(),
                soilInput.getNitrogen(),
                soilInput.getWaterRetention(),
                soilInput.getSoilpH(),
                soilInput.getOrganicMatter());
        this.permafrostDepth = soilInput.getPermafrostDepth();
        updateStatus();
    }

    @Override
    protected double calculateSoilQuality() {
        return (nitrogen * NITROGEN_WEIGHT)
                + (organicMatter * ORGANIC_WEIGHT)
                - (permafrostDepth * PERMAFROST_WEIGHT);
    }

    @Override
    protected double calculateRobotStuckProbability() {
        double rawScore = (DEPTH_THRESHOLD - permafrostDepth)
                / PROB_DIVISOR * PERCENTAGE_MULTIPLIER;
        return this.normalizeScore(rawScore);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("permafrostDepth", this.permafrostDepth);
        return node;
    }

    /**
     * Gets permafrost depth.
     *
     * @return permafrost depth value.
     */
    public double getPermafrostDepth() {
        return permafrostDepth;
    }

    /**
     * Sets permafrost depth and updates status.
     *
     * @param permafrostDepth new depth value.
     */
    public void setPermafrostDepth(final double permafrostDepth) {
        this.permafrostDepth = permafrostDepth;
        updateStatus();
    }
}
