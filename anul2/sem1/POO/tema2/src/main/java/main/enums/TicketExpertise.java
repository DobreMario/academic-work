package main.enums;

/**
 * Enumeration representing the expertise required for a ticket.
 * Uses bitmasks to allow checking for overlapping expertise.
 */
public enum TicketExpertise {
    FRONTEND(1),
    BACKEND(2),
    DEVOPS(4),
    DESIGN(8),
    DB(16);

    private final int bitmask;

    /**
     * Constructor.
     *
     * @param bitmask The bitmask value.
     */
    TicketExpertise(final int bitmask) {
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
