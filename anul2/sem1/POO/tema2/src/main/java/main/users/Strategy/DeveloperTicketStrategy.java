package main.users.Strategy;

import main.databases.Server;
import main.users.ServerUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for retrieving tickets visible to a Developer.
 * Developers see tickets from their assigned milestones that are marked as
 * OPEN.
 */
public final class DeveloperTicketStrategy implements TicketRetrievalStrategy {

    /**
     * Retrieves tickets for a developer based on assigned milestones.
     * Filters for tickets that are currently OPEN.
     *
     * @param user   The developer user.
     * @param server The server instance.
     * @return A list of open ticket IDs from assigned milestones.
     */
    @Override
    public List<Integer> getTicketIds(final ServerUser user, final Server server) {
        List<Integer> tickets = server.getUsers().get(user.getUser().getUsername())
                .getTicketsFromMilestone(server.getMilestones());

        List<Integer> openTickets = new ArrayList<>();
        for (Integer ticketId : tickets) {
            if (server.getTickets().get(ticketId).getTicket().isOpened()) {
                openTickets.add(ticketId);
            }
        }

        return openTickets;
    }
}
