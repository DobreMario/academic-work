package main.fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Data Transfer Object used to load ticket information from JSON files.
 * Contains fields for all ticket types (Bug, Feature Request, UI Feedback).
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class TicketInput {
    private Integer id;
    private String type;
    private String title;
    private String businessPriority;
    private String status;
    private String expertiseArea;
    private String description;
    private String reportedBy;

    // BUG
    private String expectedBehavior;
    private String actualBehavior;
    private String frequency;
    private String severity;
    private String environment;
    private Integer errorCode;

    // FEATURE_REQUEST
    private String businessValue;
    private String customerDemand;

    // Specifice UI_FEEDBACK
    private String uiElementId;
    private Integer usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

    /**
     * Default constructor for Jackson deserialization.
     */
    public TicketInput() {
    }

    /**
     * Retrieves the ticket ID.
     *
     * @return The ticket ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ticket ID.
     *
     * @param id The new ticket ID.
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Retrieves the ticket type.
     *
     * @return The ticket type (e.g., BUG, FEATURE_REQUEST).
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the ticket title.
     *
     * @return The ticket title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the business priority.
     *
     * @return The business priority.
     */
    public String getBusinessPriority() {
        return businessPriority;
    }

    /**
     * Sets the business priority.
     *
     * @param businessPriority The new business priority.
     */
    public void setBusinessPriority(final String businessPriority) {
        this.businessPriority = businessPriority;
    }

    /**
     * Retrieves the ticket status.
     *
     * @return The ticket status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Retrieves the expertise area required for the ticket.
     *
     * @return The expertise area.
     */
    public String getExpertiseArea() {
        return expertiseArea;
    }

    /**
     * Retrieves the description of the ticket.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the username of the reporter.
     *
     * @return The reporter's username.
     */
    public String getReportedBy() {
        return reportedBy;
    }

    /**
     * Retrieves the expected behavior (for Bugs).
     *
     * @return The expected behavior.
     */
    public String getExpectedBehavior() {
        return expectedBehavior;
    }

    /**
     * Retrieves the actual behavior (for Bugs).
     *
     * @return The actual behavior.
     */
    public String getActualBehavior() {
        return actualBehavior;
    }

    /**
     * Retrieves the frequency of occurrence (for Bugs).
     *
     * @return The frequency.
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Retrieves the severity level (for Bugs).
     *
     * @return The severity.
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Retrieves the environment where the issue occurred (for Bugs).
     *
     * @return The environment.
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Retrieves the error code (for Bugs).
     *
     * @return The error code.
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * Retrieves the business value (for Feature Requests).
     *
     * @return The business value.
     */
    public String getBusinessValue() {
        return businessValue;
    }

    /**
     * Retrieves the customer demand level (for Feature Requests).
     *
     * @return The customer demand.
     */
    public String getCustomerDemand() {
        return customerDemand;
    }

    /**
     * Retrieves the UI element ID (for UI Feedback).
     *
     * @return The UI element ID.
     */
    public String getUiElementId() {
        return uiElementId;
    }

    /**
     * Retrieves the usability score (for UI Feedback).
     *
     * @return The usability score.
     */
    public Integer getUsabilityScore() {
        return usabilityScore;
    }

    /**
     * Retrieves the screenshot URL (for UI Feedback).
     *
     * @return The screenshot URL.
     */
    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    /**
     * Retrieves the suggested fix (for UI Feedback).
     *
     * @return The suggested fix.
     */
    public String getSuggestedFix() {
        return suggestedFix;
    }
}
