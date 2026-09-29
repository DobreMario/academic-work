package main.metrics;

import main.tickets.ServerTicket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Utility class for calculating and aggregating metrics for tickets.
 */
public final class MetricCalculator {

    private static final double MAX_PERCENTAGE = 100.0;

    @FunctionalInterface
    public interface JsonScoreStrategy {
        void addToJson(ObjectNode node, String category, Double score);
    }

    /**
     * Data structure to hold aggregated ticket information.
     */
    public static class ReportData {
        private final List<ServerTicket> activeTickets = new ArrayList<>();
        private final List<ServerTicket> bugs = new ArrayList<>();
        private final List<ServerTicket> features = new ArrayList<>();
        private final List<ServerTicket> uiFeedback = new ArrayList<>();

        private final Map<String, Integer> countByType = new HashMap<>();
        private final Map<String, Integer> countByPriority = new HashMap<>();

        public ReportData() {
            countByType.put("BUG", 0);
            countByType.put("FEATURE_REQUEST", 0);
            countByType.put("UI_FEEDBACK", 0);
            countByPriority.put("LOW", 0);
            countByPriority.put("MEDIUM", 0);
            countByPriority.put("HIGH", 0);
            countByPriority.put("CRITICAL", 0);
        }

        public List<ServerTicket> getActiveTickets() {
            return activeTickets;
        }

        public List<ServerTicket> getBugs() {
            return bugs;
        }

        public List<ServerTicket> getFeatures() {
            return features;
        }

        public List<ServerTicket> getUiFeedback() {
            return uiFeedback;
        }

        public Map<String, Integer> getCountByType() {
            return countByType;
        }

        public Map<String, Integer> getCountByPriority() {
            return countByPriority;
        }
    }

    /**
     * Aggregates a list of tickets into a ReportData structure.
     *
     * @param tickets the list of tickets to aggregate.
     * @return the populated ReportData.
     */
    public ReportData aggregateTickets(final List<ServerTicket> tickets) {
        ReportData data = new ReportData();

        for (ServerTicket t : tickets) {
            data.getActiveTickets().add(t);

            String type = t.getTicket().getType();
            if (type != null) {
                String typeKey = type.toUpperCase();
                data.getCountByType().put(typeKey,
                        data.getCountByType().getOrDefault(typeKey, 0) + 1);

                switch (typeKey) {
                    case "BUG":
                        data.getBugs().add(t);
                        break;
                    case "FEATURE_REQUEST":
                        data.getFeatures().add(t);
                        break;
                    case "UI_FEEDBACK":
                        data.getUiFeedback().add(t);
                        break;
                    default:
                        break;
                }
            }

            String prioKey = t.getTicket().getBusinessPriority().toString();
            data.getCountByPriority().put(prioKey,
                    data.getCountByPriority().getOrDefault(prioKey, 0) + 1);
        }
        return data;
    }

    /**
     * Calculates the normalized category score for a list of tickets based on a
     * strategy.
     *
     * @param tickets  the list of tickets.
     * @param strategy the metric strategy to apply.
     * @return the average normalized score.
     */
    public Double calculateCategoryScore(final List<ServerTicket> tickets,
            final MetricStrategy strategy) {
        if (tickets == null || tickets.isEmpty()) {
            return 0.0;
        }

        List<Double> normalizedScores = new ArrayList<>();

        for (ServerTicket ticket : tickets) {
            Double baseScore = strategy.calculateBaseScore(ticket);
            Double maxValue = strategy.getMaxValue(ticket);
            Double normalized = calculateImpactFinal(baseScore, maxValue);
            normalizedScores.add(normalized);
        }

        return calculateAverageImpact(normalizedScores);
    }

    /**
     * Converts report data and scores into a JSON ObjectNode.
     *
     * @param mapper       the Jackson ObjectMapper.
     * @param data         the aggregated report data.
     * @param scores       the calculated scores.
     * @param scoreKeyName the key name for the score section.
     * @param jsonStrategy the strategy to format the JSON scores.
     * @param prefix       optional prefix for keys (e.g., "open").
     * @return the constructed ObjectNode.
     */
    public ObjectNode toJsonNode(final ObjectMapper mapper, final ReportData data,
            final Map<String, Double> scores, final String scoreKeyName,
            final JsonScoreStrategy jsonStrategy,
            final String prefix) {

        String p = (prefix == null) ? "" : prefix;

        String totalKey = p.isEmpty() ? "totalTickets" : "total" + capitalize(p) + "Tickets";
        String typeKey = p.isEmpty() ? "ticketsByType" : p + "TicketsByType";
        String prioKey = p.isEmpty() ? "ticketsByPriority" : p + "TicketsByPriority";

        ObjectNode reportNode = mapper.createObjectNode();
        reportNode.put(totalKey, data.getActiveTickets().size());

        ObjectNode typeNode = mapper.createObjectNode();
        data.getCountByType().forEach(typeNode::put);
        reportNode.set(typeKey, typeNode);

        ObjectNode priorityNode = mapper.createObjectNode();
        data.getCountByPriority().forEach(priorityNode::put);
        reportNode.set(prioKey, priorityNode);

        addScoreNode(mapper, reportNode, scores, scoreKeyName, jsonStrategy);

        return reportNode;
    }

    /**
     * Adds a score node to an existing JSON root node.
     *
     * @param mapper   the Jackson ObjectMapper.
     * @param root     the root JSON node.
     * @param scores   the map of scores.
     * @param keyName  the key name for the node.
     * @param strategy the JSON formatting strategy.
     */
    public void addScoreNode(final ObjectMapper mapper, final ObjectNode root,
            final Map<String, Double> scores, final String keyName,
            final JsonScoreStrategy strategy) {

        ObjectNode scoreNode = mapper.createObjectNode();

        strategy.addToJson(scoreNode, "BUG", scores.getOrDefault("BUG", 0.0));
        strategy.addToJson(scoreNode, "FEATURE_REQUEST",
                scores.getOrDefault("FEATURE_REQUEST", 0.0));
        strategy.addToJson(scoreNode, "UI_FEEDBACK", scores.getOrDefault("UI_FEEDBACK", 0.0));

        root.set(keyName, scoreNode);
    }

    private Double calculateImpactFinal(final Double baseScore, final Double maxValue) {
        if (maxValue == 0) {
            return 0.0;
        }
        return Math.min(MAX_PERCENTAGE, (baseScore * MAX_PERCENTAGE) / maxValue);
    }

    private Double calculateAverageImpact(final List<Double> scores) {
        return scores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private String capitalize(final String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Rounds a double value to two decimal places.
     *
     * @param value the value to round.
     * @return the rounded value.
     */
    public static double round(final double value) {
        return Math.round(value * MAX_PERCENTAGE) / MAX_PERCENTAGE;
    }
}
