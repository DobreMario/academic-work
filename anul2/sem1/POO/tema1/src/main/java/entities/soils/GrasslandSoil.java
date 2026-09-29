package entities.soils;

import fileio.SoilInput;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents the Grassland Soil entity.
 * Characterized by root density affecting soil quality and robot movement.
 */
public final class GrasslandSoil extends Soil {

    private static final double NITROGEN_WEIGHT = 1.3;
    private static final double ORGANIC_WEIGHT = 1.5;
    private static final double ROOT_WEIGHT = 0.8;
    private static final double DENSITY_OFFSET = 50.0;
    private static final double RETENTION_WEIGHT = 0.5;
    private static final double PROB_DIVISOR = 75.0;
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    private double rootDensity;

    /**
     * Parameterized constructor.
     *
     * @param name           Name of the soil.
     * @param mass           Mass of the soil.
     * @param nitrogen       Nitrogen level.
     * @param waterRetention Water retention level.
     * @param soilpH         pH level.
     * @param organicMatter  Organic matter level.
     * @param rootDensity    Root density level.
     */
    public GrasslandSoil(final String name,
            final double mass,
            final double nitrogen,
            final double waterRetention,
            final double soilpH,
            final double organicMatter,
            final double rootDensity) {
        super(name, mass, nitrogen, waterRetention, soilpH, organicMatter);
        this.rootDensity = rootDensity;
        updateStatus();
    }

    /**
     * Constructor based on file input.
     *
     * @param soilInput The input data object.
     */
    public GrasslandSoil(final SoilInput soilInput) {
        super(soilInput.getName(),
                soilInput.getMass(),
                soilInput.getNitrogen(),
                soilInput.getWaterRetention(),
                soilInput.getSoilpH(),
                soilInput.getOrganicMatter());
        this.rootDensity = soilInput.getRootDensity();
        updateStatus();
    }

    @Override
    protected double calculateSoilQuality() {
        return (nitrogen * NITROGEN_WEIGHT)
                + (organicMatter * ORGANIC_WEIGHT)
                + (rootDensity * ROOT_WEIGHT);
    }

    @Override
    protected double calculateRobotStuckProbability() {
        double densityFactor = DENSITY_OFFSET - rootDensity;
        double retentionFactor = waterRetention * RETENTION_WEIGHT;
        double rawScore = (densityFactor + retentionFactor) / PROB_DIVISOR * PERCENTAGE_MULTIPLIER;
        return this.normalizeScore(rawScore);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("rootDensity", this.rootDensity);
        return node;
    }

    /**
     * Gets root density.
     *
     * @return root density value.
     */
    public double getRootDensity() {
        return rootDensity;
    }

    /**
     * Sets root density and updates status.
     *
     * @param rootDensity new root density value.
     */
    public void setRootDensity(final double rootDensity) {
        this.rootDensity = rootDensity;
        updateStatus();
    }
}
