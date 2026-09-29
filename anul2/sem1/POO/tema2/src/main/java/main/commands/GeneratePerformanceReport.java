package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.metrics.DeveloperStatsCalculator;
import main.metrics.DeveloperStatsCalculator.DevStats;
import main.metrics.performance.PerformanceStrategy;
import main.metrics.performance.Factory;
import main.users.Developer;
import main.users.Manager;
import main.users.ServerUser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class GeneratePerformanceReport implements Command {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final double ROUND_FACTOR = 100.0;
    private final DeveloperStatsCalculator statsCalculator = new DeveloperStatsCalculator();

    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            ServerUser managerUser = CommandUtils.validateUser(server, inputData.getUsername());
            Manager manager = (Manager) managerUser.getUser();

            LocalDate commandDate = LocalDate.parse(inputData.getTimestamp(), DATE_FORMATTER);
            LocalDate prevMonthDate = commandDate.minusMonths(1);
            int targetMonth = prevMonthDate.getMonthValue();
            int targetYear = prevMonthDate.getYear();

            List<String> teamMembers = manager.getSubordinates();
            teamMembers.sort(String::compareTo);

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode reportArray = mapper.createArrayNode();

            for (String username : teamMembers) {
                ServerUser devUser = server.getUsers().get(username);
                Developer dev = (Developer) devUser.getUser();

                DevStats stats = statsCalculator.calculateStats(devUser, server,
                        targetMonth, targetYear);
                Double score = 0.0;
                if (stats.getClosedCount() > 0) {
                    PerformanceStrategy strategy = Factory
                            .getStrategy(dev.getSeniority().toString());
                    score = strategy.calculateScore(stats);
                }

                ObjectNode devNode = mapper.createObjectNode();
                devNode.put("username", username);
                devNode.put("closedTickets", stats.getClosedCount());
                devNode.put("averageResolutionTime", round(stats.getAvgResolutionTime()));
                devNode.put("performanceScore", round(score));
                devNode.put("seniority", dev.getSeniority().toString());

                reportArray.add(devNode);
            }

            ObjectNode commandResult = mapper.createObjectNode();
            commandResult.put("command", "generatePerformanceReport");
            commandResult.put("username", inputData.getUsername());
            commandResult.put("timestamp", inputData.getTimestamp());
            commandResult.set("report", reportArray);

            output.add(commandResult);

        } catch (ValidationException e) {
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "generatePerformanceReport");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }

    private double round(final double value) {
        if (Double.isNaN(value)) {
            return 0.0;
        }
        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
    }
}
