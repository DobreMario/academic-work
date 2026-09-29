package main.commands;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.databases.Server;
import main.fileio.CommandInput;
import main.tickets.ServerTicket;
import main.users.ServerUser;
import main.exceptions.ValidationException;

/**
 * Command to change the status of a ticket.
 */
public final class ChangeStatus implements Command {

    /**
     * Executes the changeStatus command.
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

            if (st.getTicket().isClosed()) {
                System.err.println(">>> [ERR] Cannot change status of a ticket that is CLOSED.");
                return;
            }

            st.addActionToHistory("STATUS_CHANGED", inputData.getUsername(),
                    inputData.getTimestamp(),
                    st.getTicket().getStatus().toString(),
                    st.getTicket().getNextStatus().toString(),
                    null);

            st.getTicket().incrementStatus();
            if (st.getTicket().isRezolved()) {
                st.setSolvedAt(LocalDate.parse(inputData.getTimestamp()));
            }
            System.out.println(">>> [INFO] Ticket " + st.getTicket().getId()
                    + " status changed successfully to " + st.getTicket().getStatus().toString()
                    + ".");

            if (st.getTicket().isClosed()) {
                server.getMilestones().getMilestoneByID(st.getTicket().getId())
                        .setLastTicket(st.getTicket().getId());
            }

        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "changeStatus");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
