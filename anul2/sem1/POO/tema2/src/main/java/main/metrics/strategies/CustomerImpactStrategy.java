package main.metrics.strategies;

import main.metrics.MetricStrategy;
import main.tickets.Ticket;
import main.tickets.Bug;
import main.tickets.FeatureRequest;
import main.tickets.ServerTicket;
import main.tickets.UiFeedback;

public final class CustomerImpactStrategy implements MetricStrategy {

    private static final double MAX_BUG = 48.0;
    private static final double MAX_OTHER = 100.0;
    private static final double DEFAULT_MAX = 1.0;

    @Override
    public double getMaxValue(final ServerTicket sticket) {
        Ticket ticket = sticket.getTicket();
        String type = ticket.getType();

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
    public double calculateBaseScore(final ServerTicket sticket) {
        Ticket ticket = sticket.getTicket();
        String type = ticket.getType();
        switch (type) {
            case "BUG":
                Bug bug = (Bug) ticket;
                return bug.getFrequency().getFrequency()
                        * bug.getBusinessPriority().getWeight()
                        * bug.getSeverity().getLevel();

            case "FEATURE_REQUEST":
                FeatureRequest feature = (FeatureRequest) ticket;
                return feature.getBusinessValue().getWeight()
                        * feature.getCustomerDemand().getWeight();

            case "UI_FEEDBACK":
                UiFeedback ui = (UiFeedback) ticket;
                return ui.getBusinessValue().getWeight() * ui.getUsabilityScore();

            default:
                return 0.0;
        }
    }
}
