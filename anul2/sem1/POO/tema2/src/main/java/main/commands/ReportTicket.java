package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import main.databases.Server;
import main.fileio.CommandInput;
import main.fileio.TicketInput;
import main.tickets.Factory;
import main.tickets.Ticket;
import main.tickets.ServerTicket;
import main.users.ServerUser;
import main.exceptions.ValidationException;

/**
 * Command to report a new ticket.
 */
public final class ReportTicket implements Command {

    /**
     * Executes the reportTicket command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser su = CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validatePermission(su, su.getUser().checkReportTicket(),
                    "REPORTER or USER");
            if (!server.isTestingPhaseActive(inputData.getTimestamp())) {
                throw new ValidationException(
                        "Tickets can only be reported during testing phases.");
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                TicketInput ticketData = mapper.treeToValue(inputData.getParams(),
                        TicketInput.class);
                ticketData.setId(server.getTickets().getNextTicketId());

                Ticket newTicket = Factory.createTicket(ticketData);

                ServerTicket serverTicket = new ServerTicket(newTicket, inputData.getTimestamp(),
                        newTicket.getReportedBy());
                server.getTickets().add(serverTicket);
                su.addAssignedTicket(newTicket.getId());

                System.out.println(">>> [INFO] Ticket reported successfully: "
                        + newTicket.toString());

            } catch (IllegalArgumentException e) {
                throw new ValidationException(e.getMessage());

            } catch (JsonProcessingException e) {
                System.err.println(">>> [ERR] JSON invalid în parametrii comenzii: "
                        + e.getMessage());
            }

        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "reportTicket");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
