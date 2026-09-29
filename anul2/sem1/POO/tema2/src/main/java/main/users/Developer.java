package main.users;

import main.enums.DevExpertise;
import main.enums.SeniorityLevel;
import main.enums.Token;
import main.milestones.ServerMilestone;

/**
 * Represents a user with the Developer role.
 * Contains specific fields like expertise area and seniority.
 */
public final class Developer extends User {
    private final String hireDate;
    private final DevExpertise expertiseArea;
    private final SeniorityLevel seniority;

    /**
     * Constructor for creating a Developer.
     *
     * @param username      The developer's username.
     * @param email         The developer's email.
     * @param hireDate      The date of hiring.
     * @param expertiseArea The area of expertise (e.g., JAVA, KOTLIN).
     * @param seniority     The seniority level (e.g., JUNIOR, SENIOR).
     */
    public Developer(final String username, final String email, final String hireDate,
            final String expertiseArea, final String seniority) {
        super(username, email, "DEVELOPER");
        this.hireDate = hireDate;
        this.expertiseArea = DevExpertise.valueOf(expertiseArea);
        this.seniority = SeniorityLevel.valueOf(seniority);
    }

    @Override
    public boolean isAssociatedWithMilestone(final ServerMilestone sm) {
        if (sm.getMilestone().getAssignedDevs() != null) {
            return sm.getMilestone().getAssignedDevs().contains(this.getUsername());
        }
        return false;
    }

    /**
     * Checks permission to verify lost investors.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkLostInvestors() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to start the testing phase.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkStartTestingPhase() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to report a ticket.
     *
     * @return ACCESS_DENIED for developers (usually restricted).
     */
    public Token checkReportTicket() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to add a comment.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkAddComment() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to undo adding a comment.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkUndoAddComment() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to create a milestone.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkCreateMilestone() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to view a milestone.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkViewMilestone() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to assign a ticket.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkAssignTicket() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to undo assigning a ticket.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkUndoAssignTicket() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to view assigned tickets.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkAssignedTicket() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to change ticket status directly (outside of flow).
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkChangeTicketStatus() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to undo changing ticket status.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkUndoChangeTicketStatus() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to view ticket history.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkViewTicketHistory() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to view notifications.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkViewNotifications() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to search.
     *
     * @return ACCESS_ALLOWED for developers.
     */
    public Token checkSearch() {
        return Token.ACCESS_ALLOWED;
    }

    /**
     * Checks permission to generate performance report.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkGeneratePerformanceReport() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate ticket risk report.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkGenerateTicketRiskReport() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate resolution efficiency report.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkGenerateResolutionEfficiencyReport() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate customer impact report.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkGenerateCustomerImpactReport() {
        return Token.ACCESS_DENIED;
    }

    /**
     * Checks permission to generate app stability report.
     *
     * @return ACCESS_DENIED for developers.
     */
    public Token checkAppStabilityReport() {
        return Token.ACCESS_DENIED;
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
     * Retrieves the expertise area.
     *
     * @return The DevExpertise enum.
     */
    public DevExpertise getExpertiseArea() {
        return expertiseArea;
    }

    /**
     * Retrieves the seniority level.
     *
     * @return The SeniorityLevel enum.
     */
    public SeniorityLevel getSeniority() {
        return seniority;
    }

    @Override
    public String toString() {
        return "Developer{"
                + "username='" + getUsername() + '\''
                + ", email='" + getEmail() + '\''
                + ", role=" + getRole()
                + ", hireDate='" + hireDate + '\''
                + ", expertiseArea=" + expertiseArea
                + ", seniority=" + seniority
                + '}';
    }
}
