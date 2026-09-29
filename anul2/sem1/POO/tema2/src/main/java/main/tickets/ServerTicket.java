package main.tickets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a ticket stored on the server side.
 * Wraps the generic Ticket object with server-specific metadata (timestamps,
 * history, etc.).
 */
public final class ServerTicket {
    private Ticket ticket;
    private LocalDate createdAt;
    private LocalDate assignedAt;
    private LocalDate solvedAt;
    private String assignedTo;
    private String reportedBy;
    private List<CommentObj> comments;
    private String milestone;
    private boolean isInAMilestone = false;
    private List<TicketAction> history = new ArrayList<>();

    /**
     * Constructor for ServerTicket.
     *
     * @param ticket     The underlying Ticket object.
     * @param createdAt  The creation timestamp string.
     * @param reportedBy The username of the reporter.
     */
    public ServerTicket(final Ticket ticket, final String createdAt, final String reportedBy) {
        this.ticket = ticket;
        this.createdAt = LocalDate.parse(createdAt);
        this.assignedAt = null;
        this.solvedAt = null;
        this.assignedTo = null;
        this.reportedBy = reportedBy;
        this.comments = new ArrayList<>();
    }

    /**
     * Converts the server ticket to a JSON node.
     *
     * @param mapper The ObjectMapper to use.
     * @return An ObjectNode representation.
     */
    public ObjectNode toJsonNode(final ObjectMapper mapper) {
        ObjectNode node = ticket.toJsonNode(mapper);
        node.put("createdAt", createdAt.toString());
        if (assignedAt != null) {
            node.put("assignedAt", assignedAt.toString());
        } else {
            node.put("assignedAt", "");
        }

        if (solvedAt != null) {
            node.put("solvedAt", solvedAt.toString());
        } else {
            node.put("solvedAt", "");
        }

        if (assignedTo != null) {
            node.put("assignedTo", assignedTo.toString());
        } else {
            node.put("assignedTo", "");
        }

        if (reportedBy != null) {
            node.put("reportedBy", reportedBy);
        } else {
            node.put("reportedBy", "");
        }

        ArrayNode commentsArray = node.putArray("comments");
        for (CommentObj comment : comments) {
            commentsArray.add(comment.toJsonNode(mapper));
        }

        return node;
    }

    /**
     * Converts the ticket to a JSON node specifically for developers.
     *
     * @param mapper The ObjectMapper to use.
     * @return An ObjectNode representation.
     */
    public ObjectNode toJsonNodeForDev(final ObjectMapper mapper) {
        ObjectNode node = ticket.toJsonNode(mapper);
        node.put("createdAt", createdAt.toString());
        if (assignedAt != null) {
            node.put("assignedAt", assignedAt.toString());
        } else {
            node.put("assignedAt", "");
        }

        if (reportedBy != null) {
            node.put("reportedBy", reportedBy);
        } else {
            node.put("reportedBy", "");
        }

        ArrayNode commentsArray = node.putArray("comments");
        for (CommentObj comment : comments) {
            commentsArray.add(comment.toJsonNode(mapper));
        }

        return node;
    }

    /**
     * Converts the ticket to a JSON node for search results.
     * Adds matching words based on keywords.
     *
     * @param mapper         The ObjectMapper to use.
     * @param searchKeywords The list of keywords to match against.
     * @return An ObjectNode representation.
     */
    public ObjectNode toJsonNodeForSearch(final ObjectMapper mapper,
            final List<String> searchKeywords) {
        ObjectNode node = mapper.createObjectNode();

        node.put("id", ticket.getId());
        node.put("type", ticket.getType());
        node.put("title", ticket.getTitle());
        node.put("businessPriority", ticket.getBusinessPriority().toString());
        node.put("status", ticket.getStatus().toString());
        node.put("createdAt", createdAt.toString());

        if (solvedAt != null) {
            node.put("solvedAt", solvedAt.toString());
        } else {
            node.put("solvedAt", "");
        }

        if (reportedBy != null) {
            node.put("reportedBy", reportedBy);
        } else {
            node.put("reportedBy", "");
        }

        if (searchKeywords != null && !searchKeywords.isEmpty()) {
            List<String> matched = new ArrayList<>();
            String safeDescription = (ticket.getDescription() != null)
                    ? ticket.getDescription()
                    : "";
            String content = (ticket.getTitle() + " " + safeDescription).toLowerCase();

            for (String kw : searchKeywords) {
                if (content.contains(kw.toLowerCase())) {
                    matched.add(kw);
                }
            }
            Collections.sort(matched);

            ArrayNode matchArr = node.putArray("matchingWords");
            for (String m : matched) {
                matchArr.add(m);
            }
        }

        return node;
    }

