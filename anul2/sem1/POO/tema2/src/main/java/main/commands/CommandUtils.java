package main.commands;

import main.databases.Server;
import main.enums.Token;
import main.exceptions.ValidationException;
import main.tickets.ServerTicket;
import main.users.ServerUser;

/**
 * Utility class for common command validation logic.
 */
public final class CommandUtils {

    private CommandUtils() {
    }

    /**
     * Validates if a user exists.
     *
     * @param server   The server instance.
     * @param username The username to check.
     * @return The ServerUser object.
     */
    public static ServerUser validateUser(final Server server, final String username) {
        ServerUser su = server.getUsers().get(username);
        if (su == null) {
            throw new ValidationException("The user " + username + " does not exist.");
        }
        return su;
    }

    /**
     * Validates permission for an action.
     *
     * @param su           The user attempting the action.
     * @param permission   The permission token returned by the user check.
     * @param requiredRole String description of required role for error message.
     */
    public static void validatePermission(final ServerUser su, final Token permission,
            final String requiredRole) {
        if (permission == Token.ACCESS_DENIED) {
            throw new ValidationException("The user does not have permission to execute this "
                    + "command: required role " + requiredRole + "; user role "
                    + su.getUser().getRole() + ".");
        }
    }

    /**
     * Validates if a ticket exists.
     *
     * @param server   The server instance.
     * @param ticketId The ticket ID.
     * @return The ServerTicket object.
     */
    public static ServerTicket validateTicket(final Server server, final Integer ticketId) {
        ServerTicket st = server.getTickets().get(ticketId);
        if (st == null) {
            throw new ValidationException("Ticket not found: " + ticketId);
        }
        return st;
    }

    /**
     * Validates that a ticket is NOT already in a milestone.
     *
     * @param st The ServerTicket to check.
     */
    public static void validateTicketInMilestone(final ServerTicket st) {
        if (st.isInAMilestone()) {
            throw new ValidationException("Tickets " + st.getTicket().getId()
                    + " already assigned to milestone " + st.getMilestone() + ".");
        }
    }
}
