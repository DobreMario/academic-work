package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.users.ServerUser;

/**
 * Command to view user notifications.
 */
public final class ViewNotifications implements Command {
    /**
     * Executes the viewNotifications command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser user = CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validatePermission(user, user.getUser().checkViewNotifications(),
                    "DEVELOPER");

            ObjectNode resultNode = output.addObject();
            resultNode.put("command", "viewNotifications");
            resultNode.put("username", inputData.getUsername());
            resultNode.put("timestamp", inputData.getTimestamp());

            ArrayNode notificationsArray = resultNode.putArray("notifications");
            for (String notif : user.getNotifications()) {
                notificationsArray.add(notif);
            }

            user.getNotifications().clear();

        } catch (ValidationException e) {
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "viewNotifications");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