    /**
     * Retrieves the history of the ticket visible to a specific user.
     *
     * @param mapper              The ObjectMapper to use.
     * @param username            The username requesting the history.
     * @param isManager           True if the user is a manager.
     * @param isCurrentlyAssigned True if the user is currently assigned to this
     *                            ticket.
     * @return An ObjectNode containing the filtered history.
     */
    public ObjectNode getHistoryForUser(final ObjectMapper mapper, final String username,
            final boolean isManager,
            final boolean isCurrentlyAssigned) {
        ObjectNode node = mapper.createObjectNode();

        node.put("id", ticket.getId());
        node.put("title", ticket.getTitle());
        node.put("status", ticket.getStatus().toString());

        ArrayNode actionsArray = node.putArray("actions");
        List<TicketAction> visibleActions;

        if (isManager || isCurrentlyAssigned) {
            visibleActions = this.history;
        } else {
            visibleActions = new ArrayList<>();
            int lastRelevantIndex = -1;

            for (int i = 0; i < history.size(); i++) {
                TicketAction act = history.get(i);
                if (act.getUser().equals(username)) {
                    lastRelevantIndex = i;
                }

                if ("REMOVED_FROM_DEV".equals(act.getType()) && act.getUser().equals(username)) {
                    lastRelevantIndex = i;
                }
            }

            if (lastRelevantIndex != -1) {
                visibleActions = history.subList(0, lastRelevantIndex + 1);
            }
        }

        for (TicketAction action : visibleActions) {

            actionsArray.add(action.toJsonNode(mapper));
        }

        ArrayNode commentsArray = node.putArray("comments");
        if (comments != null) {
            for (CommentObj comment : comments) {
                commentsArray.add(comment.toJsonNode(mapper));
            }
        }

        return node;
    }

    /**
     * Gets the underlying Ticket object.
     *
     * @return The Ticket object.
     */
    public Ticket getTicket() {
        return ticket;
    }

    /**
     * Gets the creation date.
     *
     * @return The creation date.
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the date when the ticket was assigned.
     *
     * @return The assignment date.
     */
    public LocalDate getAssignedAt() {
        return assignedAt;
    }

    /**
     * Sets the assignment date.
     *
     * @param assignedAtString The date string (or empty string for null).
     */
    public void setAssignedAt(final String assignedAtString) {
        if (assignedAtString.equals("")) {
            this.assignedAt = null;
            return;
        }
        this.assignedAt = LocalDate.parse(assignedAtString);
    }

    /**
     * Gets the date when the ticket was solved.
     *
     * @return The solved date.
     */
    public LocalDate getSolvedAt() {
        return solvedAt;
    }

    /**
     * Sets the solved date.
     *
     * @param solvedAt The solved date.
     */
    public void setSolvedAt(final LocalDate solvedAt) {
        this.solvedAt = solvedAt;
    }

    /**
     * Gets the username of the assignee.
     *
     * @return The assignee username.
     */
    public String getAssignedTo() {
        return assignedTo;
    }

    /**
     * Sets the assignee.
     *
     * @param assignedTo The username to assign.
     */
    public void setAssignedTo(final String assignedTo) {
        this.assignedTo = assignedTo;
    }

    /**
     * Gets the list of comments.
     *
     * @return A list of CommentObj.
     */
    public List<CommentObj> getComments() {
        return comments;
    }

