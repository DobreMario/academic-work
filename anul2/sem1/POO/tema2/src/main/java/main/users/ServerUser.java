package main.users;

import java.util.List;
import java.util.ArrayList;
import main.databases.MilestonesDB;
import main.milestones.Milestone;

/**
 * Represents a user on the server side.
 * Manages assigned tickets, milestones, and notifications.
 */
public final class ServerUser implements Observer {
    private final User user;
    private final List<Integer> assignedTickets = new ArrayList<>();
    private final List<String> assignedMilestones = new ArrayList<>();
    private final List<String> notifications = new ArrayList<>();
    private final List<Integer> allTickets = new ArrayList<>();

    /**
     * Constructor for ServerUser.
     *
     * @param user The underlying User object.
     */
    public ServerUser(final User user) {
        this.user = user;
    }

    /**
     * Assigns a ticket to the user.
     *
     * @param ticketId The ID of the ticket to assign.
     */
    public void addAssignedTicket(final int ticketId) {
        assignedTickets.add(ticketId);
        allTickets.add(ticketId);
    }

    /**
     * Removes a ticket assignment from the user.
     *
     * @param ticketId The ID of the ticket to remove.
     */
    public void removeAssignedTicket(final int ticketId) {
        assignedTickets.remove((Integer) ticketId);
    }

    /**
     * Checks if the user is assigned to a specific ticket.
     *
     * @param ticketId The ID of the ticket.
     * @return true if assigned, false otherwise.
     */
    public boolean isAssignedToTicket(final int ticketId) {
        return assignedTickets.contains(ticketId);
    }

    /**
     * Assigns a milestone to the user.
     *
     * @param milestoneName The name of the milestone.
     */
    public void addAssignedMilestone(final String milestoneName) {
        assignedMilestones.add(milestoneName);
    }

    /**
     * Retrieves the underlying User object.
     *
     * @return The User object.
     */
    public User getUser() {
        return user;
    }

    /**
     * Retrieves the list of currently assigned ticket IDs.
     *
     * @return A list of ticket IDs.
     */
    public List<Integer> getAssignedTickets() {
        return assignedTickets;
    }

    /**
     * Retrieves all ticket IDs associated with the milestones assigned to this
     * user.
     *
     * @param milestonesDB The database containing milestone information.
     * @return A list of ticket IDs found in the user's assigned milestones.
     */
    public List<Integer> getTicketsFromMilestone(final MilestonesDB milestonesDB) {
        List<Integer> tickets = new ArrayList<>();
        for (String milestoneName : assignedMilestones) {
            Milestone milestone = milestonesDB.get(milestoneName);
            if (milestone != null) {
                tickets.addAll(milestone.getTicketIds());
            }
        }

        return tickets;
    }

    /**
     * Retrieves the list of assigned milestone names.
     *
     * @return A list of milestone names.
     */
    public List<String> getAssignedMilestones() {
        return assignedMilestones;
    }

    /**
     * Retrieves the list of notifications received by the user.
     *
     * @return A list of notification strings.
     */
    public List<String> getNotifications() {
        return notifications;
    }

    /**
     * Retrieves the history of all tickets ever assigned to this user.
     *
     * @return A list of ticket IDs.
     */
    public List<Integer> getAllTickets() {
        return allTickets;
    }

    /**
     * Updates the user with a new notification.
     * Implementation of the Observer pattern.
     *
     * @param notification The notification message.
     */
    @Override
    public void update(final String notification) {
        notifications.add(notification);
    }
}
