package main.milestones;

import main.fileio.MilestoneInput;

/**
 * Factory class for creating Milestone instances.
 * Utility class.
 */
public final class Factory {

    private Factory() {
    }

    /**
     * Creates a Milestone object from input data.
     *
     * @param input The milestone input data.
     * @return A new Milestone instance.
     */
    public static Milestone createMilestone(final MilestoneInput input) {
        return new Milestone(input.getName(),
                input.getBlockingFor(),
                input.getDueDate(),
                input.getTickets(),
                input.getAssignedDevs());
    }
}
