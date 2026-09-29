package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.fileio.CommandInput;
import main.users.ServerUser;
import main.databases.Server;
import main.engines.TicketEngine;
import main.exceptions.ValidationException;

/**
 * Command to add a comment to a ticket.
 */
public final class AddComment implements Command {
    /**
     * Executes the addComment command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        Integer ticketId = inputData.getTicketId();
        String author = inputData.getUsername();
        String content = inputData.getComment();
        String timestamp = inputData.getTimestamp();

        if (server.getTickets().exists(ticketId)) {
            TicketEngine te = new TicketEngine(server);
            try {
                ServerUser su = CommandUtils.validateUser(server, author);
                CommandUtils.validateTicket(server, ticketId);
                CommandUtils.validatePermission(su, su.getUser().checkAddComment(),
                        "DEVELOPER or REPORTER");
                te.addCommentToTicket(author, ticketId, content, timestamp);
                System.out.println(">>> [INFO] Comment added to Ticket ID: " + ticketId);
            } catch (ValidationException e) {
                System.err.println(">>> [ERR] " + e.getMessage());
                System.err.println(">>> [ERR] User " + inputData.getUsername()
                        + " can't run this command.");
                ObjectNode errorNode = output.addObject();
                errorNode.put("command", "addComment");
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
