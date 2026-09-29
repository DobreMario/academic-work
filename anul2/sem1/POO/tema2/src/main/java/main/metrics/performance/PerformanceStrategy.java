package main.metrics.performance;

import main.metrics.DeveloperStatsCalculator.DevStats;

/**
 * Interface for calculating developer performance scores based on statistics.
 */
public interface PerformanceStrategy {
    /**
     * Calculates the performance score.
     *
     * @param stats the developer's statistics.
     * @return the calculated score.
     */
    double calculateScore(DevStats stats);
}
