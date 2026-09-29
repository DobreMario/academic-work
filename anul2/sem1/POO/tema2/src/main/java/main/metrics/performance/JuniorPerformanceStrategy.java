package main.metrics.performance;

import main.metrics.DeveloperStatsCalculator.DevStats;

public final class JuniorPerformanceStrategy extends BasePerformanceStrategy {

    private static final double CLOSED_WEIGHT = 0.5;
    private static final double BONUS = 5.0;

    @Override
    public double calculateScore(final DevStats stats) {
        double diversity = ticketDiversityFactor(stats.getBugCount(),
                stats.getFeatureCount(), stats.getUiCount());
        return Math.max(0.0, CLOSED_WEIGHT * stats.getClosedCount() - diversity)
                + BONUS;
    }
}
