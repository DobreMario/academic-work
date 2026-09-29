package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.milestones.ServerMilestone;
import main.users.ServerUser;
import main.users.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Command to view milestones associated with a user.
 */
public final class ViewMilestones implements Command {

    /**
     * Executes the viewMilestones command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser su = CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validatePermission(su, su.getUser().checkViewMilestone(), "USER");

            User user = su.getUser();

            List<ServerMilestone> milestonesToShow = new ArrayList<>();
            for (ServerMilestone sm : server.getMilestones().getAll()) {
                if (user.isAssociatedWithMilestone(sm)) {
                    milestonesToShow.add(sm);
                }
            }

            milestonesToShow.sort(Comparator.comparing(ServerMilestone::getDueDate)
                    .thenComparing(ServerMilestone::getName));

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode commandResult = output.addObject();

            commandResult.put("command", "viewMilestones");
            commandResult.put("username", inputData.getUsername());
            commandResult.put("timestamp", inputData.getTimestamp());

            ArrayNode milestonesArray = commandResult.putArray("milestones");

            for (ServerMilestone sm : milestonesToShow) {
                milestonesArray.add(sm.toJsonNode(mapper, server, inputData.getTimestamp()));
            }

        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "viewMilestones");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
