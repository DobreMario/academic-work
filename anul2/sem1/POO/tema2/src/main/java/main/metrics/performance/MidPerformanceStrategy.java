package main.metrics.performance;

import main.metrics.DeveloperStatsCalculator.DevStats;

public final class MidPerformanceStrategy implements PerformanceStrategy {

    private static final double CLOSED_WEIGHT = 0.5;
    private static final double HIGH_PRIO_WEIGHT = 0.7;
    private static final double TIME_PENALTY = 0.3;
    private static final double BONUS = 15.0;

    @Override
    public double calculateScore(final DevStats stats) {
        return Math.max(0.0,
                CLOSED_WEIGHT * stats.getClosedCount()
                        + HIGH_PRIO_WEIGHT * stats.getHighPriorityCount()
                        - TIME_PENALTY * stats.getAvgResolutionTime())
                + BONUS;
    }
}
