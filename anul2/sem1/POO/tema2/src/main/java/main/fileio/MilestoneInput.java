package main.fileio;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object used to load milestone information from JSON files.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class MilestoneInput {
    private String name;
    private String dueDate;
    private List<String> blockingFor;
    private List<Integer> tickets;
    private List<String> assignedDevs;

    /**
     * Default constructor for Jackson deserialization.
     */
    public MilestoneInput() {
    }

    /**
     * Retrieves the name of the milestone.
     *
     * @return The milestone name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the list of milestones that this milestone is blocking.
     *
     * @return A list of milestone names.
     */
    public List<String> getBlockingFor() {
        return blockingFor;
    }

    /**
     * Retrieves the due date of the milestone.
     *
     * @return The due date as a String.
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Retrieves the list of ticket IDs associated with this milestone.
     *
     * @return A list of ticket IDs.
     */
    public List<Integer> getTickets() {
        return tickets;
    }

    /**
     * Retrieves the list of developers assigned to this milestone.
     *
     * @return A list of developer usernames.
     */
    public List<String> getAssignedDevs() {
        return assignedDevs;
    }
}
