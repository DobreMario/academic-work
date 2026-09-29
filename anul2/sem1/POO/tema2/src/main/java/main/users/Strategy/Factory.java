package main.users.Strategy;

import java.util.Collections;

/**
 * Factory class for creating TicketRetrievalStrategy instances.
 */
public final class Factory {

    private Factory() {
    }

    /**
     * Returns the appropriate strategy based on the user type.
     *
     * @param userType The role of the user (e.g., "DEVELOPER", "MANAGER").
     * @return The corresponding TicketRetrievalStrategy.
     */
    public static TicketRetrievalStrategy getStrategy(final String userType) {
        String type = userType.toUpperCase();

        switch (type) {
            case "DEVELOPER":
                return new DeveloperTicketStrategy();
            case "MANAGER":
                return new ManagerTicketStrategy();
            default:
                // Default strategy: return only directly assigned tickets
                return (user, server) -> {
                    if (user == null) {
                        return Collections.emptyList();
                    }
                    return user.getAssignedTickets();
                };
        }
    }
}
