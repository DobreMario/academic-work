package main.users;

import main.enums.Token;
import main.milestones.ServerMilestone;

/**
 * Represents a user with the Reporter role.
 * Reporters can create tickets and add comments but have limited access to
 * other features.
 */
public final class Reporter extends User {

    /**
     * Constructor for creating a Reporter.
     *
     * @param username The reporter's username.
     * @param email    The reporter's email.
     */
    public Reporter(final String username, final String email) {
        super(username, email, "REPORTER");
    }

    @Override
    public boolean isAssociatedWithMilestone(final ServerMilestone sm) {
        return false;
    }

    /**
     * Checks permission to verify lost investors.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkLostInvestors() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to start the testing phase.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkStartTestingPhase() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to report a ticket.
     *
     * @return ACCESS_ALLOWED for reporters.
     */
    public Token checkReportTicket() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to add a comment.
     *
     * @return ACCESS_ALLOWED for reporters.
     */
    public Token checkAddComment() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to undo adding a comment.
     *
     * @return ACCESS_ALLOWED for reporters.
     */
    public Token checkUndoAddComment() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to create a milestone.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkCreateMilestone() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to view a milestone.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkViewMilestone() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to assign a ticket.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkAssignTicket() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to undo assigning a ticket.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkUndoAssignTicket() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to view assigned tickets.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkAssignedTicket() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to change ticket status.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkChangeTicketStatus() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to undo changing ticket status.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkUndoChangeTicketStatus() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to view ticket history.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkViewTicketHistory() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to view notifications.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkViewNotifications() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to search.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkSearch() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate performance report.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkGeneratePerformanceReport() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate ticket risk report.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkGenerateTicketRiskReport() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate resolution efficiency report.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkGenerateResolutionEfficiencyReport() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate customer impact report.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkGenerateCustomerImpactReport() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate app stability report.
     *
     * @return ACCESS_DENIED for reporters.
     */
    public Token checkAppStabilityReport() {
        return Token.ACCESS_DENIED;
    }

    @Override
    public String toString() {
        return "Reporter{"
                + "username='" + getUsername() + '\''
                + ", email='" + getEmail() + '\''
                + '}';
    }
}
