package main.milestones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.time.LocalDate;

/**
 * Represents the core data of a Milestone.
 * Immutable class holding configuration details.
 */
public final class Milestone {
    private final String name;
    private final List<String> blockingFor;
    private final LocalDate dueDate;
    private final List<Integer> ticketIds;
    private final List<String> assignedDevs;

    /**
     * Constructor for Milestone.
     *
     * @param name         The name of the milestone.
     * @param blockingFor  List of milestones this one blocks.
     * @param dueDate      The due date string.
     * @param ticketIds    List of ticket IDs involved.
     * @param assignedDevs List of assigned developer usernames.
     */
    public Milestone(final String name,
            final List<String> blockingFor,
            final String dueDate,
            final List<Integer> ticketIds,
            final List<String> assignedDevs) {
        this.name = name;
        this.blockingFor = blockingFor;
        this.dueDate = LocalDate.parse(dueDate);
        this.ticketIds = ticketIds;
        this.assignedDevs = assignedDevs;
    }

    /**
     * Converts the milestone to a JSON node.
     *
     * @param mapper       The ObjectMapper.
     * @param creationDate The creation date of the milestone.
     * @return An ObjectNode representation.
     */
    public ObjectNode toJsonNode(final ObjectMapper mapper, final LocalDate creationDate) {
        ObjectNode node = mapper.createObjectNode();
        node.put("name", this.name);
        ArrayNode blockingArray = mapper.createArrayNode();
        for (String blocked : this.blockingFor) {
            blockingArray.add(blocked);
        }
        node.set("blockingFor", blockingArray);
        node.put("dueDate", this.dueDate.toString());
        node.put("createdAt", creationDate.toString());
        ArrayNode ticketsArray = mapper.createArrayNode();
        for (Integer id : this.ticketIds) {
            ticketsArray.add(id);
        }
        node.set("tickets", ticketsArray);
        ArrayNode devsArray = mapper.createArrayNode();
        for (String dev : this.assignedDevs) {
            devsArray.add(dev);
        }
        node.set("assignedDevs", devsArray);
        return node;
    }

    /**
     * Gets the milestone name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of milestones blocked by this one.
     *
     * @return List of strings.
     */
    public List<String> getBlockingFor() {
        return blockingFor;
    }

    /**
     * Gets the due date.
     *
     * @return The due date.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Gets the list of ticket IDs.
     *
     * @return List of integers.
     */
    public List<Integer> getTicketIds() {
        return ticketIds;
    }

    /**
     * Gets the list of assigned developers.
     *
     * @return List of usernames.
     */
    public List<String> getAssignedDevs() {
        return assignedDevs;
    }

    @Override
    public String toString() {
        return "Milestone{name='" + name + "', blockingFor=" + blockingFor
                + ", dueDate=" + dueDate + ", ticketIds=" + ticketIds
                + ", assignedDevs=" + assignedDevs + "}";
    }
}
