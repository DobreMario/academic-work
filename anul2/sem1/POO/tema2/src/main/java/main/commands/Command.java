package main.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import main.fileio.CommandInput;
import main.databases.Server;

/**
 * Interface representing an executable command.
 */
public interface Command {
    /**
     * Executes the command.
     *
     * @param inputData The input data for the command.
     * @param output    The JSON output array.
     * @param server    The server instance.
     */
    void execute(CommandInput inputData, ArrayNode output, Server server);
}
