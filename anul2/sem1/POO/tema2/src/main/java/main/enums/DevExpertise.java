package main.enums;

/**
 * Enumeration representing the expertise of a developer.
 * Uses bitmasks to represent combinations of skills.
 */
public enum DevExpertise {
    FRONTEND(9),
    BACKEND(18),
    DEVOPS(4),
    DESIGN(9),
    DB(16),
    FULLSTACK(31);

    private final int bitmask;

    /**
     * Constructor.
     *
     * @param bitmask The bitmask value.
     */
    DevExpertise(final int bitmask) {
        this.bitmask = bitmask;
    }

    /**
     * Gets the bitmask.
     *
     * @return The bitmask integer.
     */
    public int getBitMask() {
        return bitmask;
    }
}
