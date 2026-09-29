package main.metrics;

import main.tickets.ServerTicket;

public interface MetricStrategy {
    /**
     * Calculates the base score for a specific ticket.
     *
     * @param ticket the server ticket to evaluate.
     * @return the calculated score.
     */
    double calculateBaseScore(ServerTicket ticket);

    /**
     * Returns the maximum possible value for this metric strategy.
     *
     * @param ticket the server ticket context.
     * @return the maximum value.
     */
    double getMaxValue(ServerTicket ticket);
}
