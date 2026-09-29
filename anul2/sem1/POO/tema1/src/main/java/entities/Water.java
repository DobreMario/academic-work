package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a Water entity with specific chemical properties.
 * Class is final to prevent extension without proper safety contracts.
 */
public final class Water extends Entity {

    private static final double DEFAULT_PH = 7.0;
    private static final double DEFAULT_PURITY = 100.0;
    private static final double PH_OPTIMAL = 7.5;
    private static final double SALINITY_MAX = 350.0;
    private static final double TURBIDITY_MAX = 100.0;
    private static final double PURITY_DIV = 100.0;
    private static final double CONTAMINANT_MAX = 100.0;

    private static final double W_PURITY = 0.3;
    private static final double W_PH = 0.2;
    private static final double W_SALINITY = 0.15;
    private static final double W_TURBIDITY = 0.1;
    private static final double W_CONTAMINANT = 0.15;
    private static final double W_FROZEN = 0.2;

    private static final double MAX_PERCENTAGE = 100.0;

    private String type;
    private double salinity;
    private double pH;
    private double purity;
    private double contaminantsIndex;
    private double turbidity;
    private boolean isFrozen;

    /**
     * Default constructor.
     */
    public Water() {
        super();
        this.type = "Unknown";
        this.salinity = 0.0;
        this.pH = DEFAULT_PH;
        this.purity = DEFAULT_PURITY;
        this.contaminantsIndex = 0.0;
        this.turbidity = 0.0;
        this.isFrozen = false;
    }

    /**
     * Parameterized constructor.
     *
     * @param name              Name of the entity.
     * @param mass              Mass of the entity.
     * @param type              Type of water.
     * @param salinity          Salinity level.
     * @param pHValue           pH level.
     * @param purity            Purity percentage.
     * @param contaminantsIndex Contamination level.
     * @param turbidityValue    Turbidity level.
     * @param frozen            Whether the water is frozen.
     */
    public Water(final String name,
            final double mass,
            final String type,
            final double salinity,
            final double pHValue,
            final double purity,
            final double contaminantsIndex,
            final double turbidityValue,
            final boolean frozen) {
        super(name, mass, 0.0);
        this.type = type;
        this.salinity = salinity;
        this.pH = pHValue;
        this.purity = purity;
        this.contaminantsIndex = contaminantsIndex;
        this.turbidity = turbidityValue;
        this.isFrozen = frozen;
    }

    /**
     * Copy constructor.
     *
     * @param other The Water object to copy.
     */
    public Water(final Water other) {
        super(other);
        this.type = other.type;
        this.salinity = other.salinity;
        this.pH = other.pH;
        this.purity = other.purity;
        this.contaminantsIndex = other.contaminantsIndex;
        this.turbidity = other.turbidity;
        this.isFrozen = other.isFrozen;
    }

    /**
     * Calculates the purity score based on max division.
     *
     * @return Score between 0 and 1.
     */
    public double getPurityScore() {
        return this.purity / PURITY_DIV;
    }

    /**
     * Calculates the pH score based on optimal deviation.
     *
     * @return Score between 0 and 1.
     */
    public double getPHScore() {
        return 1.0 - Math.abs(this.pH - PH_OPTIMAL) / PH_OPTIMAL;
    }

    /**
     * Calculates the salinity score.
     *
     * @return Score between 0 and 1.
     */
    public double getSalinityScore() {
        return 1.0 - (this.salinity / SALINITY_MAX);
    }

    /**
     * Calculates the turbidity score.
     *
     * @return Score between 0 and 1.
     */
    public double getTurbidityScore() {
        return 1.0 - (this.turbidity / TURBIDITY_MAX);
    }

    /**
     * Calculates the contaminant score.
     *
     * @return Score between 0 and 1.
     */
    public double getContaminantScore() {
        return 1.0 - (this.contaminantsIndex / CONTAMINANT_MAX);
    }

    /**
     * Returns score based on frozen state.
     *
     * @return 0.0 if frozen, 1.0 otherwise.
     */
    public double getFrozenScore() {
        return this.isFrozen ? 0.0 : 1.0;
    }

    /**
     * Calculates the overall water quality.
     *
     * @return A double representing quality percentage.
     */
    public double calculateWaterQuality() {
        double weightedScore = W_PURITY * getPurityScore()
                + W_PH * getPHScore()
                + W_SALINITY * getSalinityScore()
                + W_TURBIDITY * getTurbidityScore()
                + W_CONTAMINANT * getContaminantScore()
                + W_FROZEN * getFrozenScore();

        return weightedScore * MAX_PERCENTAGE;
    }

    @Override
    public void interactWith(final Entity other) {
        other.interactWithWater(this);
    }

    @Override
    public ObjectNode getEntityNode() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("type", this.getType());
        node.put("name", this.name);
        node.put("mass", this.mass);
        return node;
    }

    @Override
    public void updateStatus() {
        double waterQuality = calculateWaterQuality();
        this.quality = determineQuality(waterQuality);
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getMainClass() {
        return "water";
    }

    @Override
    public int getMemoryKey() {
        return 2;
    }

    /**
     * Sets the water type.
     *
     * @param typeValue The new type.
     */
    public void setType(final String typeValue) {
        this.type = typeValue;
    }

    /**
     * Gets salinity.
     *
     * @return salinity value.
     */
    public double getSalinity() {
        return this.salinity;
    }

    /**
     * Sets salinity.
     *
     * @param salinityValue The new salinity value.
     */
    public void setSalinity(final double salinityValue) {
        this.salinity = salinityValue;
    }

    /**
     * Gets pH.
     *
     * @return pH value.
     */
    public double getPH() {
        return this.pH;
    }

    /**
     * Sets pH.
     *
     * @param phValue The new pH value.
     */
    public void setPH(final double phValue) {
        this.pH = phValue;
    }

    /**
     * Gets purity.
     *
     * @return purity value.
     */
    public double getPurity() {
        return this.purity;
    }

    /**
     * Sets purity.
     *
     * @param purityValue The new purity value.
     */
    public void setPurity(final double purityValue) {
        this.purity = purityValue;
    }

    /**
     * Gets contaminants index.
     *
     * @return contaminants index.
     */
    public double getContaminantsIndex() {
        return this.contaminantsIndex;
    }

    /**
     * Sets contaminants index.
     *
     * @param contaminantsIndexValue The new index value.
     */
    public void setContaminantsIndex(final double contaminantsIndexValue) {
        this.contaminantsIndex = contaminantsIndexValue;
    }

    /**
     * Gets turbidity.
     *
     * @return turbidity value.
     */
    public double getTurbidity() {
        return this.turbidity;
    }

    /**
     * Sets turbidity.
     *
     * @param turbidityValue The new turbidity value.
     */
    public void setTurbidity(final double turbidityValue) {
        this.turbidity = turbidityValue;
    }

    /**
     * Checks if frozen.
     *
     * @return true if frozen.
     */
    public boolean isFrozen() {
        return this.isFrozen;
    }

    /**
     * Sets frozen state.
     *
     * @param frozenState The new frozen state.
     */
    public void setFrozen(final boolean frozenState) {
        this.isFrozen = frozenState;
    }
}
