package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.fileio.CommandInput;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.engines.TicketEngine;

/**
 * Command to assign a ticket to a user.
 */
public final class AssignTicket implements Command {
    /**
     * Executes the assignTicket command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        TicketEngine te = new TicketEngine(server);
        Integer ticketId = inputData.getTicketId();

        try {
            CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validateTicket(server, ticketId);

            te.assignTicket(inputData.getUsername(), ticketId, inputData.getTimestamp());

            System.out.println(
                    ">>> [INFO] Ticket " + ticketId + " assigned to user "
                            + inputData.getUsername() + " successfully.");
        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "assignTicket");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
            return;
        }
    }
}
