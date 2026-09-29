package main.enums;

/**
 * Enumeration representing customer demand levels.
 */
public enum Demand {
    LOW(1),
    MEDIUM(3),
    HIGH(6),
    VERY_HIGH(10);

    private final int weight;

    /**
     * Constructor.
     *
     * @param weight The numerical weight of the demand.
     */
    Demand(final int weight) {
        this.weight = weight;
    }

    /**
     * Gets the weight.
     *
     * @return The weight.
     */
    public int getWeight() {
        return weight;
    }
}
