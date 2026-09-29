package main.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.fileio.CommandInput;
import main.tickets.ServerTicket;
import main.databases.Server;
import main.users.ServerUser;
import main.exceptions.ValidationException;

/**
 * Command to view the history of a ticket.
 */
public final class ViewTicketHistory implements Command {

    /**
     * Executes the viewTicketHistory command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser su = CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validatePermission(su, su.getUser().checkViewTicketHistory(),
                    "DEVELOPER or MANAGER");
            List<ServerTicket> userTickets = new ArrayList<>();
            for (Integer id : su.getAllTickets()) {
                ServerTicket t = server.getTickets().get(id);
                if (t != null) {
                    userTickets.add(t);
                }
            }
            Collections.sort(userTickets, new Comparator<ServerTicket>() {
                @Override
                public int compare(final ServerTicket t1, final ServerTicket t2) {
                    int dateCompare = t1.getCreatedAt().compareTo(t2.getCreatedAt());
                    if (dateCompare != 0) {
                        return dateCompare;
                    }
                    return Integer.compare(t1.getTicket().getId(), t2.getTicket().getId());
                }
            });
            ObjectNode resultNode = output.addObject();
            resultNode.put("command", "viewTicketHistory");
            resultNode.put("username", inputData.getUsername());
            resultNode.put("timestamp", inputData.getTimestamp());

            ArrayNode historyArray = resultNode.putArray("ticketHistory");
            ObjectMapper mapper = new ObjectMapper();
            boolean isManager = su.getUser().isManager();
            for (ServerTicket st : userTickets) {
                boolean isCurrentlyAssigned = su.getAssignedTickets()
                        .contains(st.getTicket().getId());
                ObjectNode ticketNode = st.getHistoryForUser(
                        mapper,
                        inputData.getUsername(),
                        isManager,
                        isCurrentlyAssigned);

                historyArray.add(ticketNode);
            }

        } catch (ValidationException e) {
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "viewTicketHistory");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
