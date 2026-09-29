package main.commands.searchHelper.strategies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.commands.searchHelper.FilterSet;
import main.commands.searchHelper.users.ExpertiseFilter;
import main.commands.searchHelper.users.SeniorityFilter;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.users.Developer;
import main.users.Manager;
import main.users.ServerUser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy implementation for searching users (developers).
 */
public final class UserSearchStrategy implements SearchStrategy {

    @Override
    public void performSearch(final ServerUser currentUser, final JsonNode filtersNode,
            final Server server, final ArrayNode resultsArray,
            final ObjectMapper mapper) {
        if (!currentUser.getUser().isManager()) {
            throw new ValidationException("Only managers can search for users.");
        }

        FilterSet<ServerUser> filters = new FilterSet<>();

        if (filtersNode.has("expertiseArea")) {
            filters.addFilter(new ExpertiseFilter(filtersNode.get("expertiseArea").asText()));
        }
        if (filtersNode.has("seniority")) {
            filters.addFilter(new SeniorityFilter(filtersNode.get("seniority").asText()));
        }

        if (filtersNode.has("performanceScoreAbove")) {
            // Implementation pending
        }
        if (filtersNode.has("performanceScoreBelow")) {
            // Implementation pending
        }

        Manager manager = (Manager) currentUser.getUser();
        List<String> subordinates = manager.getSubordinates();
        List<ServerUser> matchedUsers = new ArrayList<>();

        for (String subName : subordinates) {
            ServerUser subUser = server.getUsers().get(subName);
            if (subUser != null && filters.matches(subUser)) {
                matchedUsers.add(subUser);
            }
        }

        matchedUsers.sort(Comparator.comparing(u -> u.getUser().getUsername()));

        for (ServerUser su : matchedUsers) {
            ObjectNode userNode = mapper.createObjectNode();

            if (su.getUser().isDeveloper()) {
                Developer dev = (Developer) su.getUser();
                userNode.put("username", dev.getUsername());
                userNode.put("expertiseArea", dev.getExpertiseArea().toString());
                userNode.put("seniority", dev.getSeniority().toString());
                userNode.put("performanceScore", 0.0);
                userNode.put("hireDate", dev.getHireDate());
            } else {
                userNode.put("username", su.getUser().getUsername());
            }
            resultsArray.add(userNode);
        }
    }
}
