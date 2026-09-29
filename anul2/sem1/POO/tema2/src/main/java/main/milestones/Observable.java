package main.milestones;

import main.databases.Server;

/**
 * Interface for the Observer pattern.
 * Allows objects to notify observers about changes.
 */
public interface Observable {
    /**
     * Notifies observers with a message.
     *
     * @param server  The server instance.
     * @param message The message to broadcast.
     */
    void notifyObservers(final Server server, final String message);
}
