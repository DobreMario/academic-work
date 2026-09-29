package main.commands.searchHelper.tickets;

import main.commands.searchHelper.SearchFilter;
import main.tickets.ServerTicket;

/**
 * Filters tickets by business priority.
 */
public final class BusinessPriorityFilter implements SearchFilter<ServerTicket> {
    private final String priority;

    /**
     * Constructor.
     *
     * @param priority The priority to match.
     */
    public BusinessPriorityFilter(final String priority) {
        this.priority = priority;
    }

    @Override
    public boolean matches(final ServerTicket st) {
        return st.getTicket().getBusinessPriority().toString().equals(priority);
    }
}
