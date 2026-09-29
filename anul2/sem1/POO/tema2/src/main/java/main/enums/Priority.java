package main.enums;

/**
 * Enumeration representing the business priority of a ticket.
 */
public enum Priority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private static final int SENIORITY_OFFSET = 2;
    private final int weight;

    /**
     * Constructor.
     *
     * @param weight The numerical weight of the priority.
     */
    Priority(final int weight) {
        this.weight = weight;
    }

    /**
     * Gets the priority weight.
     *
     * @return The weight.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Calculates the minimum seniority level required to handle this priority.
     * Logic: Subtracts an offset from the weight.
     *
     * @return The required seniority level index.
     */
    public int toSeniorityLevel() {
        return (this.weight - SENIORITY_OFFSET < 0) ? 0 : this.weight - SENIORITY_OFFSET;
    }
}
