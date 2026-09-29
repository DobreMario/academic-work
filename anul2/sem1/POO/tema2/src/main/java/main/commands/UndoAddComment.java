package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.fileio.CommandInput;
import main.users.ServerUser;
import main.databases.Server;
import main.engines.TicketEngine;
import main.exceptions.ValidationException;

/**
 * Command to undo adding a comment.
 */
public final class UndoAddComment implements Command {
    /**
     * Executes the undoAddComment command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        Integer ticketId = inputData.getTicketId();
        String author = inputData.getUsername();

        if (server.getTickets().exists(ticketId)) {
            TicketEngine te = new TicketEngine(server);
            try {
                ServerUser su = CommandUtils.validateUser(server, author);
                CommandUtils.validateTicket(server, ticketId);
                CommandUtils.validatePermission(su, su.getUser().checkAddComment(),
                        "DEVELOPER or REPORTER");
                te.undoAddComment(author, ticketId);
                System.out.println(">>> [INFO] Comment added to Ticket ID: " + ticketId);
            } catch (ValidationException e) {
                System.err.println(">>> [ERR] " + e.getMessage());
                System.err.println(">>> [ERR] User " + inputData.getUsername()
                        + " can't run this command.");
                ObjectNode errorNode = output.addObject();
                errorNode.put("command", "undoAddComment");
                errorNode.put("username", inputData.getUsername());
                errorNode.put("timestamp", inputData.getTimestamp());
                errorNode.put("error", e.getMessage());
                return;
            }
        } else {
            System.err.println(">>> [ERR] Ticket ID: " + ticketId
                    + " does not exist. Cannot add comment.");
        }
    }

}
