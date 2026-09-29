package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.metrics.MetricCalculator;
import main.metrics.strategies.ResolutionEfficiencyStrategy;
import main.tickets.ServerTicket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenerateResolutionEfficiencyReport implements Command {

    private final MetricCalculator metricCalculator = new MetricCalculator();

    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            CommandUtils.validateUser(server, inputData.getUsername());

            List<ServerTicket> eligibleTickets = new ArrayList<>();
            for (ServerTicket st : server.getTickets().getAll()) {
                if (st.getTicket().isRezolved() || st.getTicket().isClosed()) {
                    eligibleTickets.add(st);
                }
            }

            MetricCalculator.ReportData data = metricCalculator.aggregateTickets(eligibleTickets);

            ResolutionEfficiencyStrategy strategy = new ResolutionEfficiencyStrategy();
            Map<String, Double> scores = new HashMap<>();

            scores.put("BUG", metricCalculator.calculateCategoryScore(data.getBugs(), strategy));
            scores.put("FEATURE_REQUEST", metricCalculator
                    .calculateCategoryScore(data.getFeatures(), strategy));
            scores.put("UI_FEEDBACK", metricCalculator
                    .calculateCategoryScore(data.getUiFeedback(), strategy));

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode commandResult = mapper.createObjectNode();
            commandResult.put("command", "generateResolutionEfficiencyReport");
            commandResult.put("username", inputData.getUsername());
            commandResult.put("timestamp", inputData.getTimestamp());

            ObjectNode reportNode = metricCalculator.toJsonNode(
                    mapper,
                    data,
                    scores,
                    "efficiencyByType",
                    (node, cat, score) -> node.put(cat, MetricCalculator.round(score)),
                    null);

            commandResult.set("report", reportNode);
            output.add(commandResult);

        } catch (ValidationException e) {
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "generateResolutionEfficiencyReport");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
