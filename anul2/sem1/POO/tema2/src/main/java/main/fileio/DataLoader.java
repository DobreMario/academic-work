package main.fileio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.databases.Server;
import main.users.User;
import main.users.Factory;
import main.users.ServerUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DataLoader {

    private DataLoader() {
    }

    /**
     * Loads users from a JSON file and adds them to the provided server instance.
     *
     * @param filePath The path to the input file containing user data.
     * @param server   The server instance where users will be loaded.
     */
    public static void loadUsers(final String filePath, final Server server) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<UserInput> inputs = mapper.readValue(
                    new File(filePath),
                    new TypeReference<List<UserInput>>() {
                    });

            for (UserInput input : inputs) {
                try {
                    User user = Factory.createUser(input);
                    ServerUser serverUser = new ServerUser(user);
                    server.getUsers().add(serverUser);
                } catch (Exception e) {
                    System.err.println("Skipping invalid user: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
    }

    /**
     * Loads commands from a JSON file.
     *
     * @param filePath The path to the input file containing commands.
     * @return A list of CommandInput objects loaded from the file.
     */
    public static List<CommandInput> loadCommands(final String filePath) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(
                    new File(filePath),
                    new TypeReference<List<CommandInput>>() {
                    });

        } catch (IOException e) {
            System.err.println("Error reading commands file: " + e.getMessage());
        }

        return new ArrayList<>();
    }
}
