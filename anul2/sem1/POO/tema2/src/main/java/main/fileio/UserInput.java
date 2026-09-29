package main.fileio;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Data Transfer Object used to load user information from JSON files.
 * Contains fields for different user roles (Manager, Developer, etc.).
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class UserInput {
    private String username;
    private String email;
    private String role;

    // MANAGER
    private String hireDate; // and for developer
    private List<String> subordinates;

    // DEVELOPER
    private String expertiseArea;
    private String seniority;

    /**
     * Default constructor for Jackson deserialization.
     */
    public UserInput() {
    }

    /**
     * Retrieves the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the user's email address.
     *
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retrieves the user's role (e.g., MANAGER, DEVELOPER).
     *
     * @return The role.
     */
    public String getRole() {
        return role;
    }

    /**
     * Retrieves the hiring date.
     *
     * @return The hire date.
     */
    public String getHireDate() {
        return hireDate;
    }

    /**
     * Retrieves the list of subordinates (for Managers).
     *
     * @return A list of usernames representing subordinates.
     */
    public List<String> getSubordinates() {
        return subordinates;
    }

    /**
     * Retrieves the expertise area (for Developers).
     *
     * @return The expertise area.
     */
    public String getExpertiseArea() {
        return expertiseArea;
    }

    /**
     * Retrieves the seniority level (for Developers).
     *
     * @return The seniority.
     */
    public String getSeniority() {
        return seniority;
    }
}
