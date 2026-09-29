package main.enums;

/**
 * Enumeration representing business value/size (T-shirt sizing).
 */
public enum Value {
    S(1),
    M(3),
    L(6),
    XL(10);

    private final int weight;

    /**
     * Constructor.
     *
     * @param weight The numerical weight of the value.
     */
    Value(final int weight) {
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
