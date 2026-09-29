package main.commands.searchHelper.strategies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.databases.Server;
import main.users.ServerUser;

/**
 * Interface defining a search strategy (Ticket vs User).
 */
public interface SearchStrategy {
    /**
     * Executes the search operation.
     *
     * @param currentUser  The user performing the search.
     * @param filters      The JSON node containing filters.
     * @param server       The server instance.
     * @param resultsArray The output array for results.
     * @param mapper       The ObjectMapper.
     */
    void performSearch(ServerUser currentUser, JsonNode filters, Server server,
            ArrayNode resultsArray, ObjectMapper mapper);
}
