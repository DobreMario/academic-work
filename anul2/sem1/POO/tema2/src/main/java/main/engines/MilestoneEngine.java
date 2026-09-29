package main.engines;

import main.databases.Server;
import main.milestones.Milestone;
import main.milestones.ServerMilestone;
import main.tickets.ServerTicket;
import main.tickets.Ticket;
import main.users.ServerUser;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Engine responsible for managing milestone logic and updates.
 */
public final class MilestoneEngine {
    private static final int PRIORITY_UPDATE_INTERVAL = 3;
    private final Server server;

    /**
     * Constructor.
     *
     * @param server The server instance.
     */
    public MilestoneEngine(final Server server) {
        this.server = server;
    }

    /**
     * Creates a new milestone and associates tickets and developers.
     *
     * @param milestone       The milestone data.
     * @param creatorUsername The username of the creator.
     * @param timestamp       The creation timestamp.
     */
    public void createMilestone(final Milestone milestone, final String creatorUsername,
            final String timestamp) {
        for (Integer ticketId : milestone.getTicketIds()) {
            ServerTicket st = server.getTickets().get(ticketId);
            if (st != null) {
                st.setInAMilestone(milestone.getName());
            }
        }

        for (String devUsername : milestone.getAssignedDevs()) {
            ServerUser dev = server.getUsers().get(devUsername);
            if (dev != null) {
                dev.addAssignedMilestone(milestone.getName());
            }
        }

        ServerMilestone sm = new ServerMilestone(milestone, creatorUsername, timestamp);
        server.getMilestones().add(sm);

        for (String blockerName : milestone.getBlockingFor()) {
            ServerMilestone blockedMilestone = server.getMilestones()
                    .getServerMilestone(blockerName);
            if (blockedMilestone != null) {
                blockedMilestone.addBlockedBy(milestone.getName());
            }
        }

        String msg = "New milestone " + sm.getName() + " has been created with due date "
                + milestone.getDueDate() + ".";
        sm.notifyObservers(server, msg);
    }

    /**
     * Checks if milestones were completed on the given date via synchronization.
     *
     * @param currentDate The current simulation date.
     */
    public void checkIfWasCompleted(final LocalDate currentDate) {
        for (ServerMilestone sm : server.getMilestones().getAll()) {
            if (sm.isCompleted(server) && sm.getFinishDate() == null) {
                System.out.println(">>> [AUTO] Milestone " + sm.getName()
                        + " ~ completed via SYNC on " + currentDate);
                sm.setFinishDate(currentDate.toString());
                unlockDependentMilestones(sm, currentDate, sm.getLastTicket());
            }
        }
    }

    /**
     * Updates the status of all milestones for the current date.
     *
     * @param currentDate The current simulation date.
     */
    public void updateAllMilestones(final LocalDate currentDate) {
        for (ServerMilestone sm : server.getMilestones().getAll()) {
            processMilestoneUpdate(sm, currentDate);
        }
    }

    private void processMilestoneUpdate(final ServerMilestone sm, final LocalDate currentDate) {
        if (sm.isBlocked() || sm.getFinishDate() != null) {
            return;
        }

        sm.incrementActiveDays();

        if (sm.getActiveDays() % PRIORITY_UPDATE_INTERVAL == 0) {
            increaseTicketsPriority(sm);
            System.out.println(">>> [AUTO] Priority increased for milestone " + sm.getName());
        }

        long daysUntilDue = ChronoUnit.DAYS.between(currentDate, sm.getMilestone().getDueDate());

        if (daysUntilDue == 1 && !sm.isNotifiedOneDay()) {
            setAllTicketsToCritical(sm);
            sm.setNotifiedOneDay(true);

            String msg = "Milestone " + sm.getName()
                    + " is due tomorrow. All unresolved tickets are now CRITICAL.";
            sm.notifyObservers(server, msg);
            System.out.println(">>> [AUTO] " + msg);
        }
    }

    private void unlockDependentMilestones(final ServerMilestone completedMilestone,
            final LocalDate currentDate,
            final Integer closingTicketId) {
        Milestone m = completedMilestone.getMilestone();

        for (String blockedName : m.getBlockingFor()) {
            ServerMilestone blockedMilestone = server.getMilestones()
                    .getServerMilestone(blockedName);

            if (blockedMilestone != null) {
                blockedMilestone.removeBlockedBy(m.getName());

                if (!blockedMilestone.isBlocked()) {
                    sendUnblockedNotification(blockedMilestone, currentDate, closingTicketId);
                }
            }
        }
    }

    private void sendUnblockedNotification(final ServerMilestone sm, final LocalDate currentDate,
            final Integer triggeringTicketId) {
        long daysUntilDue = ChronoUnit.DAYS.between(currentDate, sm.getMilestone().getDueDate());

        String msg;

        if (daysUntilDue < 0) {
            setAllTicketsToCritical(sm);
            sm.setNotifiedOverdue(true);

            msg = "Milestone " + sm.getName()
                    + " was unblocked after due date. All active tickets are now CRITICAL.";
            System.out.println(">>> [AUTO] " + msg);
        } else {
            if (triggeringTicketId != null) {
                msg = "Milestone " + sm.getName() + " is now unblocked as tichet "
                        + triggeringTicketId + " has been CLOSED.";
            } else {
                msg = "Milestone " + sm.getName()
                        + " is now unblocked because dependency finished.";
            }
            System.out.println(">>> [AUTO] " + msg);
        }

        sm.notifyObservers(server, msg);
    }

    private void increaseTicketsPriority(final ServerMilestone sm) {
        for (Integer id : sm.getMilestone().getTicketIds()) {
            ServerTicket st = server.getTickets().get(id);
            if (st != null) {
                Ticket ticket = st.getTicket();
                if (!ticket.isClosed() && !ticket.isRezolved()) {
                    ticket.incrementPriority();
                }
            }
        }
    }

    private void setAllTicketsToCritical(final ServerMilestone sm) {
        for (Integer id : sm.getMilestone().getTicketIds()) {
            ServerTicket st = server.getTickets().get(id);
            if (st != null) {
                st.getTicket().incrementPriorityToCritical();
            }
        }
    }
}
