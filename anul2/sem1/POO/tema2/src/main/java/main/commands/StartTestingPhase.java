package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import main.fileio.CommandInput;
import main.databases.Server;

public final class StartTestingPhase implements Command {

    /**
     * Executes the startTestingPhase command.
     *
     * @param inputData The input data.
     * @param output    The JSON output.
     * @param server    The server instance.
     */
    @Override
    public void execute(final CommandInput inputData, final ArrayNode output, final Server server) {
        server.startNewTestingPhase(inputData.getTimestamp());
    }
}
