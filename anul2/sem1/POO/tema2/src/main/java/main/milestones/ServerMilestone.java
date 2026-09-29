package main.milestones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.tickets.ServerTicket;
import main.users.ServerUser;
import main.databases.Server;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a milestone stored on the server with dynamic state.
 */
public final class ServerMilestone implements Observable {
    private static final double PERCENTAGE_SCALE = 100.0;
    private final Milestone milestone;
    private int activeDays = 1;
    private final String creator;
    private final LocalDate creationDate;
    private final List<String> blockedBy = new ArrayList<>();
    private String finishDate = null;
    private boolean notifiedOneDay = false;
    private boolean notifiedOverdue = false;
    private Integer lastTicket;
    private boolean wasBlocked = false;

    /**
     * Constructor for ServerMilestone.
     *
     * @param milestone    The underlying Milestone object.
     * @param creator      The creator's username.
     * @param creationDate The creation date string.
     */
    public ServerMilestone(final Milestone milestone, final String creator,
            final String creationDate) {
        this.milestone = milestone;
        this.creator = creator;
        this.creationDate = LocalDate.parse(creationDate);
    }

    /**
     * Increments the count of active days.
     */
    public void incrementActiveDays() {
        this.activeDays++;
    }

    /**
     * Checks if the milestone is currently blocked.
     *
     * @return true if blocked.
     */
    public boolean isBlocked() {
        return !blockedBy.isEmpty();
    }

    /**
     * Adds a dependency that blocks this milestone.
     *
     * @param milestoneName The name of the blocking milestone.
     */
    public void addBlockedBy(final String milestoneName) {
        if (!blockedBy.contains(milestoneName)) {
            blockedBy.add(milestoneName);
            this.wasBlocked = true;
        }
    }

    /**
     * Removes a dependency blocking this milestone.
     *
     * @param milestoneName The name of the blocking milestone.
     */
    public void removeBlockedBy(final String milestoneName) {
        blockedBy.remove(milestoneName);
    }

