package main.users;

public interface Observer {
    /**
     * Updates the observer with a new notification.
     *
     * @param notification The notification message.
     */
    void update(String notification);
}
