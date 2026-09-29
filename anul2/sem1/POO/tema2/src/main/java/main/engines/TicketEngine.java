package main.engines;

import main.databases.Server;
import main.enums.DevExpertise;
import main.enums.SeniorityLevel;
import main.exceptions.ValidationException;
import main.milestones.Milestone;
import main.tickets.ServerTicket;
import main.tickets.Ticket;
import main.users.Developer;
import main.users.ServerUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Engine responsible for managing ticket assignments and updates.
 */
public final class TicketEngine {
    private static final int MIN_COMMENT_LENGTH = 10;
    private final Server server;

    /**
     * Constructor.
     *
     * @param server The server instance.
     */
    public TicketEngine(final Server server) {
        this.server = server;
    }

    /**
     * Assigns a ticket to a user.
     *
     * @param username   The username of the assignee.
     * @param ticketId   The ID of the ticket.
     * @param assignedAt The timestamp of assignment.
     */
    public void assignTicket(final String username, final Integer ticketId,
            final String assignedAt) {
        ServerUser su = server.getUsers().get(username);
        ServerTicket st = server.getTickets().get(ticketId);

        if (su == null) {
            throw new ValidationException("User not found: " + username);
        }
        if (st == null) {
            throw new ValidationException("Ticket not found: " + ticketId);
        }

        Ticket ticket = st.getTicket();
        validateTicketAssignment(su, ticket);

        su.addAssignedTicket(ticketId);
        st.setAssignedTo(username);
        st.setAssignedAt(assignedAt);

        st.addActionToHistory("ASSIGNED", username, assignedAt,
                null, null, null);
        st.addActionToHistory("STATUS_CHANGED", username, assignedAt,
                "OPEN", "IN_PROGRESS", null);

        ticket.getAssigned();
    }

    /**
     * Unassigns a ticket from a user.
     *
     * @param username  The username of the assignee.
     * @param ticketId  The ID of the ticket.
     * @param timestamp The timestamp of the action.
     */
    public void unAssignTicket(final String username, final Integer ticketId,
            final String timestamp) {
        ServerUser su = server.getUsers().get(username);
        ServerTicket st = server.getTickets().get(ticketId);

        if (su == null || st == null) {
            return;
        }

        Ticket ticket = st.getTicket();

        if (!ticket.isInProgress()) {
            throw new ValidationException("Only IN_PROGRESS tickets can be unassigned.");
        }

        su.removeAssignedTicket(ticketId);

        st.setAssignedTo("");
        st.setAssignedAt("");

        st.addActionToHistory("DE-ASSIGNED", username, timestamp,
                null, null, null);

        ticket.getUnAssigned();
    }

    /**
     * Adds a comment to a ticket.
     *
     * @param username  The author of the comment.
     * @param ticketId  The ticket ID.
     * @param content   The comment content.
     * @param timestamp The timestamp.
     */
    public void addCommentToTicket(final String username, final Integer ticketId,
            final String content, final String timestamp) {
        ServerUser su = server.getUsers().get(username);
        ServerTicket st = server.getTickets().get(ticketId);

        if (su == null) {
            throw new ValidationException("User not found: " + username);
        }
        if (st == null) {
            throw new ValidationException("Ticket not found: " + ticketId);
        }

        validateTicketComment(su, st, content);

        st.addComment(username, content, timestamp);
    }

    /**
     * Undoes the last added comment on a ticket.
     *
     * @param username The username who added the comment.
     * @param ticketId The ticket ID.
     */
    public void undoAddComment(final String username, final Integer ticketId) {
        ServerUser su = server.getUsers().get(username);
        ServerTicket st = server.getTickets().get(ticketId);

        if (su == null) {
            throw new ValidationException("User not found: " + username);
        }
        if (st == null) {
            throw new ValidationException("Ticket not found: " + ticketId);
        }

        if (st.getTicket().isAnonymous()) {
            throw new ValidationException("Comments are not allowed on anonymous tickets.");
        }

        st.removeLastCommentFrom(su.getUser().getUsername());
    }

