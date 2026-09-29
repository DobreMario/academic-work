package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.databases.Server;
import main.fileio.CommandInput;
import main.tickets.ServerTicket;
import main.users.ServerUser;
import main.exceptions.ValidationException;

/**
 * Command to undo a status change on a ticket.
 */
public final class UndoChangeStatus implements Command {

    /**
     * Executes the undoChangeStatus command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser su = CommandUtils.validateUser(server, inputData.getUsername());
            ServerTicket st = CommandUtils.validateTicket(server, inputData.getTicketId());

            if (!su.getAssignedTickets().contains(st.getTicket().getId())) {
                System.err.println(">>> [ERR] Ticket " + st.getTicket().getId()
                        + " is not assigned to user " + su.getUser().getUsername() + ".");
                throw new ValidationException("Ticket " + st.getTicket().getId()
                        + " is not assigned to developer " + su.getUser().getUsername() + ".");
            }

            if (st.getTicket().isInProgress()) {
                System.err.println(">>> [ERR] Cannot change status of a ticket that is "
                        + "IN_PROGRESS.");
                return;
            }

            st.getTicket().decrementStatus();

            st.addActionToHistory("STATUS_CHANGED", inputData.getUsername(),
                    inputData.getTimestamp(),
                    st.getTicket().getNextStatus().toString(),
                    st.getTicket().getStatus().toString(),
                    null);

            System.out.println(">>> [INFO] Ticket " + st.getTicket().getId()
                    + " status changed successfully to " + st.getTicket().getStatus().toString()
                    + ".");

        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "undoChangeStatus");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
