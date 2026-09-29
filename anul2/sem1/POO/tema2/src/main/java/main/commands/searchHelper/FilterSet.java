package main.commands.searchHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of search filters combined with logical AND.
 *
 * @param <T> The type of entity to filter.
 */
public final class FilterSet<T> implements SearchFilter<T> {
    private final List<SearchFilter<T>> filters = new ArrayList<>();

    /**
     * Adds a filter to the set.
     *
     * @param filter The filter to add.
     */
    public void addFilter(final SearchFilter<T> filter) {
        this.filters.add(filter);
    }

    /**
     * Checks if the entity matches all filters in the set.
     *
     * @param entity The entity to check.
     * @return true if all filters match.
     */
    @Override
    public boolean matches(final T entity) {
        for (SearchFilter<T> filter : filters) {
            if (!filter.matches(entity)) {
                return false;
            }
        }
        return true;
    }
}