    /**
     * Adds a comment to the ticket.
     *
     * @param author    The author of the comment.
     * @param content   The content of the comment.
     * @param timestamp The timestamp of the comment.
     */
    public void addComment(final String author, final String content, final String timestamp) {
        CommentObj comment = new CommentObj(content, author, timestamp);
        this.comments.add(comment);
    }

    /**
     * Removes the last comment made by a specific user.
     *
     * @param username The username whose last comment should be removed.
     */
    public void removeLastCommentFrom(final String username) {
        if (comments.isEmpty()) {
            return;
        }
        for (int i = comments.size() - 1; i >= 0; i--) {
            if (comments.get(i).isAuthor(username)) {
                comments.remove(i);
                return;
            }
        }
    }

    /**
     * Checks if the ticket belongs to a milestone.
     *
     * @return true if part of a milestone.
     */
    public boolean isInAMilestone() {
        return isInAMilestone;
    }

    /**
     * Gets the milestone name.
     *
     * @return The milestone name.
     */
    public String getMilestone() {
        return milestone;
    }

    /**
     * Assigns the ticket to a milestone.
     *
     * @param milestoneName The name of the milestone.
     */
    public void setInAMilestone(final String milestoneName) {
        isInAMilestone = true;
        this.milestone = milestoneName;
    }

    /**
     * Removes the ticket from any milestone.
     */
    public void unsetInAMilestone() {
        isInAMilestone = false;
    }

    /**
     * Gets the history of actions on this ticket.
     *
     * @return A list of TicketAction.
     */
    public List<TicketAction> getHistory() {
        return history;
    }

    /**
     * Adds an action to the ticket history.
     *
     * @param type          The type of action.
     * @param user          The user who performed the action.
     * @param timestamp     The timestamp of the action.
     * @param fromStatus    The previous status.
     * @param toStatus      The new status.
     * @param milestoneName The milestone involved (if any).
     */
    public void addActionToHistory(final String type, final String user, final String timestamp,
            final String fromStatus, final String toStatus,
            final String milestoneName) {
        TicketAction action = new TicketAction(type, user, timestamp,
                fromStatus, toStatus, milestoneName);
        history.add(action);
    }

    /**
     * Populates a JSON ArrayNode with the ticket history.
     *
     * @param historyArray The ArrayNode to populate.
     */
    public void toJsonHistory(final ArrayNode historyArray) {
        ObjectMapper mapper = new ObjectMapper();
        for (TicketAction action : history) {
            historyArray.add(action.toJsonNode(mapper));
        }
    }
}

class CommentObj {
    private String comment;
    private String user;
    private String timestamp;

    CommentObj(final String comment, final String user, final String timestamp) {
        this.comment = comment;
        this.user = user;
        this.timestamp = timestamp;
    }

    public ObjectNode toJsonNode(final ObjectMapper mapper) {
        ObjectNode commentsNode = mapper.createObjectNode();
        commentsNode.put("author", user);
        commentsNode.put("content", comment);
        commentsNode.put("createdAt", timestamp);

        return commentsNode;
    }

    public boolean isAuthor(final String username) {
        return this.user.equals(username);
    }

}

class TicketAction {
    private String type;
    private String user;
    private String timestamp;

    private String fromStatus;
    private String toStatus;
    private String milestone;

    TicketAction(final String type, final String user, final String timestamp,
            final String fromStatus, final String toStatus, final String milestone) {
        this.type = type;
        this.user = user;
        this.timestamp = timestamp;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.milestone = milestone;
    }

    public ObjectNode toJsonNode(final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        if (fromStatus != null) {
            node.put("from", fromStatus);
        }

        if (toStatus != null) {
            node.put("to", toStatus);
        }

        if (milestone != null) {
            node.put("milestone", milestone);
        }

        if ("REMOVED_FROM_DEV".equals(type)) {
            node.put("by", "system");
            node.put("from", user);
        } else {
            node.put("by", user);
        }

        node.put("timestamp", timestamp);
        node.put("action", type);

        return node;
    }

    public String getType() {
        return type;
    }

    public String getUser() {
        return user;
    }
}
