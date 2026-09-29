package main.fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Data Transfer Object used to load command information from JSON files.
 * Contains all possible fields for various commands (add user, create ticket,
 * etc.).
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CommandInput {
    private String command;
    private String username;
    private String timestamp;

    private String type;
    private Double value;
    private Integer ticketID;
    private List<String> args;
    private JsonNode params;
    private String comment;

    private String name;
    private List<String> blockingFor;
    private String dueDate;
    private List<Integer> tickets;
    private List<String> assignedDevs;

    private JsonNode filters;

    /**
     * Default constructor for Jackson deserialization.
     */
    public CommandInput() {
    }

    /**
     * Retrieves the command name (e.g., "add_user").
     *
     * @return The command string.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Retrieves the username associated with the command.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the timestamp of the command.
     *
     * @return The timestamp string.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Retrieves the type (used in specific commands like create ticket).
     *
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the value (used for metrics or generic values).
     *
     * @return The value.
     */
    public Double getValue() {
        return value;
    }

    /**
     * Retrieves the ticket ID associated with the command.
     *
     * @return The ticket ID.
     */
    public Integer getTicketId() {
        return ticketID;
    }

    /**
     * Retrieves the list of arguments.
     *
     * @return A list of arguments.
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Retrieves the comment content.
     *
     * @return The comment string.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Retrieves the name (e.g., for milestones or projects).
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the list of items this entity is blocking for.
     *
     * @return A list of blocking items.
     */
    public List<String> getBlockingFor() {
        return blockingFor;
    }

    /**
     * Retrieves the due date.
     *
     * @return The due date string.
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Retrieves the list of ticket IDs.
     *
     * @return A list of ticket IDs.
     */
    public List<Integer> getTickets() {
        return tickets;
    }

    /**
     * Retrieves the list of assigned developers.
     *
     * @return A list of developer usernames.
     */
    public List<String> getAssignedDevs() {
        return assignedDevs;
    }

    /**
     * Retrieves the JSON parameters object.
     *
     * @return A JsonNode containing parameters.
     */
    public JsonNode getParams() {
        return params;
    }

    /**
     * Retrieves the JSON filters object.
     *
     * @return A JsonNode containing filters.
     */
    public JsonNode getFilters() {
        return filters;
    }
}