    /**
     * Checks if a ticket can be assigned to a user.
     *
     * @param username The username.
     * @param ticketId The ticket ID.
     * @return true if assignment is valid, false otherwise.
     */
    public boolean canAssign(final String username, final Integer ticketId) {
        try {
            ServerUser su = server.getUsers().get(username);
            ServerTicket st = server.getTickets().get(ticketId);
            if (su == null || st == null) {
                return false;
            }
            validateTicketAssignment(su, st.getTicket());
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    private void validateTicketAssignment(final ServerUser serverUser, final Ticket ticket) {
        Developer developer = (Developer) serverUser.getUser();

        if (!checkExpertise(developer, ticket)) {
            String required = getRequiredExpertiseString(ticket);
            throw new ValidationException(String.format(
                    "Developer %s cannot assign ticket %d due to expertise area. Required: %s; "
                            + "Current: %s.",
                    developer.getUsername(), ticket.getId(), required,
                    developer.getExpertiseArea().toString()));
        }

        if (!checkSeniority(developer, ticket) || !checkTicketType(developer, ticket)) {
            String required = getRequiredSeniorityString(ticket);
            throw new ValidationException(String.format(
                    "Developer %s cannot assign ticket %d due to seniority level. Required: %s; "
                            + "Current: %s.",
                    developer.getUsername(), ticket.getId(), required, developer.getSeniority()));
        }

        if (!ticket.isOpened()) {
            throw new ValidationException("Only OPEN tickets can be assigned.");
        }

        Milestone tmpMilestone = checkTicketMilestone(ticket);

        if (tmpMilestone == null) {
            return;
        }

        if (tmpMilestone.getTicketIds().contains(ticket.getId())
                && !serverUser.getAssignedMilestones().contains(tmpMilestone.getName())) {
            throw new ValidationException(String.format(
                    "Developer %s is not assigned to milestone %s.",
                    developer.getUsername(), tmpMilestone.getName()));
        }

        if (server.getMilestones().getServerMilestone(tmpMilestone.getName()).isBlocked()) {
            throw new ValidationException(String.format(
                    "Cannot assign ticket %d from blocked milestone %s.",
                    ticket.getId(), tmpMilestone.getName()));

        }
    }

    private boolean checkExpertise(final Developer developer, final Ticket ticket) {
        int devMask = developer.getExpertiseArea().getBitMask();
        int ticketMask = ticket.getExpertiseArea().getBitMask();
        return (devMask & ticketMask) != 0;
    }

    private String getRequiredExpertiseString(final Ticket ticket) {
        int ticketMask = ticket.getExpertiseArea().getBitMask();
        List<String> reqs = new ArrayList<>();

        for (DevExpertise d : DevExpertise.values()) {
            if ((d.getBitMask() & ticketMask) != 0) {
                reqs.add(d.toString());
            }
        }
        Collections.sort(reqs);
        return String.join(", ", reqs);
    }

    private boolean checkSeniority(final Developer developer, final Ticket ticket) {
        int devLevel = developer.getSeniority().getLevel();
        int requiredLevel = ticket.getBusinessPriority().toSeniorityLevel();
        return devLevel >= requiredLevel;
    }

    private String getRequiredSeniorityString(final Ticket ticket) {
        int priorityReq = ticket.getBusinessPriority().toSeniorityLevel();
        int typeReq = ticket.toSeniorityLevel();
        int minLevel = Math.max(priorityReq, typeReq);

        List<String> reqs = new ArrayList<>();
        for (SeniorityLevel s : SeniorityLevel.values()) {
            if (s.getLevel() >= minLevel) {
                reqs.add(s.toString());
            }
        }
        Collections.sort(reqs);
        return String.join(", ", reqs);
    }

    private boolean checkTicketType(final Developer developer, final Ticket ticket) {
        int devLevel = developer.getSeniority().getLevel();
        int requiredLevel = ticket.toSeniorityLevel();
        return devLevel >= requiredLevel;
    }

    private Milestone checkTicketMilestone(final Ticket ticket) {
        for (Milestone milestone : server.getMilestones().getAllMilestones()) {
            if (milestone != null && milestone.getTicketIds().contains(ticket.getId())) {
                return milestone;
            }
        }
        return null;
    }

    private void validateTicketComment(final ServerUser serverUser,
            final ServerTicket serverTicket, final String content) {

        if (serverTicket.getTicket().isAnonymous()) {
            throw new ValidationException("Comments are not allowed on anonymous tickets.");
        }

        if (serverUser.getUser().isReporter()
                && serverTicket.getTicket().isClosed()) {
            throw new ValidationException("Reporters cannot comment on CLOSED tickets.");
        }

        if (content == null || content.length() < MIN_COMMENT_LENGTH) {
            throw new ValidationException("Comment must be at least 10 characters long.");
        }

        if (!serverUser.isAssignedToTicket(serverTicket.getTicket().getId())
                && serverUser.getUser().isDeveloper()) {
            throw new ValidationException(String.format(
                    "Ticket %d is not assigned to the developer %s.",
                    serverTicket.getTicket().getId(), serverUser.getUser().getUsername()));
        }

        if (!serverUser.isAssignedToTicket(serverTicket.getTicket().getId())
                && serverUser.getUser().isReporter()) {
            throw new ValidationException(String.format(
                    "Reporter %s cannot comment on ticket %d.",
                    serverUser.getUser().getUsername(), serverTicket.getTicket().getId()));
        }

    }
}
