package main.commands.searchHelper.tickets;

import main.commands.searchHelper.SearchFilter;
import main.tickets.ServerTicket;
import main.users.ServerUser;

/**
 * Filters tickets relevant to a developer (in assigned milestones).
 */
public final class DeveloperScopeFilter implements SearchFilter<ServerTicket> {
    private final ServerUser developer;

    /**
     * Constructor.
     *
     * @param developer The developer user.
     */
    public DeveloperScopeFilter(final ServerUser developer) {
        this.developer = developer;
    }

    @Override
    public boolean matches(final ServerTicket st) {
        if (!st.getTicket().isOpened()) {
            return false;
        }

        if (!st.isInAMilestone()) {
            return false;
        }

        String ticketMilestoneName = st.getMilestone();
        return developer.getAssignedMilestones().contains(ticketMilestoneName);
    }
}
