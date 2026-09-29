package main.commands.searchHelper.users;

import main.commands.searchHelper.SearchFilter;
import main.users.ServerUser;

/**
 * Filters users based on performance score.
 */
public final class PerformanceScoreFilter implements SearchFilter<ServerUser> {

    @Override
    public boolean matches(final ServerUser serverUser) {
        // TODO: implement method
        return false;
    }
}