    /**
     * Checks if the milestone is completed (all tickets closed).
     *
     * @param server The server instance to check tickets.
     * @return true if completed.
     */
    public boolean isCompleted(final Server server) {
        for (Integer id : milestone.getTicketIds()) {
            ServerTicket st = server.getTickets().get(id);
            if (st != null && !st.getTicket().isClosed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts to JSON node with current status.
     *
     * @param mapper   The ObjectMapper.
     * @param server   The server instance.
     * @param currDate The current date string.
     * @return An ObjectNode.
     */
    public ObjectNode toJsonNode(final ObjectMapper mapper, final Server server,
            final String currDate) {
        LocalDate currentDate = LocalDate.parse(currDate);
        ObjectNode node = milestone.toJsonNode(mapper, creationDate);

        node.put("createdBy", this.creator);
        node.put("status", isCompleted(server) ? "COMPLETED" : "ACTIVE");
        node.put("isBlocked", isBlocked());

        Integer daysBetween = (int) ChronoUnit.DAYS.between(currentDate, milestone.getDueDate());
        if (finishDate != null) {
            LocalDate finishDateParsed = LocalDate.parse(this.finishDate);
            daysBetween = (int) ChronoUnit.DAYS.between(finishDateParsed, milestone.getDueDate());
        }

        node.put("daysUntilDue", daysBetween < 0 ? 0 : daysBetween + 1);
        node.put("overdueBy", daysBetween < 0 ? -daysBetween + 1 : 0);

        ArrayNode openedTicketsArray = node.putArray("openTickets");
        ArrayNode closedTicketsArray = node.putArray("closedTickets");

        List<Integer> milestoneTicketIds = milestone.getTicketIds();

        for (Integer ticketId : milestoneTicketIds) {
            ServerTicket st = server.getTickets().get(ticketId);

            if (st == null) {
                continue;
            }

            String status = st.getTicket().getStatus().toString();

            switch (status) {
                case "OPEN":
                case "IN_PROGRESS":
                case "RESOLVED":
                    openedTicketsArray.add(ticketId);
                    break;
                case "CLOSED":
                    closedTicketsArray.add(ticketId);
                    break;
                default:
                    break;
            }
        }

        int openCount = openedTicketsArray.size();
        int closedCount = closedTicketsArray.size();
        int totalTickets = openCount + closedCount;
        double rawRatio = (totalTickets == 0) ? 0.0 : ((double) closedCount / totalTickets);
        double percentage = Math.round(rawRatio * PERCENTAGE_SCALE) / PERCENTAGE_SCALE;

        node.put("completionPercentage", percentage);

        ArrayNode repartitionArray = node.putArray("repartition");
        for (String dev : milestone.getAssignedDevs()) {
            ObjectNode devNode = mapper.createObjectNode();
            devNode.put("developer", dev);

            List<Integer> allTickets = server.getUsers().get(dev).getAssignedTickets();
            ArrayNode assignedTickets = devNode.putArray("assignedTickets");

            for (Integer ticketId : allTickets) {
                if (milestone.getTicketIds().contains(ticketId)) {
                    assignedTickets.add(ticketId);
                }
            }
            repartitionArray.add(devNode);
        }

        return node;
    }

    /**
     * Notifies assigned developers.
     *
     * @param server  The server instance.
     * @param message The notification message.
     */
    @Override
    public void notifyObservers(final Server server, final String message) {
        List<String> assignedDevs = milestone.getAssignedDevs();

        for (String username : assignedDevs) {
            ServerUser observer = server.getUsers().get(username);
            if (observer != null) {
                observer.update(message);
            }
        }
    }

    /**
     * Gets the Milestone object.
     *
     * @return The Milestone.
     */
    public Milestone getMilestone() {
        return milestone;
    }

    /**
     * Checks if this milestone contains a specific ticket ID.
     *
     * @param id The ticket ID.
     * @return The ServerMilestone if found, null otherwise.
     */
    public ServerMilestone hasMilestoneByID(final Integer id) {
        if (milestone.getTicketIds().contains(id)) {
            return this;
        }
        return null;
    }

    /**
     * Gets the creator username.
     *
     * @return The creator.
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Gets the creation date.
     *
     * @return The creation date.
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the milestone name.
     *
     * @return The name.
     */
    public String getName() {
        return milestone.getName();
    }

    /**
     * Gets the due date.
     *
     * @return The due date.
     */
    public LocalDate getDueDate() {
        return milestone.getDueDate();
    }

    /**
     * Gets the number of days active.
     *
     * @return Active days.
     */
    public int getActiveDays() {
        return activeDays;
    }

    /**
     * Sets the finish date.
     *
     * @param finishDateString The date string.
     */
    public void setFinishDate(final String finishDateString) {
        this.finishDate = new String(finishDateString);
    }

    /**
     * Gets the finish date.
     *
     * @return The finish date string.
     */
    public String getFinishDate() {
        return finishDate;
    }

    /**
     * Checks if the one-day warning has been sent.
     *
     * @return true if notified.
     */
    public boolean isNotifiedOneDay() {
        return notifiedOneDay;
    }

    /**
     * Sets the one-day warning status.
     *
     * @param status True to mark as notified.
     */
    public void setNotifiedOneDay(final boolean status) {
        this.notifiedOneDay = status;
    }

    /**
     * Checks if the overdue warning has been sent.
     *
     * @return true if notified.
     */
    public boolean isNotifiedOverdue() {
        return notifiedOverdue;
    }

    /**
     * Sets the overdue warning status.
     *
     * @param status True to mark as notified.
     */
    public void setNotifiedOverdue(final boolean status) {
        this.notifiedOverdue = status;
    }

    /**
     * Gets the ID of the last ticket that closed the milestone.
     *
     * @return Ticket ID.
     */
    public Integer getLastTicket() {
        return lastTicket;
    }

    /**
     * Sets the ID of the last ticket.
     *
     * @param id The ticket ID.
     */
    public void setLastTicket(final Integer id) {
        this.lastTicket = id;
    }

    /**
     * Checks if the milestone was ever blocked.
     *
     * @return true if it was blocked.
     */
    public boolean wasBlocked() {
        return wasBlocked;
    }
}
