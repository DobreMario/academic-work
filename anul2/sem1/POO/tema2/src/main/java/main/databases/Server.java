package main.databases;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import main.engines.MilestoneEngine;

/**
 * Singleton class representing the main server.
 * Manages databases (Users, Tickets, Milestones) and synchronization logic.
 */
public final class Server {
    private static final int TESTING_DURATION_DAYS = 12;
    private static Server instance = null;
    private boolean isRunning = true;

    private final UsersDB users;
    private final TicketsDB tickets;
    private final MilestonesDB milestones;
    private LocalDate lastTestingStartDate = null;
    private LocalDate lastSyncDate = null;

    private Server() {
        this.users = new UsersDB();
        this.tickets = new TicketsDB();
        this.milestones = new MilestonesDB();
    }

    /**
     * Retrieves the singleton instance of the Server.
     *
     * @return The single Server instance.
     */
    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    /**
     * Sets the start date for a new testing phase.
     *
     * @param date The start date of the testing phase as a String.
     */
    public void startNewTestingPhase(final String date) {
        LocalDate testingDate = LocalDate.parse(date);
        lastTestingStartDate = testingDate;
    }

    /**
     * Checks if the testing phase is currently active based on the given timestamp.
     * The testing phase lasts for a specific duration (12 days).
     *
     * @param currentTimestamp The current date to check against.
     * @return true if the testing phase is active, false otherwise.
     */
    public boolean isTestingPhaseActive(final String currentTimestamp) {
        LocalDate currentDate = LocalDate.parse(currentTimestamp);

        long daysBetween = ChronoUnit.DAYS.between(lastTestingStartDate, currentDate);
        return daysBetween >= 0 && daysBetween < TESTING_DURATION_DAYS;
    }

    /**
     * Synchronizes the server state up to the given date.
     * Updates milestones iteratively day by day if necessary.
     *
     * @param date The target date for synchronization.
     */
    public void sync(final String date) {
        LocalDate syncDate = LocalDate.parse(date);
        if (lastSyncDate == null) {
            lastSyncDate = syncDate;
            return;
        }
        while (lastSyncDate.isBefore(syncDate) || lastSyncDate.isEqual(syncDate)) {
            MilestoneEngine me = new MilestoneEngine(this);
            me.updateAllMilestones(lastSyncDate);

            lastSyncDate = lastSyncDate.plusDays(1);
        }
    }

    /**
     * Performs synchronization checks specifically after a certain date.
     * Checks if milestones were completed.
     *
     * @param date The date to check synchronization for.
     */
    public void syncAfter(final String date) {
        LocalDate syncDate = LocalDate.parse(date);
        if (lastSyncDate == null) {
            lastSyncDate = syncDate;
            return;
        }
        MilestoneEngine me = new MilestoneEngine(this);
        me.checkIfWasCompleted(syncDate);
    }

    /**
     * Retrieves the Users database.
     *
     * @return The UsersDB instance.
     */
    public UsersDB getUsers() {
        return users;
    }

    /**
     * Retrieves the Tickets database.
     *
     * @return The TicketsDB instance.
     */
    public TicketsDB getTickets() {
        return tickets;
    }

    /**
     * Retrieves the Milestones database.
     *
     * @return The MilestonesDB instance.
     */
    public MilestonesDB getMilestones() {
        return milestones;
    }

    /**
     * Retrieves the start date of the last testing phase.
     *
     * @return The start date as a LocalDate.
     */
    public LocalDate getLastTestingStartDate() {
        return lastTestingStartDate;
    }

    /**
     * Resets the server instance.
     * Useful for testing purposes to clear the singleton state.
     */
    public static void reset() {
        instance = new Server();
    }

    /**
     * Checks if the server is currently running.
     *
     * @return true if running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Stops the server execution.
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Retrieves the last synchronization date as a String.
     *
     * @return The last sync date string.
     */
    public String getLastSyncDate() {
        if (lastSyncDate == null) {
            return null;
        }
        return lastSyncDate.toString();
    }
}
