package main.metrics.strategies;

import main.metrics.MetricStrategy;
import main.tickets.ServerTicket;
import main.tickets.Ticket;
import main.tickets.Bug;
import main.tickets.FeatureRequest;
import main.tickets.UiFeedback;

public final class TicketRiskStrategy implements MetricStrategy {

    private static final double MAX_BUG = 12.0;
    private static final double MAX_FEATURE = 20.0;
    private static final double MAX_UI = 100.0;
    private static final double DEFAULT_MAX = 1.0;
    private static final int UI_BASE = 11;

    @Override
    public double getMaxValue(final ServerTicket sticket) {
        Ticket ticket = sticket.getTicket();
        String type = ticket.getType();

        switch (type) {
            case "BUG":
                return MAX_BUG;
            case "FEATURE_REQUEST":
                return MAX_FEATURE;
            case "UI_FEEDBACK":
                return MAX_UI;
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
                        * bug.getSeverity().getLevel();

            case "FEATURE_REQUEST":
                FeatureRequest feature = (FeatureRequest) ticket;
                return feature.getBusinessValue().getWeight()
                        + feature.getCustomerDemand().getWeight();

            case "UI_FEEDBACK":
                UiFeedback ui = (UiFeedback) ticket;
                return (UI_BASE - ui.getUsabilityScore())
                        * ui.getBusinessValue().getWeight();

            default:
                return 0.0;
        }
    }
}
