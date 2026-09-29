package main.users;

import main.fileio.UserInput;

/**
 * Factory class for creating User instances based on roles.
 * This is a utility class and cannot be instantiated.
 */
public final class Factory {

    private Factory() {
    }

    /**
     * Creates a new User object based on the provided input data.
     *
     * @param input The input data containing user details and role.
     * @return A specific implementation of User (Reporter, Manager, or Developer).
     * @throws IllegalArgumentException If the role provided is invalid.
     */
    public static User createUser(final UserInput input) {
        switch (input.getRole()) {
            case "REPORTER":
                return new Reporter(input.getUsername(),
                        input.getEmail());
            case "MANAGER":
                return new Manager(input.getUsername(),
                        input.getEmail(),
                        input.getHireDate(),
                        input.getSubordinates());
            case "DEVELOPER":
                return new Developer(input.getUsername(),
                        input.getEmail(),
                        input.getHireDate(),
                        input.getExpertiseArea(),
                        input.getSeniority());
            default:
                throw new IllegalArgumentException("Invalid role: " + input.getRole());
        }
    }
}
