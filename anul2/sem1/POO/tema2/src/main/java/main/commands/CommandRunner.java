package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import main.databases.Server;
import main.fileio.CommandInput;

import java.util.HashMap;
import java.util.Map;

/**
 * Orchestrator class responsible for mapping inputs to commands and executing
 * them.
 */
public final class CommandRunner {
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Constructor. Registers all available commands.
     */
    public CommandRunner() {
        // --- 1. Flow Management ---
        commands.put("lostInvestors", new LostInvestors());
        commands.put("startTestingPhase", new StartTestingPhase());

        // --- 2. Ticket Management ---
        commands.put("reportTicket", new ReportTicket());
        commands.put("viewTickets", new ViewTickets());
        commands.put("addComment", new AddComment());
        commands.put("undoAddComment", new UndoAddComment());

        // --- 3. Milestone Management ---
        commands.put("createMilestone", new CreateMilestone());
        commands.put("viewMilestones", new ViewMilestones());

        // --- 4. Development Actions ---
        commands.put("assignTicket", new AssignTicket());
        commands.put("undoAssignTicket", new UndoAssignTicket());
        commands.put("viewAssignedTickets", new ViewAssignedTickets());
        commands.put("changeStatus", new ChangeStatus());
        commands.put("undoChangeStatus", new UndoChangeStatus());
        commands.put("viewTicketHistory", new ViewTicketHistory());

        // --- 5. Advanced & Notifications ---
        commands.put("viewNotifications", new ViewNotifications());
        commands.put("search", new Search());

        // --- 6. Reports & Statistics ---
        commands.put("generatePerformanceReport", new GeneratePerformanceReport());
        commands.put("generateTicketRiskReport", new GenerateTicketRiskReport());
        commands.put("generateResolutionEfficiencyReport",
                new GenerateResolutionEfficiencyReport());
        commands.put("generateCustomerImpactReport", new GenerateCustomerImpactReport());
        commands.put("appStabilityReport", new AppStabilityReport());
    }

    /**
     * Executes the command specified in the input.
     *
     * @param input  The command input data.
     * @param output The output array node.
     * @param server The server instance.
     */
    public void run(final CommandInput input, final ArrayNode output, final Server server) {
        Command command = commands.get(input.getCommand());

        if (command != null) {
            command.execute(input, output, server);
        } else {
            System.out.println(">>> [WARN] Command: " + input.getCommand() + " is unavailable");
        }
    }
}
