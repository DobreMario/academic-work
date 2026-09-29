package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.metrics.MetricCalculator;
import main.metrics.strategies.TicketRiskStrategy;
import main.tickets.ServerTicket;
import main.tickets.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenerateTicketRiskReport implements Command {

    private static final double NEGLIGIBLE_THRESHOLD = 24.0;
    private static final double MODERATE_THRESHOLD = 49.0;
    private static final double SIGNIFICANT_THRESHOLD = 74.0;

    private final MetricCalculator metricCalculator = new MetricCalculator();

    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            CommandUtils.validateUser(server, inputData.getUsername());

            List<ServerTicket> eligibleTickets = new ArrayList<>();
            for (ServerTicket st : server.getTickets().getAll()) {
                Ticket t = st.getTicket();
                if (t.isOpened() || t.isInProgress()) {
                    eligibleTickets.add(st);
                }
            }

            MetricCalculator.ReportData data = metricCalculator.aggregateTickets(eligibleTickets);

            TicketRiskStrategy strategy = new TicketRiskStrategy();
            Map<String, Double> scores = new HashMap<>();

            scores.put("BUG", metricCalculator.calculateCategoryScore(data.getBugs(), strategy));
            scores.put("FEATURE_REQUEST", metricCalculator
                    .calculateCategoryScore(data.getFeatures(), strategy));
            scores.put("UI_FEEDBACK", metricCalculator
                    .calculateCategoryScore(data.getUiFeedback(), strategy));

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode commandResult = mapper.createObjectNode();
            commandResult.put("command", "generateTicketRiskReport");
            commandResult.put("username", inputData.getUsername());
            commandResult.put("timestamp", inputData.getTimestamp());

            ObjectNode reportNode = metricCalculator.toJsonNode(
                    mapper,
                    data,
                    scores,
                    "riskByType",
                    (node, category, score) -> node.put(category, getRiskLabel(score)),
                    null);

            commandResult.set("report", reportNode);
            output.add(commandResult);

        } catch (ValidationException e) {
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "generateTicketRiskReport");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }

    private String getRiskLabel(final Double score) {
        double s = (score == null) ? 0.0 : MetricCalculator.round(score);
        if (s <= NEGLIGIBLE_THRESHOLD) {
            return "NEGLIGIBLE";
        }
        if (s <= MODERATE_THRESHOLD) {
            return "MODERATE";
        }
        if (s <= SIGNIFICANT_THRESHOLD) {
            return "SIGNIFICANT";
        }
        return "MAJOR";
    }
}
