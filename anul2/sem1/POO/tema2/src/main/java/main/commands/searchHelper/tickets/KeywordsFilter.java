package main.commands.searchHelper.tickets;

import main.commands.searchHelper.SearchFilter;
import main.tickets.ServerTicket;
import java.util.List;

/**
 * Filters tickets by keywords found in title or description.
 */
public final class KeywordsFilter implements SearchFilter<ServerTicket> {
    private final List<String> keywords;

    /**
     * Constructor.
     *
     * @param keywords List of keywords to search for.
     */
    public KeywordsFilter(final List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean matches(final ServerTicket st) {
        if (keywords == null || keywords.isEmpty()) {
            return true;
        }

        String title = st.getTicket().getTitle();
        String desc = st.getTicket().getDescription();

        String safeTitle = (title != null) ? title.toLowerCase() : "";
        String safeDesc = (desc != null) ? desc.toLowerCase() : "";
        String content = safeTitle + " " + safeDesc;

        for (String kw : keywords) {
            if (content.contains(kw.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
