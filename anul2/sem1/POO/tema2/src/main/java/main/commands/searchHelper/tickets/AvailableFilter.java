package main.commands.searchHelper.tickets;

import main.commands.searchHelper.SearchFilter;
import main.tickets.ServerTicket;
import main.databases.Server;
import main.engines.TicketEngine;

/**
 * Filters tickets available for assignment to a specific developer.
 */
public final class AvailableFilter implements SearchFilter<ServerTicket> {
    private final Server server;
    private final String developerUsername;

    /**
     * Constructor.
     *
     * @param server            The server instance.
     * @param developerUsername The username of the developer.
     */
    public AvailableFilter(final Server server, final String developerUsername) {
        this.server = server;
        this.developerUsername = developerUsername;
    }

    @Override
    public boolean matches(final ServerTicket st) {
        if (st.getAssignedTo() != null && !st.getAssignedTo().isEmpty()) {
            return false;
        }
        TicketEngine engine = new TicketEngine(server);

        return engine.canAssign(developerUsername, st.getTicket().getId());
    }
}
