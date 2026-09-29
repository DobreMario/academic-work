package main.databases;

import main.tickets.ServerTicket;
import main.tickets.Ticket;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Database class that manages the storage and retrieval of tickets.
 */
public final class TicketsDB {
    private final Map<Integer, ServerTicket> tickets = new HashMap<>();

    /**
     * Default constructor.
     */
    public TicketsDB() {
    }

    /**
     * Adds a ServerTicket to the database.
     *
     * @param ticket The ServerTicket instance to be added.
     */
    public void add(final ServerTicket ticket) {
        if (ticket != null) {
            tickets.put(ticket.getTicket().getId(), ticket);
        }
    }

    /**
     * Retrieves a ServerTicket by its ID.
     *
     * @param ticketId The ID of the ticket to retrieve.
     * @return The ServerTicket instance, or null if not found.
     */
    public ServerTicket get(final int ticketId) {
        return tickets.get(ticketId);
    }

    /**
     * Removes a ticket from the database by its ID.
     *
     * @param ticketId The ID of the ticket to remove.
     */
    public void remove(final int ticketId) {
        tickets.remove(ticketId);
    }

    /**
     * Checks if a ticket exists in the database.
     *
     * @param ticketId The ID to check.
     * @return true if the ticket exists, false otherwise.
     */
    public boolean exists(final int ticketId) {
        return tickets.containsKey(ticketId);
    }

    /**
     * Retrieves all ServerTicket objects stored in the database.
     *
     * @return A list of all ServerTicket objects.
     */
    public List<ServerTicket> getAll() {
        return new ArrayList<>(tickets.values());
    }

    /**
     * Retrieves a list of ServerTickets based on a list of IDs.
     *
     * @param ticketIds The list of ticket IDs to retrieve.
     * @return A list of matching ServerTicket objects.
     */
    public List<ServerTicket> getByIds(final List<Integer> ticketIds) {
        List<ServerTicket> result = new ArrayList<>();
        if (ticketIds == null) {
            return result;
        }
        for (Integer id : ticketIds) {
            if (tickets.containsKey(id)) {
                result.add(tickets.get(id));
            }
        }
        return result;
    }

    /**
     * Retrieves all ticket IDs currently in the database.
     *
     * @return A list of all ticket IDs.
     */
    public List<Integer> getAllbyID() {
        return new ArrayList<>(tickets.keySet());
    }

    /**
     * Determines the next available ticket ID based on the current size.
     *
     * @return The next ticket ID.
     */
    public int getNextTicketId() {
        return tickets.size();
    }

    /**
     * Retrieves a list of ticket IDs that match a specific status.
     *
     * @param status The status string to filter by.
     * @return A list of ticket IDs with the matching status.
     */
    public List<Integer> getTicketsByStatus(final String status) {
        List<Integer> result = new ArrayList<>();
        for (ServerTicket st : tickets.values()) {
            Ticket ticket = st.getTicket();
            if (ticket.getStatus().toString().equals(status)) {
                result.add(ticket.getId());
            }
        }
        return result;
    }

    /**
     * Checks if a specific ticket has a specific status.
     *
     * @param id     The ID of the ticket.
     * @param status The status string to check against.
     * @return true if the ticket exists and has the specified status.
     */
    public boolean hasStatus(final Integer id, final String status) {
        return tickets.containsKey(id)
                && tickets.get(id).getTicket().getStatus().toString().equals(status);
    }
}
