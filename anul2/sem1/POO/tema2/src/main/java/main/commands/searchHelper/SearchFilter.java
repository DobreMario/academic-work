package main.commands.searchHelper;

/**
 * Interface defining a generic search filter.
 *
 * @param <T> The type of entity to filter.
 */
public interface SearchFilter<T> {
    /**
     * Checks if the entity matches the filter criteria.
     *
     * @param entity The entity to check.
     * @return true if it matches, false otherwise.
     */
    boolean matches(T entity);
}
