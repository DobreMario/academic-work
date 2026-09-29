package entities.soils;

import fileio.SoilInput;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents the Desert Soil entity.
 * Characterized by high salinity and low water retention.
 */
public final class DesertSoil extends Soil {

    private static final double NITROGEN_WEIGHT = 0.5;
    private static final double RETENTION_WEIGHT = 0.3;
    private static final double SALINITY_PENALTY = 2.0;
    private static final double CALCULATION_BASE = 100.0;
    private static final double PROB_DIVISOR = 100.0;
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    private double salinity;

    /**
     * Parameterized constructor.
     *
     * @param name           Name of the soil.
     * @param mass           Mass of the soil.
     * @param nitrogen       Nitrogen level.
     * @param waterRetention Water retention level.
     * @param soilpH         pH level.
     * @param organicMatter  Organic matter level.
     * @param salinity       Salinity level.
     */
    public DesertSoil(final String name,
            final double mass,
            final double nitrogen,
            final double waterRetention,
            final double soilpH,
            final double organicMatter,
            final double salinity) {
        super(name, mass, nitrogen, waterRetention, soilpH, organicMatter);
        this.salinity = salinity;
        updateStatus();
    }

    /**
     * Constructor based on file input.
     *
     * @param soilInput The input data object.
     */
    public DesertSoil(final SoilInput soilInput) {
        super(soilInput.getName(),
                soilInput.getMass(),
                soilInput.getNitrogen(),
                soilInput.getWaterRetention(),
                soilInput.getSoilpH(),
                soilInput.getOrganicMatter());
        this.salinity = soilInput.getSalinity();
        updateStatus();
    }

    @Override
    protected double calculateSoilQuality() {
        return (nitrogen * NITROGEN_WEIGHT)
                + (waterRetention * RETENTION_WEIGHT)
                - (salinity * SALINITY_PENALTY);
    }

    @Override
    protected double calculateRobotStuckProbability() {
        double rawScore = (CALCULATION_BASE - waterRetention + salinity)
                / PROB_DIVISOR * PERCENTAGE_MULTIPLIER;
        return this.normalizeScore(rawScore);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("salinity", this.salinity);
        return node;
    }

    /**
     * Gets the salinity level.
     *
     * @return salinity value.
     */
    public double getSalinity() {
        return salinity;
    }

    /**
     * Sets the salinity level and updates status.
     *
     * @param salinity new salinity value.
     */
    public void setSalinity(final double salinity) {
        this.salinity = salinity;
        updateStatus();
    }
}
