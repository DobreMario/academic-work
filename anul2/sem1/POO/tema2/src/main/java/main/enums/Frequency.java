package main.enums;

/**
 * Enumeration representing the frequency of occurrence for bugs.
 */
public enum Frequency {
    RARE(1),
    OCCASIONAL(2),
    FREQUENT(3),
    ALWAYS(4);

    private final int frequency;

    /**
     * Constructor.
     *
     * @param frequency The numerical frequency value.
     */
    Frequency(final int frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets the frequency value.
     *
     * @return The frequency.
     */
    public int getFrequency() {
        return frequency;
    }
}
