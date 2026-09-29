package main.enums;

/**
 * Enumeration representing the severity of a bug.
 */
public enum Severity {
    MINOR(1),
    MODERATE(2),
    SEVERE(3);

    private final int level;

    /**
     * Constructor.
     *
     * @param level The numerical severity level.
     */
    Severity(final int level) {
        this.level = level;
    }

    /**
     * Gets the severity level.
     *
     * @return The level.
     */
    public int getLevel() {
        return level;
    }
}
