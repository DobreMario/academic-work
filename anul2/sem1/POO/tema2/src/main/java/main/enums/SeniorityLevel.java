package main.enums;

/**
 * Enumeration representing the seniority level of a developer.
 */
public enum SeniorityLevel {
    JUNIOR(0),
    MID(1),
    SENIOR(2);

    private final int level;

    /**
     * Constructor.
     *
     * @param level The numerical level of seniority.
     */
    SeniorityLevel(final int level) {
        this.level = level;
    }

    /**
     * Gets the seniority level.
     *
     * @return The level.
     */
    public int getLevel() {
        return level;
    }
}
