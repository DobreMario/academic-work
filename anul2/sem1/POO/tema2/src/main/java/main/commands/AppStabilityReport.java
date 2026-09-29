package main.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.databases.Server;
import main.exceptions.ValidationException;
import main.fileio.CommandInput;
import main.metrics.MetricCalculator;
import main.metrics.strategies.CustomerImpactStrategy;
import main.metrics.strategies.TicketRiskStrategy;
import main.tickets.ServerTicket;
import main.tickets.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AppStabilityReport implements Command {

    private static final double NEGLIGIBLE_THRESHOLD = 24.0;
    private static final double MODERATE_THRESHOLD = 49.0;
    private static final double SIGNIFICANT_THRESHOLD = 74.0;
    private static final double IMPACT_THRESHOLD = 50.0;

    private final MetricCalculator metricCalculator = new MetricCalculator();

    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        try {
            CommandUtils.validateUser(server, inputData.getUsername());
            List<ServerTicket> activeTickets = new ArrayList<>();
            for (ServerTicket st : server.getTickets().getAll()) {
                Ticket t = st.getTicket();
                if (t.isOpened() || t.isInProgress()) {
                    activeTickets.add(st);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode commandResult = mapper.createObjectNode();
            commandResult.put("command", "appStabilityReport");
            commandResult.put("username", inputData.getUsername());
            commandResult.put("timestamp", inputData.getTimestamp());

            if (activeTickets.isEmpty()) {
                ObjectNode reportNode = mapper.createObjectNode();
                reportNode.put("appStability", "STABLE");
                commandResult.set("report", reportNode);
                output.add(commandResult);
                return;
            }

            MetricCalculator.ReportData data = metricCalculator.aggregateTickets(activeTickets);

            TicketRiskStrategy riskStrat = new TicketRiskStrategy();
            CustomerImpactStrategy impactStrat = new CustomerImpactStrategy();

            Map<String, Double> riskScores = new HashMap<>();
            riskScores.put("BUG", metricCalculator
                    .calculateCategoryScore(data.getBugs(), riskStrat));
            riskScores.put("FEATURE_REQUEST", metricCalculator
                    .calculateCategoryScore(data.getFeatures(), riskStrat));
            riskScores.put("UI_FEEDBACK", metricCalculator
                    .calculateCategoryScore(data.getUiFeedback(), riskStrat));

            Map<String, Double> impactScores = new HashMap<>();
            impactScores.put("BUG", metricCalculator
                    .calculateCategoryScore(data.getBugs(), impactStrat));
            impactScores.put("FEATURE_REQUEST", metricCalculator
                    .calculateCategoryScore(data.getFeatures(), impactStrat));
            impactScores.put("UI_FEEDBACK", metricCalculator
                    .calculateCategoryScore(data.getUiFeedback(), impactStrat));

            ObjectNode reportNode = metricCalculator.toJsonNode(
                    mapper,
                    data,
                    riskScores,
                    "riskByType",
                    (node, cat, score) -> node.put(cat, getRiskLabel(score)),
                    "open");

            metricCalculator.addScoreNode(
                    mapper,
                    reportNode,
                    impactScores,
                    "impactByType",
                    (node, cat, score) -> node.put(cat, MetricCalculator.round(score)));

            reportNode.put("appStability", determineStability(riskScores, impactScores));

            commandResult.set("report", reportNode);
            output.add(commandResult);

        } catch (ValidationException e) {
            ObjectNode errorNode = output.addObject();
            errorNode.put("command", "appStabilityReport");
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

    private String determineStability(final Map<String, Double> risks,
            final Map<String, Double> impacts) {
        boolean hasSignificantRisk = false;
        boolean allNegligibleRisk = true;
        boolean allLowImpact = true;
        String[] cats = { "BUG", "FEATURE_REQUEST", "UI_FEEDBACK" };

        for (String cat : cats) {
            String label = getRiskLabel(risks.getOrDefault(cat, 0.0));
            double impact = impacts.getOrDefault(cat, 0.0);

            if ("SIGNIFICANT".equals(label) || "MAJOR".equals(label)) {
                hasSignificantRisk = true;
            }
            if (!"NEGLIGIBLE".equals(label)) {
                allNegligibleRisk = false;
            }
            if (impact >= IMPACT_THRESHOLD) {
                allLowImpact = false;
            }
        }

        if (hasSignificantRisk) {
            return "UNSTABLE";
        }
        if (allNegligibleRisk && allLowImpact) {
            return "STABLE";
        }
        return "PARTIALLY STABLE";
    }
}
