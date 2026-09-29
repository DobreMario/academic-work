package main.tickets;

import main.fileio.TicketInput;

/**
 * Factory class for creating different types of tickets.
 * This is a utility class and cannot be instantiated.
 */
public final class Factory {

    private Factory() {
    }

    /**
     * Creates a ticket based on the input type.
     *
     * @param input The input data containing ticket details.
     * @return A specific implementation of Ticket (Bug, FeatureRequest,
     *         UiFeedback).
     * @throws IllegalArgumentException If the ticket type is unknown.
     */
    public static Ticket createTicket(final TicketInput input) {
        switch (input.getType()) {
            case "BUG":
                return buildBug(input);
            case "FEATURE_REQUEST":
                return buildFeatureRequest(input);
            case "UI_FEEDBACK":
                return buildUiFeedback(input);
            default:
                throw new IllegalArgumentException("Unknown ticket type: " + input.getType());
        }
    }

    private static Bug buildBug(final TicketInput input) {
        Bug.BugBuilder builder = new Bug.BugBuilder();

        populateCommonFields(builder, input);

        builder.setEnvironment(input.getEnvironment())
                .setErrorCode(input.getErrorCode());

        if (input.getExpectedBehavior() != null) {
            builder.setExpectedBehavior(input.getExpectedBehavior());
        } else {
            throw new IllegalArgumentException("Expected behavior is required for Bug.");
        }

        if (input.getActualBehavior() != null) {
            builder.setActualBehavior(input.getActualBehavior());
        } else {
            throw new IllegalArgumentException("Actual behavior is required for Bug.");
        }

        if (input.getFrequency() != null) {
            builder.setFrequency(input.getFrequency());
        } else {
            throw new IllegalArgumentException("Frequency is required for Bug.");
        }

        if (input.getSeverity() != null) {
            builder.setSeverity(input.getSeverity());
        } else {
            throw new IllegalArgumentException("Severity is required for Bug.");
        }

        return builder.build();
    }

    private static FeatureRequest buildFeatureRequest(final TicketInput input) {
        FeatureRequest.FeatureRequestBuilder builder = new FeatureRequest.FeatureRequestBuilder();

        populateCommonFields(builder, input);

        if (input.getReportedBy() == null || input.getReportedBy().isEmpty()) {
            throw new IllegalArgumentException("Anonymous reports are only allowed "
                    + "for tickets of type BUG.");
        }

        if (input.getBusinessValue() != null) {
            builder.setBusinessValue(input.getBusinessValue());
        } else {
            throw new IllegalArgumentException("Business value is required.");
        }

        if (input.getCustomerDemand() != null) {
            builder.setCustomerDemand(input.getCustomerDemand());
        } else {
            throw new IllegalArgumentException("Customer demand is required.");
        }

        return builder.build();
    }

    private static UiFeedback buildUiFeedback(final TicketInput input) {
        UiFeedback.UiFeedbackBuilder builder = new UiFeedback.UiFeedbackBuilder();

        populateCommonFields(builder, input);

        if (input.getReportedBy() == null || input.getReportedBy().isEmpty()) {
            throw new IllegalArgumentException("Anonymous reports are only allowed "
                    + "for tickets of type BUG.");
        }

        builder.setUiElement(input.getUiElementId())
                .setScreenshotUrl(input.getScreenshotUrl())
                .setSuggestedFix(input.getSuggestedFix());

        if (input.getBusinessValue() != null) {
            builder.setBusinessValue(input.getBusinessValue());
        } else {
            throw new IllegalArgumentException("Business value is required.");
        }

        if (input.getUsabilityScore() != null) {
            builder.setUsabilityScore(input.getUsabilityScore());
        } else {
            throw new IllegalArgumentException("Usability score is required.");
        }

        return builder.build();
    }

    private static void populateCommonFields(final TicketBuilder<?> builder,
            final TicketInput input) {
        builder.setStatus()
                .setDescription(input.getDescription())
                .setReportedBy(input.getReportedBy());

        if (input.getId() != null) {
            builder.setId(input.getId());
        } else {
            throw new IllegalArgumentException("Ticket ID is required.");
        }

        if (input.getTitle() != null) {
            builder.setTitle(input.getTitle());
        } else {
            throw new IllegalArgumentException("Ticket title is required.");
        }

        if (input.getBusinessPriority() != null) {
            builder.setBusinessPriority(input.getBusinessPriority());
        } else {
            throw new IllegalArgumentException("Business priority is required.");
        }

        if (input.getExpertiseArea() != null) {
            builder.setExpertiseArea(input.getExpertiseArea());
        } else {
            throw new IllegalArgumentException("Expertise area is required.");
        }
    }
}
