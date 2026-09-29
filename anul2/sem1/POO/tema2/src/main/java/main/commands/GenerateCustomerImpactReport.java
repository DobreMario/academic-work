package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.metrics.MetricCalculator;
import main.metrics.strategies.CustomerImpactStrategy;
import main.tickets.ServerTicket;
import main.tickets.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenerateCustomerImpactReport implements Command {

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

            CustomerImpactStrategy strategy = new CustomerImpactStrategy();
            Map<String, Double> scores = new HashMap<>();

            scores.put("BUG", metricCalculator.calculateCategoryScore(data.getBugs(), strategy));
            scores.put("FEATURE_REQUEST", metricCalculator
                    .calculateCategoryScore(data.getFeatures(), strategy));
            scores.put("UI_FEEDBACK", metricCalculator
                    .calculateCategoryScore(data.getUiFeedback(), strategy));

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode commandResult = mapper.createObjectNode();
            commandResult.put("command", "generateCustomerImpactReport");
            commandResult.put("username", inputData.getUsername());
            commandResult.put("timestamp", inputData.getTimestamp());

            ObjectNode reportNode = metricCalculator.toJsonNode(
                    mapper,
                    data,
                    scores,
                    "customerImpactByType",
                    (node, cat, score) -> node.put(cat, MetricCalculator.round(score)),
                    null);

            commandResult.set("report", reportNode);
            output.add(commandResult);

        } catch (ValidationException e) {
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "generateCustomerImpactReport");
            errorNode.put("username", inputData.getUsername());
            errorNode.put("timestamp", inputData.getTimestamp());
            errorNode.put("error", e.getMessage());
        }
    }
}
