package main.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.commands.searchHelper.strategies.SearchStrategy;
import main.commands.searchHelper.strategies.Factory;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.users.ServerUser;

/**
 * Command to execute advanced search.
 */
public final class Search implements Command {

    /**
     * Executes the search command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser user = CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validatePermission(user, user.getUser().checkSearch(), "USER");

            JsonNode filtersNode = inputData.getFilters();
            if (filtersNode == null) {
                ObjectMapper mapper = new ObjectMapper();
                filtersNode = mapper.createObjectNode();
            }

            String entityType = null;
            entityType = filtersNode.get("searchType").asText();

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode resultNode = output.addObject();
            resultNode.put("command", "search");
            resultNode.put("username", inputData.getUsername());
            resultNode.put("timestamp", inputData.getTimestamp());
            resultNode.put("searchType", entityType);

            ArrayNode resultsArray = resultNode.putArray("results");

            SearchStrategy strategy = Factory.getStrategy(entityType);
            strategy.performSearch(user, filtersNode, server, resultsArray, mapper);

        } catch (ValidationException e) {
            if (output.size() > 0 && output.get(output.size() - 1).has("results")) {
                output.remove(output.size() - 1);
            }
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "search");
            errorNode.put("user", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
