package main.users.Strategy;

import main.databases.Server;
import main.users.ServerUser;
import java.util.List;

/**
 * Interface defining a strategy for retrieving ticket IDs based on user role.
 */
public interface TicketRetrievalStrategy {
    /**
     * Retrieves a list of ticket IDs visible to the user.
     *
     * @param user   The server user requesting the tickets.
     * @param server The server instance.
     * @return A list of integer IDs.
     */
    List<Integer> getTicketIds(final ServerUser user, final Server server);
}
