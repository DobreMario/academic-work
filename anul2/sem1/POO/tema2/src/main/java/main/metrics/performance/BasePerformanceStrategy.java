package main.metrics.performance;

public abstract class BasePerformanceStrategy implements PerformanceStrategy {

    private static final double DIVISOR = 3.0;

    /**
     * Calculates the ticket diversity factor.
     *
     * @param bug     number of bugs.
     * @param feature number of features.
     * @param ui      number of ui feedbacks.
     * @return the diversity factor.
     */
    protected double ticketDiversityFactor(final int bug, final int feature, final int ui) {
        double mean = (bug + feature + ui) / DIVISOR;
        if (mean == 0.0) {
            return 0.0;
        }

        double variance = (Math.pow(bug - mean, 2)
                + Math.pow(feature - mean, 2)
                + Math.pow(ui - mean, 2)) / DIVISOR;
        double std = Math.sqrt(variance);

        return std / mean;
    }
}
