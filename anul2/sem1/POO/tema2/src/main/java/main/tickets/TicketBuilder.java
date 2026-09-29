package main.tickets;

import main.enums.Priority;
import main.enums.TicketStatus;
import main.enums.TicketExpertise;

/**
 * Abstract Builder class using the Recursive Generics pattern.
 * Allows constructing Ticket objects while maintaining fluent interface in
 * subclasses.
 *
 * @param <T> The concrete type of the Builder (e.g., BugBuilder).
 */
public abstract class TicketBuilder<T extends TicketBuilder<T>> {
    protected Integer id;
    protected String type;
    protected String title;
    protected Priority businessPriority;
    protected TicketStatus status;
    protected TicketExpertise expertiseArea;
    protected String description;
    protected String reportedBy;

    /**
     * Returns the "this" reference typed as the concrete subclass.
     * Essential for method chaining in the builder pattern.
     *
     * @return The builder instance.
     */
    protected abstract T self();

    /**
     * Sets the ticket ID.
     *
     * @param ticketId The ID to set.
     * @return The builder instance.
     */
    public T setId(final Integer ticketId) {
        this.id = ticketId;
        return self();
    }

    /**
     * Sets the ticket type string.
     *
     * @param ticketType The type of the ticket.
     * @return The builder instance.
     */
    public T setType(final String ticketType) {
        this.type = ticketType;
        return self();
    }

    /**
     * Sets the ticket title.
     *
     * @param ticketTitle The title of the ticket.
     * @return The builder instance.
     */
    public T setTitle(final String ticketTitle) {
        this.title = ticketTitle;
        return self();
    }

    /**
     * Sets the business priority using the Priority enum.
     *
     * @param priority The priority enum value.
     * @return The builder instance.
     */
    public T setBusinessPriority(final Priority priority) {
        this.businessPriority = priority;
        return self();
    }

    /**
     * Sets the business priority using a string value.
     *
     * @param priorityStr The priority as a string.
     * @return The builder instance.
     */
    public T setBusinessPriority(final String priorityStr) {
        this.businessPriority = Priority.valueOf(priorityStr);
        return self();
    }

    /**
     * Sets the status to default (OPEN).
     *
     * @return The builder instance.
     */
    public T setStatus() {
        this.status = TicketStatus.OPEN;
        return self();
    }

    /**
     * Sets the expertise area using the TicketExpertise enum.
     *
     * @param expertise The expertise area enum.
     * @return The builder instance.
     */
    public T setExpertiseArea(final TicketExpertise expertise) {
        this.expertiseArea = expertise;
        return self();
    }

    /**
     * Sets the expertise area using a string value.
     *
     * @param expertiseStr The expertise area as a string.
     * @return The builder instance.
     */
    public T setExpertiseArea(final String expertiseStr) {
        this.expertiseArea = TicketExpertise.valueOf(expertiseStr);
        return self();
    }

    /**
     * Sets the description of the ticket.
     *
     * @param desc The description string.
     * @return The builder instance.
     */
    public T setDescription(final String desc) {
        this.description = desc;
        return self();
    }

    /**
     * Sets the reporter of the ticket.
     *
     * @param reporter The username of the reporter.
     * @return The builder instance.
     */
    public T setReportedBy(final String reporter) {
        this.reportedBy = reporter;
        return self();
    }

    /**
     * Builds the final Ticket object.
     * Must be implemented by concrete builders to return specific Ticket types.
     *
     * @return The constructed Ticket.
     */
    public abstract Ticket build();
}
