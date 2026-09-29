package main.commands.searchHelper.users;

import main.commands.searchHelper.SearchFilter;
import main.users.Developer;
import main.users.ServerUser;

/**
 * Filters developers by expertise area.
 */
public final class ExpertiseFilter implements SearchFilter<ServerUser> {
    private final String requiredExpertise;

    /**
     * Constructor.
     *
     * @param expertise The required expertise string.
     */
    public ExpertiseFilter(final String expertise) {
        this.requiredExpertise = expertise.toUpperCase();
    }

    @Override
    public boolean matches(final ServerUser serverUser) {
        if (serverUser.getUser().isDeveloper()) {
            Developer dev = (Developer) serverUser.getUser();
            return dev.getExpertiseArea().toString().equals(requiredExpertise);
        }
        return false;
    }
}
