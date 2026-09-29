package main.metrics.performance;

import main.metrics.DeveloperStatsCalculator.DevStats;

public final class SeniorPerformanceStrategy implements PerformanceStrategy {

    private static final double CLOSED_WEIGHT = 0.5;
    private static final double HIGH_PRIO_WEIGHT = 1.0;
    private static final double TIME_PENALTY = 0.5;
    private static final double BONUS = 30.0;

    @Override
    public double calculateScore(final DevStats stats) {
        return Math.max(0.0,
                CLOSED_WEIGHT * stats.getClosedCount()
                        + HIGH_PRIO_WEIGHT * stats.getHighPriorityCount()
                        - TIME_PENALTY * stats.getAvgResolutionTime())
                + BONUS;
    }
}
