package main.commands.searchHelper.tickets;

import main.commands.searchHelper.SearchFilter;
import main.tickets.ServerTicket;
import java.time.LocalDate;

/**
 * Filters tickets based on creation date.
 */
public final class CreatedFilter implements SearchFilter<ServerTicket> {
    private final LocalDate date;
    private final String format;

    /**
     * Constructor.
     *
     * @param dateStr The date string.
     * @param format  The comparison format (BEFORE, AFTER, AT).
     */
    public CreatedFilter(final String dateStr, final String format) {
        this.date = LocalDate.parse(dateStr);
        this.format = format;
    }

    @Override
    public boolean matches(final ServerTicket st) {
        switch (format) {
            case "BEFORE":
                return st.getCreatedAt().isBefore(date);
            case "AFTER":
                return st.getCreatedAt().isAfter(date);
            case "AT":
                return st.getCreatedAt().isEqual(date);
            default:
                return false;
        }
    }
}
