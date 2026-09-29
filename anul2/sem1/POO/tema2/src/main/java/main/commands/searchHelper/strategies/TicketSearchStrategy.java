package main.commands.searchHelper.strategies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.commands.searchHelper.FilterSet;
import main.commands.searchHelper.tickets.AvailableFilter;
import main.commands.searchHelper.tickets.BusinessPriorityFilter;
import main.commands.searchHelper.tickets.CreatedFilter;
import main.commands.searchHelper.tickets.DeveloperScopeFilter;
import main.commands.searchHelper.tickets.KeywordsFilter;
import main.commands.searchHelper.tickets.TypeFilter;
import main.databases.Server;
import main.tickets.ServerTicket;
import main.users.ServerUser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy implementation for searching tickets.
 */
public final class TicketSearchStrategy implements SearchStrategy {

    @Override
    public void performSearch(final ServerUser currentUser, final JsonNode filtersNode,
            final Server server, final ArrayNode resultsArray,
            final ObjectMapper mapper) {
        FilterSet<ServerTicket> filters = new FilterSet<>();

        if (currentUser.getUser().isDeveloper()) {
            filters.addFilter(new DeveloperScopeFilter(currentUser));
        }

        if (filtersNode.has("type")) {
            filters.addFilter(new TypeFilter(filtersNode.get("type").asText()));
        }
        if (filtersNode.has("businessPriority")) {
            filters.addFilter(
                    new BusinessPriorityFilter(filtersNode.get("businessPriority").asText()));
        }
        if (filtersNode.has("availableForAssignment")
                && filtersNode.get("availableForAssignment").asBoolean()) {
            filters.addFilter(new AvailableFilter(server, currentUser.getUser().getUsername()));
        }

        List<String> searchKeywords = new ArrayList<>();
        if (filtersNode.has("keywords")) {
            JsonNode keywordsNode = filtersNode.get("keywords");
            if (keywordsNode.isArray()) {
                for (JsonNode kw : keywordsNode) {
                    searchKeywords.add(kw.asText());
                }
            } else {
                searchKeywords.add(keywordsNode.asText());
            }
            filters.addFilter(new KeywordsFilter(searchKeywords));
        }

        if (filtersNode.has("createdBefore")) {
            filters.addFilter(new CreatedFilter(filtersNode.get("createdBefore").asText(),
                    "BEFORE"));
        }

        if (filtersNode.has("createdAfter")) {
            filters.addFilter(new CreatedFilter(filtersNode.get("createdAfter").asText(),
                    "AFTER"));
        }

        if (filtersNode.has("createdAt")) {
            filters.addFilter(new CreatedFilter(filtersNode.get("createdAt").asText(), "AT"));
        }
        List<ServerTicket> matches = new ArrayList<>();
        for (ServerTicket st : server.getTickets().getAll()) {
            if (filters.matches(st)) {
                matches.add(st);
            }
        }

        matches.sort(Comparator.comparing(ServerTicket::getCreatedAt)
                .thenComparingInt(st -> st.getTicket().getId()));

        for (ServerTicket st : matches) {
            resultsArray.add(st.toJsonNodeForSearch(mapper, searchKeywords));
        }
    }
}
