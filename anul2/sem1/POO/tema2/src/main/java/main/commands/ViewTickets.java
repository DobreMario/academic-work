package main.commands;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.users.User;
import main.users.Strategy.TicketRetrievalStrategy;
import main.users.Strategy.Factory;
import main.users.ServerUser;
import main.tickets.ServerTicket;

/**
 * Command to view tickets assigned to a user.
 */
public final class ViewTickets implements Command {

    /**
     * Executes the viewTickets command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser su = CommandUtils.validateUser(server, inputData.getUsername());
            User user = su.getUser();
            TicketRetrievalStrategy strategy = Factory.getStrategy(user.getRole().toString());
            List<Integer> ticketIds = strategy.getTicketIds(su, server);

            System.out.print(">>> [INFO] Ticket ID assigned to user " + inputData.getUsername()
                    + ": ");
            if (ticketIds == null || ticketIds.isEmpty()) {
                System.out.print("None");
            } else {
                for (Integer ticketId : ticketIds) {
                    System.out.print(ticketId + ", ");
                }
            }
            System.out.println();

            ObjectMapper mapper = new ObjectMapper();
            List<ServerTicket> tickets = server.getTickets().getByIds(ticketIds);
            tickets.sort(Comparator.comparing(ServerTicket::getCreatedAt)
                    .thenComparing(st -> st.getTicket().getId()));

            ObjectNode commandResult = output.addObject();
            commandResult.put("command", "viewTickets");
            commandResult.put("username", inputData.getUsername());
            commandResult.put("timestamp", inputData.getTimestamp());

            ArrayNode ticketsArray = commandResult.putArray("tickets");

            for (ServerTicket st : tickets) {
                ticketsArray.add(st.toJsonNode(mapper));
            }

        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "viewTickets");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
