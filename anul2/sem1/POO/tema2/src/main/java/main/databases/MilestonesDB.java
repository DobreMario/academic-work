package main.databases;

import main.milestones.Milestone;
import main.milestones.ServerMilestone;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Database class that manages the storage and retrieval of milestones.
 */
public final class MilestonesDB {
    private final Map<String, ServerMilestone> milestones = new HashMap<>();

    /**
     * Default constructor.
     */
    public MilestonesDB() {
    }

    /**
     * Adds a new milestone to the database.
     *
     * @param milestone The ServerMilestone to add.
     */
    public void add(final ServerMilestone milestone) {
        if (milestone != null) {
            milestones.put(milestone.getName(), milestone);
        }
    }

    /**
     * Retrieves a ServerMilestone by its name.
     *
     * @param name The name of the milestone.
     * @return The ServerMilestone object, or null if not found.
     */
    public ServerMilestone getServerMilestone(final String name) {
        return milestones.get(name);
    }

    /**
     * Retrieves the core Milestone object by its name.
     *
     * @param name The name of the milestone.
     * @return The Milestone object.
     */
    public Milestone get(final String name) {
        ServerMilestone sm = milestones.get(name);
        if (sm != null) {
            return sm.getMilestone();
        }
        return null;
    }

    /**
     * Retrieves all ServerMilestone objects stored in the database.
     *
     * @return A list of all ServerMilestone objects.
     */
    public List<ServerMilestone> getAll() {
        return new ArrayList<>(milestones.values());
    }

    /**
     * Retrieves all core Milestone objects stored in the database.
     *
     * @return A list of all Milestone objects.
     */
    public List<Milestone> getAllMilestones() {
        List<Milestone> milestoneList = new ArrayList<>();
        for (ServerMilestone sm : milestones.values()) {
            milestoneList.add(sm.getMilestone());
        }
        return milestoneList;
    }

    /**
     * Retrieves a list of all milestone names.
     *
     * @return A list of strings representing milestone names.
     */
    public List<String> getAllMilestoneNames() {
        return new ArrayList<>(milestones.keySet());
    }

    /**
     * Searches for a milestone by its unique ID.
     * Delegates the search to the ServerMilestone logic.
     *
     * @param id The ID of the milestone to find.
     * @return The ServerMilestone containing the ID, or null if not found.
     */
    public ServerMilestone getMilestoneByID(final Integer id) {
        for (ServerMilestone sm : milestones.values()) {
            ServerMilestone result = sm.hasMilestoneByID(id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
