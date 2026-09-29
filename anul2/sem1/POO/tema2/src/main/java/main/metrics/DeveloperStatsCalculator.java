package main.metrics;

import main.databases.Server;
import java.util.ArrayList;
import main.tickets.ServerTicket;
import main.tickets.Ticket;
import main.users.ServerUser;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Calculates raw statistics for a developer based on closed tickets.
 */
public final class DeveloperStatsCalculator {

    /**
     * Internal structure to hold developer statistics.
     */
    public static class DevStats {
        private int closedCount = 0;
        private int highPriorityCount = 0;
        private double avgResolutionTime = 0.0;
        private int bugCount = 0;
        private int featureCount = 0;
        private int uiCount = 0;

        public int getClosedCount() {
            return closedCount;
        }

        public int getHighPriorityCount() {
            return highPriorityCount;
        }

        public double getAvgResolutionTime() {
            return avgResolutionTime;
        }

        public int getBugCount() {
            return bugCount;
        }

        public int getFeatureCount() {
            return featureCount;
        }

        public int getUiCount() {
            return uiCount;
        }
    }

    /**
     * Calculates statistics for a given user within a target month and year.
     *
     * @param devUser     the developer user.
     * @param server      the server instance.
     * @param targetMonth the month to filter by.
     * @param targetYear  the year to filter by.
     * @return a DevStats object containing the calculated data.
     */
    public DevStats calculateStats(final ServerUser devUser, final Server server,
            final int targetMonth, final int targetYear) {
        DevStats stats = new DevStats();
        List<Double> resolutionTimes = new ArrayList<>();

        for (Integer ticketId : devUser.getAllTickets()) {
            ServerTicket st = server.getTickets().get(ticketId);
            Ticket t = st.getTicket();

            if (t.isClosed() && st.getSolvedAt() != null) {
                LocalDate solvedDate = st.getSolvedAt();
                if (solvedDate.getMonthValue() == targetMonth
                        && solvedDate.getYear() == targetYear) {

                    stats.closedCount++;

                    String type = t.getType() != null ? t.getType().toUpperCase() : "";
                    switch (type) {
                        case "BUG":
                            stats.bugCount++;
                            break;
                        case "FEATURE_REQUEST":
                            stats.featureCount++;
                            break;
                        case "UI_FEEDBACK":
                            stats.uiCount++;
                            break;
                        default:
                            break;
                    }

                    if (t.isHighPriority()) {
                        stats.highPriorityCount++;
                    }

                    Long days = ChronoUnit.DAYS.between(st.getAssignedAt(), solvedDate);
                    resolutionTimes.add(days + 1.0);
                }
            }
        }

        if (!resolutionTimes.isEmpty()) {
            stats.avgResolutionTime = resolutionTimes.stream()
                    .mapToDouble(Double::doubleValue).average().orElse(0.0);
        }

        return stats;
    }
}
