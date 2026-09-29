package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.fileio.CommandInput;
import main.users.ServerUser;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.engines.TicketEngine;

/**
 * Command to undo a ticket assignment.
 */
public final class UndoAssignTicket implements Command {
    /**
     * Executes the undoAssignTicket command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser su = CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validatePermission(su, su.getUser().checkUndoAssignTicket(), "DEVELOPER");
            Integer ticketId = inputData.getTicketId();
            TicketEngine te = new TicketEngine(server);
            te.unAssignTicket(inputData.getUsername(), ticketId, inputData.getTimestamp());

            System.out.println(">>> [INFO] Ticket " + ticketId + " unassigned from user "
                    + inputData.getUsername() + " successfully.");

        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "undoAssignTicket");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
