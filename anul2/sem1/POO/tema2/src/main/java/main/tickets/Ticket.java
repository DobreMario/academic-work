package main.tickets;

import main.enums.Priority;
import main.enums.TicketStatus;
import main.enums.TicketExpertise;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Abstract base class representing a generic Ticket.
 * Contains common fields and logic for all ticket types.
 */
public abstract class Ticket {
    protected final Integer id;
    protected final String type;
    protected final String title;
    protected Priority businessPriority;
    protected TicketStatus status;
    protected final TicketExpertise expertiseArea;
    protected final String description;
    protected final String reportedBy;

    protected Ticket(final TicketBuilder<?> builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.title = builder.title;
        this.businessPriority = builder.businessPriority;
        this.status = builder.status;
        this.expertiseArea = builder.expertiseArea;
        this.description = builder.description;
        this.reportedBy = builder.reportedBy;
    }

    /**
     * Abstract method to calculate seniority level.
     * Must be implemented by subclasses.
     *
     * @return The seniority level as an integer.
     */
    public abstract int toSeniorityLevel();

    /**
     * Converts the ticket to a JSON ObjectNode.
     *
     * @param mapper The ObjectMapper used to create the node.
     * @return An ObjectNode containing ticket details.
     */
    public final ObjectNode toJsonNode(final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", this.id);
        node.put("type", this.type);
        node.put("title", this.title);
        node.put("businessPriority", this.businessPriority.toString());
        node.put("status", this.status.toString());
        return node;
    }

    /**
     * Increments the business priority of the ticket.
     * Logic: LOW -> MEDIUM -> HIGH -> CRITICAL.
     */
    public final void incrementPriority() {
        switch (this.businessPriority) {
            case LOW:
                this.businessPriority = Priority.MEDIUM;
                break;
            case MEDIUM:
                this.businessPriority = Priority.HIGH;
                break;
            case HIGH:
                this.businessPriority = Priority.CRITICAL;
                break;
            default:
                break;
        }
    }

    /**
     * Increments the status of the ticket.
     * Logic: IN_PROGRESS -> RESOLVED -> CLOSED.
     */
    public final void incrementStatus() {
        switch (this.status) {
            case IN_PROGRESS:
                this.status = TicketStatus.RESOLVED;
                break;
            case RESOLVED:
                this.status = TicketStatus.CLOSED;
                break;
            default:
                break;
        }
    }

    /**
     * Decrements the status of the ticket.
     * Logic: CLOSED -> RESOLVED -> IN_PROGRESS.
     */
    public final void decrementStatus() {
        switch (this.status) {
            case CLOSED:
                this.status = TicketStatus.RESOLVED;
                break;
            case RESOLVED:
                this.status = TicketStatus.IN_PROGRESS;
                break;
            default:
                break;
        }
    }

    /**
     * Directly sets the priority to CRITICAL.
     */
    public final void incrementPriorityToCritical() {
        this.businessPriority = Priority.CRITICAL;
    }

    /**
     * Sets the status to IN_PROGRESS (marks as assigned).
     */
    public final void getAssigned() {
        status = TicketStatus.IN_PROGRESS;
    }

    /**
     * Sets the status to OPEN (marks as unassigned).
     */
    public final void getUnAssigned() {
        status = TicketStatus.OPEN;
    }

    /**
     * Checks if the ticket is OPEN.
     *
     * @return true if status is OPEN.
     */
    public final boolean isOpened() {
        return status == TicketStatus.OPEN;
    }

    /**
     * Checks if the ticket is IN_PROGRESS.
     *
     * @return true if status is IN_PROGRESS.
     */
    public final boolean isInProgress() {
        return status == TicketStatus.IN_PROGRESS;
    }

    /**
     * Checks if the ticket is CLOSED.
     *
     * @return true if status is CLOSED.
     */
    public final boolean isClosed() {
        return status == TicketStatus.CLOSED;
    }

    /**
     * Checks if the ticket was reported anonymously.
     *
     * @return true if reportedBy is null or empty.
     */
    public final boolean isAnonymous() {
        return this.reportedBy == null || this.reportedBy.isEmpty();
    }

    /**
     * Checks if the ticket is RESOLVED.
     * Note: Kept 'isRezolved' spelling to match interface, though 'Resolved' is
     * correct English.
     *
     * @return true if status is RESOLVED.
     */
    public final boolean isRezolved() {
        return status == TicketStatus.RESOLVED;
    }

    public final boolean isHighPriority() {
        return businessPriority == Priority.HIGH || businessPriority == Priority.CRITICAL;
    }

    /**
     * Retrieves the ticket ID.
     *
     * @return The ID.
     */
    public final Integer getId() {
        return id;
    }

    /**
     * Retrieves the ticket type.
     *
     * @return The type string.
     */
    public final String getType() {
        return type;
    }

    /**
     * Retrieves the ticket title.
     *
     * @return The title.
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Retrieves the business priority.
     *
     * @return The priority enum.
     */
    public final Priority getBusinessPriority() {
        return businessPriority;
    }

    /**
     * Retrieves the current status.
     *
     * @return The status enum.
     */
    public final TicketStatus getStatus() {
        return status;
    }

    /**
     * Predicts the next status in the workflow without changing the state.
     *
     * @return The next TicketStatus.
     */
    public final TicketStatus getNextStatus() {
        switch (this.status) {
            case IN_PROGRESS:
                return TicketStatus.RESOLVED;
            case RESOLVED:
                return TicketStatus.CLOSED;
            default:
                return this.status;
        }
    }

    /**
     * Predicts the previous status in the workflow without changing the state.
     *
     * @return The previous TicketStatus.
     */
    public final TicketStatus getPreviousStatus() {
        switch (this.status) {
            case CLOSED:
                return TicketStatus.RESOLVED;
            case RESOLVED:
                return TicketStatus.IN_PROGRESS;
            default:
                return this.status;
        }
    }

    /**
     * Retrieves the expertise area required.
     *
     * @return The expertise enum.
     */
    public final TicketExpertise getExpertiseArea() {
        return expertiseArea;
    }

    /**
     * Retrieves the description.
     *
     * @return The description string.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Retrieves the reporter's username.
     *
     * @return The reporter username.
     */
    public final String getReportedBy() {
        return reportedBy;
    }

    /**
     * Provides a string representation of the ticket.
     */
    @Override
    public String toString() {
        return "Ticket{"
                + "id=" + id
                + ", type='" + type + '\''
                + ", title='" + title + '\''
                + ", businessPriority=" + businessPriority
                + ", status=" + status
                + ", expertiseArea=" + expertiseArea
                + ", description='" + description + '\''
                + ", reportedBy='" + reportedBy + '\''
                + '}';
    }
}
