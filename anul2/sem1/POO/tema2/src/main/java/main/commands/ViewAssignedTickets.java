package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.tickets.ServerTicket;
import main.users.ServerUser;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Command to view assigned tickets for a developer.
 */
public final class ViewAssignedTickets implements Command {

    /**
     * Executes the viewAssignedTickets command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser su = CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validatePermission(su, su.getUser().checkAssignedTicket(), "DEVELOPER");
            List<Integer> ticketIds = su.getAssignedTickets();
            List<ServerTicket> tickets = new ArrayList<>();

            for (Integer id : ticketIds) {
                ServerTicket st = server.getTickets().get(id);
                if (st != null) {
                    tickets.add(st);
                }
            }

            tickets.sort(Comparator.comparing(
                    (ServerTicket st) -> st.getTicket().getBusinessPriority().getWeight())
                    .reversed()
                    .thenComparing(ServerTicket::getCreatedAt)
                    .thenComparing(st -> st.getTicket().getId()));

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode commandResult = output.addObject();

            commandResult.put("command", "viewAssignedTickets");
            commandResult.put("username", inputData.getUsername());
            commandResult.put("timestamp", inputData.getTimestamp());

            ArrayNode ticketsArray = commandResult.putArray("assignedTickets");

            for (ServerTicket st : tickets) {
                ticketsArray.add(st.toJsonNodeForDev(mapper));
            }

        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "viewAssignedTickets");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
