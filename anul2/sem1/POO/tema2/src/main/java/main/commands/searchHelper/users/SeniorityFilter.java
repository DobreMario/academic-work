package main.commands.searchHelper.users;

import main.commands.searchHelper.SearchFilter;
import main.users.Developer;
import main.users.ServerUser;

/**
 * Filters developers by seniority level.
 */
public final class SeniorityFilter implements SearchFilter<ServerUser> {
    private final String requiredSeniority;

    /**
     * Constructor.
     *
     * @param seniority The required seniority string.
     */
    public SeniorityFilter(final String seniority) {
        this.requiredSeniority = seniority.toUpperCase();
    }

    @Override
    public boolean matches(final ServerUser serverUser) {
        if (serverUser.getUser().isDeveloper()) {
            Developer dev = (Developer) serverUser.getUser();
            return dev.getSeniority().toString().equals(requiredSeniority);
        }
        return false;
    }
}
