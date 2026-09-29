package main.databases;

import main.users.ServerUser;
import main.users.User;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Database class that manages the storage and retrieval of users.
 */
public final class UsersDB {
    private final Map<String, ServerUser> users = new HashMap<>();

    /**
     * Default constructor.
     */
    public UsersDB() {
    }

    /**
     * Adds a ServerUser to the database.
     *
     * @param user The ServerUser instance to be added.
     */
    public void add(final ServerUser user) {
        if (user != null) {
            users.put(user.getUser().getUsername(), user);
        }
    }

    /**
     * Retrieves a ServerUser by username.
     *
     * @param username The username of the user to retrieve.
     * @return The ServerUser instance, or null if not found.
     */
    public ServerUser get(final String username) {
        return users.get(username);
    }

    /**
     * Retrieves the underlying User object by username.
     *
     * @param username The username of the user.
     * @return The User object, or null if not found.
     */
    public User getUser(final String username) {
        ServerUser serverUser = users.get(username);
        if (serverUser != null) {
            return serverUser.getUser();
        }
        return null;
    }

    /**
     * Retrieves a list of all User objects in the database.
     *
     * @return A list containing all User objects.
     */
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        for (ServerUser serverUser : users.values()) {
            allUsers.add(serverUser.getUser());
        }
        return allUsers;
    }

    /**
     * Removes a user from the database by username.
     *
     * @param username The username of the user to remove.
     */
    public void remove(final String username) {
        users.remove(username);
    }

    /**
     * Checks if a user exists in the database.
     *
     * @param username The username to check.
     * @return true if the user exists, false otherwise.
     */
    public boolean exists(final String username) {
        return users.containsKey(username);
    }

    /**
     * Retrieves all ServerUser objects stored in the database.
     *
     * @return A list of all ServerUser objects.
     */
    public List<ServerUser> getAll() {
        return new ArrayList<>(users.values());
    }
}
