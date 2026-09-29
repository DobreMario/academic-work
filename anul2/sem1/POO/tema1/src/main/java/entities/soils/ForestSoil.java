package entities.soils;

import fileio.SoilInput;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents the Forest Soil entity.
 * Characterized by leaf litter which affects soil quality and robot movement.
 */
public final class ForestSoil extends Soil {

    private static final double NITROGEN_WEIGHT = 1.2;
    private static final double ORGANIC_WEIGHT = 2.0;
    private static final double RETENTION_WEIGHT_QUALITY = 1.5;
    private static final double LITTER_WEIGHT_QUALITY = 0.3;

    private static final double RETENTION_WEIGHT_PROB = 0.6;
    private static final double LITTER_WEIGHT_PROB = 0.4;
    private static final double PROB_DIVISOR = 80.0;
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    private double leafLitter;

    /**
     * Parameterized constructor.
     *
     * @param name           Name of the soil.
     * @param mass           Mass of the soil.
     * @param nitrogen       Nitrogen level.
     * @param waterRetention Water retention level.
     * @param soilpH         pH level.
     * @param organicMatter  Organic matter level.
     * @param leafLitter     Leaf litter level.
     */
    public ForestSoil(final String name,
            final double mass,
            final double nitrogen,
            final double waterRetention,
            final double soilpH,
            final double organicMatter,
            final double leafLitter) {
        super(name, mass, nitrogen, waterRetention, soilpH, organicMatter);
        this.leafLitter = leafLitter;
        updateStatus();
    }

    /**
     * Constructor based on file input.
     *
     * @param soilInput The input data object.
     */
    public ForestSoil(final SoilInput soilInput) {
        super(soilInput.getName(),
                soilInput.getMass(),
                soilInput.getNitrogen(),
                soilInput.getWaterRetention(),
                soilInput.getSoilpH(),
                soilInput.getOrganicMatter());
        this.leafLitter = soilInput.getLeafLitter();
        updateStatus();
    }

    @Override
    protected double calculateSoilQuality() {
        return (nitrogen * NITROGEN_WEIGHT)
                + (organicMatter * ORGANIC_WEIGHT)
                + (waterRetention * RETENTION_WEIGHT_QUALITY)
                + (leafLitter * LITTER_WEIGHT_QUALITY);
    }

    @Override
    protected double calculateRobotStuckProbability() {
        double weightedRetention = waterRetention * RETENTION_WEIGHT_PROB;
        double weightedLitter = leafLitter * LITTER_WEIGHT_PROB;
        return this.normalizeScore((weightedRetention + weightedLitter)
                / PROB_DIVISOR * PERCENTAGE_MULTIPLIER);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = super.getEntityNode();
        node.put("leafLitter", this.leafLitter);
        return node;
    }

    /**
     * Gets leaf litter amount.
     *
     * @return leaf litter value.
     */
    public double getLeafLitter() {
        return leafLitter;
    }

    /**
     * Sets leaf litter amount and updates status.
     *
     * @param leafLitter new leaf litter value.
     */
    public void setLeafLitter(final double leafLitter) {
        this.leafLitter = leafLitter;
        updateStatus();
    }
}
