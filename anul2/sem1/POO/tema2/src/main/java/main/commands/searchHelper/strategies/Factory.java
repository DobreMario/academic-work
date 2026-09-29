package main.commands.searchHelper.strategies;

import main.exceptions.ValidationException;

/**
 * Factory for creating SearchStrategy instances.
 * Utility class.
 */
public final class Factory {

    private Factory() {
    }

    /**
     * Returns the appropriate search strategy based on the type.
     *
     * @param searchType The type of search (TICKET or DEVELOPER).
     * @return The SearchStrategy implementation.
     */
    public static SearchStrategy getStrategy(final String searchType) {
        if (searchType == null) {
            throw new ValidationException("Invalid search type: null");
        }

        switch (searchType.toUpperCase()) {
            case "TICKET":
                return new TicketSearchStrategy();
            case "DEVELOPER":
                return new UserSearchStrategy();
            default:
                throw new ValidationException("Invalid search type: " + searchType);
        }
    }
}
