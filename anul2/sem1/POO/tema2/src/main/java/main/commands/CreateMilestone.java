package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import main.databases.Server;
import main.fileio.CommandInput;
import main.fileio.MilestoneInput;
import main.milestones.Factory;
import main.milestones.Milestone;
import main.tickets.ServerTicket;
import main.users.ServerUser;
import main.exceptions.ValidationException;
import main.engines.MilestoneEngine;

/**
 * Command to create a new milestone.
 */
public final class CreateMilestone implements Command {

    /**
     * Executes the createMilestone command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {

        if (server.isTestingPhaseActive(inputData.getTimestamp())) {
            System.err.println(">>> [ERR] Cannot create milestone during an active testing phase.");
            return;
        }

        try {
            ServerUser su = CommandUtils.validateUser(server, inputData.getUsername());
            CommandUtils.validatePermission(su, su.getUser().checkCreateMilestone(), "MANAGER");

            ObjectMapper mapper = new ObjectMapper();
            MilestoneInput milestoneData = mapper.convertValue(inputData, MilestoneInput.class);

            Milestone milestone = Factory.createMilestone(milestoneData);
            for (Integer ticketId : milestone.getTicketIds()) {
                ServerTicket st = CommandUtils.validateTicket(server, ticketId);
                CommandUtils.validateTicketInMilestone(st);
            }

            MilestoneEngine me = new MilestoneEngine(server);
            me.createMilestone(milestone, su.getUser().getUsername(), inputData.getTimestamp());
            for (Integer ticketId : milestone.getTicketIds()) {
                ServerTicket st = server.getTickets().get(ticketId);
                su.addAssignedTicket(ticketId);
                st.addActionToHistory("ADDED_TO_MILESTONE", su.getUser().getUsername(),
                        inputData.getTimestamp(), null, null, milestone.getName());
            }

            System.out.println(">>> [INFO] Milestone created successfully: "
                    + milestone.toString());

        } catch (ValidationException e) {
            System.err.println(">>> [ERR] " + e.getMessage());
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "createMilestone");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
