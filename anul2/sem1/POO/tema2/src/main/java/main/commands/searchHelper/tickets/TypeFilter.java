package main.commands.searchHelper.tickets;

import main.commands.searchHelper.SearchFilter;
import main.tickets.ServerTicket;

/**
 * Filters tickets by type (BUG, FEATURE_REQUEST, etc.).
 */
public final class TypeFilter implements SearchFilter<ServerTicket> {
    private final String type;

    /**
     * Constructor.
     *
     * @param type The ticket type string.
     */
    public TypeFilter(final String type) {
        this.type = type;
    }

    @Override
    public boolean matches(final ServerTicket st) {
        return st.getTicket().getType().equals(type);
    }
}
