package main.metrics.strategies;

import main.metrics.MetricStrategy;
import main.tickets.Bug;
import main.tickets.FeatureRequest;
import main.tickets.ServerTicket;
import main.tickets.Ticket;
import main.tickets.UiFeedback;

import java.time.temporal.ChronoUnit;

public final class ResolutionEfficiencyStrategy implements MetricStrategy {

    private static final double MAX_BUG = 70.0;
    private static final double MAX_OTHER = 20.0;
    private static final double DEFAULT_MAX = 1.0;
    private static final double BUG_MULTIPLIER = 10.0;

    @Override
    public double getMaxValue(final ServerTicket st) {
        String type = st.getTicket().getType();
        if (type == null) {
            return DEFAULT_MAX;
        }

        switch (type) {
            case "BUG":
                return MAX_BUG;
            case "FEATURE_REQUEST":
            case "UI_FEEDBACK":
                return MAX_OTHER;
            default:
                return DEFAULT_MAX;
        }
    }

    @Override
    public double calculateBaseScore(final ServerTicket st) {
        Ticket ticket = st.getTicket();
        String type = ticket.getType();
        if (type == null) {
            return 0.0;
        }

        long daysToResolve = 0;
        if (st.getAssignedAt() != null && st.getSolvedAt() != null) {
            daysToResolve = Math.abs(
                    ChronoUnit.DAYS.between(st.getAssignedAt(), st.getSolvedAt())) + 1;
        }

        switch (type.toUpperCase()) {
            case "BUG":
                Bug bug = (Bug) ticket;
                return (bug.getFrequency().getFrequency()
                        + bug.getSeverity().getLevel())
                        * BUG_MULTIPLIER / daysToResolve;

            case "FEATURE_REQUEST":
                FeatureRequest fr = (FeatureRequest) ticket;
                return (fr.getBusinessValue().getWeight()
                        + fr.getCustomerDemand().getWeight())
                        / daysToResolve;

            case "UI_FEEDBACK":
                UiFeedback ui = (UiFeedback) ticket;
                return (ui.getUsabilityScore()
                        + ui.getBusinessValue().getWeight())
                        / daysToResolve;

            default:
                return 0.0;
        }
    }
}
