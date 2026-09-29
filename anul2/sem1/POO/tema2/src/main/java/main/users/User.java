package main.users;

import main.enums.Role;
import main.enums.Token;
import main.milestones.ServerMilestone;

/**
 * Abstract base class representing a generic user in the system.
 * Defines the contract for permissions (Token checks) and holds common data.
 */
public abstract class User {
    private final String username;
    private final String email;
    private final Role role;

    /**
     * Constructor for creating a new User.
     *
     * @param username The unique username.
     * @param email    The email address.
     * @param role     The role string (mapped to Role enum).
     */
    public User(final String username, final String email, final String role) {
        this.username = username;
        this.email = email;
        this.role = Role.valueOf(role);
    }

    /**
     * Checks if the user has the REPORTER role.
     *
     * @return true if the user is a reporter.
     */
    public final boolean isReporter() {
        return this.role == Role.REPORTER;
    }

    /**
     * Checks if the user has the DEVELOPER role.
     *
     * @return true if the user is a developer.
     */
    public final boolean isDeveloper() {
        return this.role == Role.DEVELOPER;
    }

    /**
     * Checks if the user has the MANAGER role.
     *
     * @return true if the user is a manager.
     */
    public final boolean isManager() {
        return this.role == Role.MANAGER;
    }

    /**
     * Checks if the user is associated with a specific milestone.
     *
     * @param sm The ServerMilestone to check against.
     * @return true if associated.
     */
    public abstract boolean isAssociatedWithMilestone(ServerMilestone sm);

    /**
     * Checks permission to verify lost investors.
     *
     * @return The access token (ALLOWED/DENIED).
     */
    public abstract Token checkLostInvestors();

    /**
     * Checks permission to start a testing phase.
     *
     * @return The access token.
     */
    public abstract Token checkStartTestingPhase();

    /**
     * Checks permission to report a ticket.
     *
     * @return The access token.
     */
    public abstract Token checkReportTicket();

    /**
     * Checks permission to add a comment.
     *
     * @return The access token.
     */
    public abstract Token checkAddComment();

    /**
     * Checks permission to undo adding a comment.
     *
     * @return The access token.
     */
    public abstract Token checkUndoAddComment();

    /**
     * Checks permission to create a milestone.
     *
     * @return The access token.
     */
    public abstract Token checkCreateMilestone();

    /**
     * Checks permission to view a milestone.
     *
     * @return The access token.
     */
    public abstract Token checkViewMilestone();

    /**
     * Checks permission to assign a ticket.
     *
     * @return The access token.
     */
    public abstract Token checkAssignTicket();

    /**
     * Checks permission to undo assigning a ticket.
     *
     * @return The access token.
     */
    public abstract Token checkUndoAssignTicket();

    /**
     * Checks permission to check assigned tickets.
     *
     * @return The access token.
     */
    public abstract Token checkAssignedTicket();

    /**
     * Checks permission to change ticket status.
     *
     * @return The access token.
     */
    public abstract Token checkChangeTicketStatus();

    /**
     * Checks permission to undo changing ticket status.
     *
     * @return The access token.
     */
    public abstract Token checkUndoChangeTicketStatus();

    /**
     * Checks permission to view ticket history.
     *
     * @return The access token.
     */
    public abstract Token checkViewTicketHistory();

    /**
     * Checks permission to view notifications.
     *
     * @return The access token.
     */
    public abstract Token checkViewNotifications();

    /**
     * Checks permission to use search functionality.
     *
     * @return The access token.
     */
    public abstract Token checkSearch();

    /**
     * Checks permission to generate performance reports.
     *
     * @return The access token.
     */
    public abstract Token checkGeneratePerformanceReport();

    /**
     * Checks permission to generate ticket risk reports.
     *
     * @return The access token.
     */
    public abstract Token checkGenerateTicketRiskReport();

    /**
     * Checks permission to generate resolution efficiency reports.
     *
     * @return The access token.
     */
    public abstract Token checkGenerateResolutionEfficiencyReport();

    /**
     * Checks permission to generate customer impact reports.
     *
     * @return The access token.
     */
    public abstract Token checkGenerateCustomerImpactReport();

    /**
     * Checks permission to generate app stability reports.
     *
     * @return The access token.
     */
    public abstract Token checkAppStabilityReport();

    /**
     * Retrieves the username.
     *
     * @return The username string.
     */
    public final String getUsername() {
        return username;
    }

    /**
     * Retrieves the email.
     *
     * @return The email string.
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Retrieves the user role.
     *
     * @return The Role enum.
     */
    public final Role getRole() {
        return role;
    }

    /**
     * Returns a string representation of the user.
     * This method can be overridden by subclasses.
     *
     * @return A string containing user details.
     */
    @Override
    public String toString() {
        return "User{"
                + "username='" + username + '\''
                + ", email='" + email + '\''
                + ", role=" + role
                + '}';
    }
}
