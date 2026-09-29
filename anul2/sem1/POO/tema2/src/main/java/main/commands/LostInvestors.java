package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import main.databases.Server;
import main.fileio.CommandInput;
import main.users.User;

/**
 * Command to handle lost investors scenario.
 */
public final class LostInvestors implements Command {
    /**
     * Executes the lostInvestors command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output,
            final Server server) {

        User user = server.getUsers().get(inputData.getUsername()).getUser();
        if (user.checkLostInvestors().getValue() == -1) {
            System.err.println(">>> [ERROR] User " + inputData.getUsername()
                    + " can't run this command.");
            return;
        }

        System.out.println(">>> [INFO] Executing LostInvestors command...");
        server.stop();
    }
}
