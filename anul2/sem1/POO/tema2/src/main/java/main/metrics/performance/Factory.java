package main.metrics.performance;

public final class Factory {

    private Factory() {
        // utility class
    }

    /**
     * Returns the appropriate performance strategy based on user type.
     *
     * @param userType the type of the user (JUNIOR, MID, SENIOR).
     * @return the corresponding strategy.
     */
    public static PerformanceStrategy getStrategy(final String userType) {
        switch (userType.toUpperCase()) {
            case "JUNIOR":
                return new JuniorPerformanceStrategy();
            case "MID":
                return new MidPerformanceStrategy();
            case "SENIOR":
                return new SeniorPerformanceStrategy();
            default:
                return stats -> 0.0;
        }
    }
}
