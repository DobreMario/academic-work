package main.users;

import java.util.List;

import main.enums.Token;
import main.milestones.ServerMilestone;

/**
 * Represents a user with the Manager role.
 * Managers have specific permissions regarding milestones and subordinates.
 */
public final class Manager extends User {

    private final String hireDate;
    private final List<String> subordinates;

    /**
     * Constructor for creating a Manager.
     *
     * @param username     The manager's username.
     * @param email        The manager's email.
     * @param hireDate     The hiring date.
     * @param subordinates The list of subordinate usernames.
     */
    public Manager(final String username, final String email, final String hireDate,
            final List<String> subordinates) {
        super(username, email, "MANAGER");
        this.hireDate = hireDate;
        this.subordinates = subordinates;
    }

    @Override
    public boolean isAssociatedWithMilestone(final ServerMilestone sm) {
        return sm.getCreator().equals(this.getUsername());
    }

    /**
     * Checks permission to verify lost investors.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkLostInvestors() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to start the testing phase.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkStartTestingPhase() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to report a ticket.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkReportTicket() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to add a comment.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkAddComment() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to undo adding a comment.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkUndoAddComment() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to create a milestone.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkCreateMilestone() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to view a milestone.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkViewMilestone() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to assign a ticket.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkAssignTicket() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to undo assigning a ticket.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkUndoAssignTicket() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to view assigned tickets.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkAssignedTicket() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to change ticket status.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkChangeTicketStatus() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to undo changing ticket status.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkUndoChangeTicketStatus() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to view ticket history.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkViewTicketHistory() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to view notifications.
     *
     * @return ACCESS_DENIED for managers.
     */
    public Token checkViewNotifications() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to search.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkSearch() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to generate performance report.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkGeneratePerformanceReport() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to generate ticket risk report.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkGenerateTicketRiskReport() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to generate resolution efficiency report.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkGenerateResolutionEfficiencyReport() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to generate customer impact report.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkGenerateCustomerImpactReport() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to generate app stability report.
     *
     * @return ACCESS_ALLOWED for managers.
     */
    public Token checkAppStabilityReport() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Retrieves the hire date.
     *
     * @return The hire date string.
     */
    public String getHireDate() {
        return hireDate;
    }

    /**
     * Retrieves the list of subordinates.
     *
     * @return A list of usernames.
     */
    public List<String> getSubordinates() {
        return subordinates;
    }

    /**
     * Adds a subordinate to the manager's list.
     *
     * @param subordinate The username of the subordinate.
     */
    public void addSubordinate(final String subordinate) {
        this.subordinates.add(subordinate);
    }

    /**
     * Removes a subordinate from the manager's list.
     *
     * @param subordinate The username of the subordinate.
     */
    public void removeSubordinate(final String subordinate) {
        this.subordinates.remove(subordinate);
    }

    @Override
    public String toString() {
        return "Manager{"
                + "username='" + getUsername() + '\''
                + ", email='" + getEmail() + '\''
                + ", hireDate='" + hireDate + '\''
                + ", subordinates=" + subordinates
                + '}';
    }
}
