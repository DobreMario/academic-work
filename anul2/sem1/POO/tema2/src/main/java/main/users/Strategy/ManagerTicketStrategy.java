package main.users.Strategy;

import main.databases.Server;
import main.users.ServerUser;
import java.util.List;

/**
 * Strategy for retrieving tickets visible to a Manager.
 * Managers can see all tickets in the system.
 */
public final class ManagerTicketStrategy implements TicketRetrievalStrategy {

    /**
     * Retrieves all ticket IDs existing on the server.
     *
     * @param user   The manager user.
     * @param server The server instance.
     * @return A list of all ticket IDs.
     */
    @Override
    public List<Integer> getTicketIds(final ServerUser user, final Server server) {
        return server.getTickets().getAllbyID();
    }
}
