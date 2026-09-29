package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Entity;
import entities.ScannedEntity;
import entities.Water;
import entities.airs.Air;
import entities.airs.DesertAir;
import entities.airs.MountainAir;
import entities.airs.PolarAir;
import entities.airs.TemperateAir;
import entities.airs.TropicalAir;
import entities.animals.Animal;
import entities.plants.Plant;
import entities.soils.Soil;
import factory.AirFactory;
import fileio.CommandInput;
import fileio.InputLoader;
import fileio.SimulationInput;
import grid.Grid;
import grid.GridEngine.EntityLayer;
import grid.GridEngine.ScannedLayer;
import grid.InteractionEngine;
import grid.RobotMemory;
import grid.ScannedGrid;

import java.util.List;
import java.util.Map;

/**
 * Controller class that manages the entire simulation lifecycle.
 * Handles commands, time progression, entity interactions, and output
 * generation.
 */
public final class SimulationController {

    // --- Costs & Values ---
    private static final int COST_SCAN = 7;
    private static final int COST_LEARN = 2;
    private static final int COST_IMPROVE = 10;
    private static final double BOOST_OXYGEN = 0.3;
    private static final double BOOST_ORGANIC = 0.3;
    private static final double BOOST_HUMIDITY = 0.2;
    private static final double BOOST_MOISTURE = 0.2;
    private static final double ROUNDING_FACTOR = 100.0;
    private static final double DEFAULT_COUNT = 2.0;

    // --- Commands ---
    private static final String CMD_START = "startSimulation";
    private static final String CMD_END = "endSimulation";
    private static final String CMD_PRINT_ENV = "printEnvConditions";
    private static final String CMD_PRINT_MAP = "printMap";
    private static final String CMD_MOVE = "moveRobot";
    private static final String CMD_RECHARGE = "rechargeBattery";
    private static final String CMD_STATUS = "getEnergyStatus";
    private static final String CMD_WEATHER = "changeWeatherConditions";
    private static final String CMD_SCAN = "scanObject";
    private static final String CMD_LEARN = "learnFact";
    private static final String CMD_KNOWLEDGE = "printKnowledgeBase";
    private static final String CMD_IMPROVE = "improveEnvironment";

    // --- Weather Types ---
    private static final String W_DESERT = "desertStorm";
    private static final String W_POLAR = "polarStorm";
    private static final String W_RAIN = "rainfall";
    private static final String W_SEASON = "newSeason";
    private static final String W_HIKING = "peopleHiking";

    // --- Improvement Types ---
    private static final String IMP_VEGETATION = "Vegetation";
    private static final String IMP_SOIL = "Soil";
    private static final String IMP_HUMIDITY = "Humidity";
    private static final String IMP_MOISTURE = "Moisture";

    // --- Error Messages ---
    private static final String ERR_NOT_STARTED = "ERROR: Simulation not started. Cannot perform action";
    private static final String ERR_ALREADY_STARTED = "ERROR: Simulation already started. Cannot perform action";
    private static final String ERR_CHARGING = "ERROR: Robot still charging. Cannot perform action";
    private static final String ERR_NO_BATTERY = "ERROR: Not enough battery left. Cannot perform action";
    private static final String ERR_NO_ENERGY = "ERROR: Not enough energy to perform action";
    private static final String ERR_UNKNOWN = "ERROR: Unknown command.";
    private static final String ERR_WEATHER = "ERROR: The weather change does not affect the environment. Cannot perform action";
    private static final String ERR_OBJECT_NOT_FOUND = "ERROR: Object not found. Cannot perform action";
    private static final String ERR_SUBJECT_NOT_SAVED = "ERROR: Subject not yet saved. Cannot perform action";
    private static final String ERR_FACT_NOT_SAVED = "ERROR: Fact not yet saved. Cannot perform action";
    private static final String ERR_IMPROVE_TYPE = "ERROR: Improvement type not applicable to the current cell.";

    // --- Output Keys ---
    private static final String KEY_COMMAND = "command";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_OUTPUT = "output";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_SECTION = "section";
    private static final String KEY_TOTAL_OBJ = "totalNrOfObjects";
    private static final String KEY_AIR_Q = "airQuality";
    private static final String KEY_SOIL_Q = "soilQuality";
    private static final String KEY_TOPIC = "topic";
    private static final String KEY_FACTS = "facts";

    private SimulationInput currSimulation = null;
    private Grid grid = null;
    private ScannedGrid scannedGrid = null;
    private int coordonateX = 0;
    private int coordonateY = 0;
    private int simulationIndex = 0;
    private int chargeTimer = 0;
    private int oldTimestamp = -1;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Main entry point for running the simulation based on input commands.
     *
     * @param inputLoader The loader containing simulation data and commands.
     * @return An ArrayNode containing the JSON output of the simulation.
     */
    public ArrayNode runSimulation(final InputLoader inputLoader) {
        List<CommandInput> commands = inputLoader.getCommands();
        ArrayNode output = mapper.createArrayNode();

        for (CommandInput cmd : commands) {
            handleCommand(cmd, inputLoader, output);
        }
        return output;
    }

    private void handleCommand(final CommandInput cmd,
            final InputLoader inputLoader,
            final ArrayNode output) {
        String message = null;
        ObjectNode outputNode = null;
        ArrayNode outputArr = null;

        while (oldTimestamp < cmd.getTimestamp()) {
            oldTimestamp++;
            InteractionEngine.processGridInteractions(scannedGrid, grid, oldTimestamp);
        }

        if (currSimulation == null && !CMD_START.equals(cmd.getCommand())) {
            message = ERR_NOT_STARTED;
        } else if (chargeTimer > cmd.getTimestamp()
                && !CMD_RECHARGE.equals(cmd.getCommand())) {
            message = ERR_CHARGING;
        } else {
            switch (cmd.getCommand()) {
                case CMD_START -> message = startSimulation(inputLoader);
                case CMD_END -> message = endSimulation();
                case CMD_PRINT_ENV -> outputNode = printEnvConditions();
                case CMD_PRINT_MAP -> outputArr = printMap();
                case CMD_MOVE -> message = moveRobot();
                case CMD_RECHARGE -> message = rechargeBattery(cmd);
                case CMD_STATUS -> message = getEnergyStatus();
                case CMD_WEATHER -> message = changeWeatherConditions(cmd);
                case CMD_SCAN -> message = scanObjects(cmd);
                case CMD_LEARN -> message = learnFact(cmd);
                case CMD_KNOWLEDGE -> outputArr = printKnowledgeBase();
                case CMD_IMPROVE -> message = improveEnvironment(cmd);
                default -> message = ERR_UNKNOWN;
            }
        }

        if (message != null && !message.isEmpty()) {
            setOutputNode(cmd, output, message);
        } else if (outputNode != null) {
            setOutputNode(cmd, output, outputNode);
        } else if (outputArr != null) {
            setOutputNode(cmd, output, outputArr);
        }
    }

    private void setOutputNode(final CommandInput cmd,
            final ArrayNode output,
            final String message) {
        ObjectNode cmdNode = mapper.createObjectNode();
        cmdNode.put(KEY_COMMAND, cmd.getCommand());
        cmdNode.put(KEY_MESSAGE, message);
        cmdNode.put(KEY_TIMESTAMP, cmd.getTimestamp());
        output.add(cmdNode);
    }

    private void setOutputNode(final CommandInput cmd,
            final ArrayNode output,
            final ObjectNode outputData) {
        ObjectNode cmdNode = mapper.createObjectNode();
        cmdNode.put(KEY_COMMAND, cmd.getCommand());
        cmdNode.set(KEY_OUTPUT, outputData);
        cmdNode.put(KEY_TIMESTAMP, cmd.getTimestamp());
        output.add(cmdNode);
    }

    private void setOutputNode(final CommandInput cmd,
            final ArrayNode output,
            final ArrayNode outputData) {
        ObjectNode cmdNode = mapper.createObjectNode();
        cmdNode.put(KEY_COMMAND, cmd.getCommand());
        cmdNode.set(KEY_OUTPUT, outputData);
        cmdNode.put(KEY_TIMESTAMP, cmd.getTimestamp());
        output.add(cmdNode);
    }

    private int getScore(final int idx1, final int idx2) {
        if (this.grid.isValidPosition(idx1, idx2)) {
            double count = DEFAULT_COUNT;
            double avg = 0.0;
            List<Entity> entities = this.grid.getAllEntitiesAt(idx1, idx2);

            if (entities == null || entities.isEmpty()) {
                return Integer.MAX_VALUE;
            }

            avg += entities.get(0).getRobotProbability();
            avg += entities.get(entities.size() - 1).getRobotProbability();

            for (int i = 1; i < entities.size() - 1; i++) {
                Entity entity = entities.get(i);
                if (entity.getRobotProbability() > 0.0) {
                    count++;
                    avg += entity.getRobotProbability();
                }
            }

            double result = Math.abs(avg / count);
            result = Math.round(result * ROUNDING_FACTOR) / ROUNDING_FACTOR;
            return (int) Math.round(result);
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private String startSimulation(final InputLoader inputLoader) {
        if (currSimulation != null) {
            return ERR_ALREADY_STARTED;
        }

        currSimulation = inputLoader.getSimulations().get(simulationIndex++);
        String[] parts = currSimulation.getTerritoryDim().split("x");
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);

        grid = new Grid(width, height);
        scannedGrid = new ScannedGrid(width, height);
        grid.init(currSimulation.getTerritorySectionParams());
        scannedGrid.initFromGrid(grid);
        coordonateX = 0;
        coordonateY = 0;
        return "Simulation has started.";
    }

    private String endSimulation() {
        if (currSimulation == null) {
            return ERR_NOT_STARTED;
        }
        currSimulation = null;
        grid = null;
        scannedGrid = null;
        AirFactory.reset();
        RobotMemory.getInstance().clearMemory();
        return "Simulation has ended.";
    }

    private ObjectNode printEnvConditions() {
        ObjectNode outputNode = mapper.createObjectNode();
        List<Entity> entities = grid.getAllEntitiesAt(coordonateX, coordonateY);

        for (Entity entity : entities) {
            outputNode.set(entity.getMainClass(), entity.getEntityNode());
        }
        return outputNode;
    }

    private ArrayNode printMap() {
        ArrayNode outputArr = mapper.createArrayNode();
        for (int i = 0; i < grid.getHeight(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                ObjectNode cellNode = mapper.createObjectNode();
                List<Entity> entities = grid.getAllEntitiesAt(j, i);
                ArrayNode tmpSection = mapper.createArrayNode();
                tmpSection.add(j).add(i);
                cellNode.set(KEY_SECTION, tmpSection);
                if (entities != null) {
                    cellNode.put(KEY_TOTAL_OBJ, entities.size() - 2);
                    cellNode.put(KEY_AIR_Q,
                            entities.get(entities.size() - 1).toStringQuality());
                    cellNode.put(KEY_SOIL_Q, entities.get(0).toStringQuality());
                }
                outputArr.add(cellNode);
            }
        }
        return outputArr;
    }

    private String moveRobot() {
        int upper = getScore(coordonateX, coordonateY + 1);
        int right = getScore(coordonateX + 1, coordonateY);
        int down = getScore(coordonateX, coordonateY - 1);
        int left = getScore(coordonateX - 1, coordonateY);

        int min = Math.min(Math.min(upper, right), Math.min(down, left));

        if (currSimulation.getEnergyPoints() < min) {
            return ERR_NO_BATTERY;
        }

        if (min == upper) {
            coordonateY += 1;
        } else if (min == right) {
            coordonateX += 1;
        } else if (min == down) {
            coordonateY -= 1;
        } else if (min == left) {
            coordonateX -= 1;
        }
        currSimulation.setEnergyPoints(currSimulation.getEnergyPoints() - min);

        return "The robot has successfully moved to position ("
                + coordonateX + ", " + coordonateY + ").";
    }

    private String rechargeBattery(final CommandInput cmd) {
        if (chargeTimer > cmd.getTimestamp()) {
            return ERR_CHARGING;
        }
        chargeTimer = cmd.getTimestamp() + cmd.getTimeToCharge();
        currSimulation.setEnergyPoints(currSimulation.getEnergyPoints() + cmd.getTimeToCharge());
        return "Robot battery is charging.";
    }

    private String getEnergyStatus() {
        return "TerraBot has " + currSimulation.getEnergyPoints() + " energy points left.";
    }

    private String changeWeatherConditions(final CommandInput cmd) {
        switch (cmd.getType()) {
            case W_DESERT -> {
                DesertAir.setDesertStorm(true);
                DesertAir.setDesertStormCount(0);
            }
            case W_POLAR -> {
                PolarAir.setWindSpeed(cmd.getWindSpeed());
                PolarAir.setPolarStormCount(0);
            }
            case W_RAIN -> {
                TropicalAir.setRainfall(cmd.getRainfall());
                TropicalAir.setRainfallCount(0);
            }
            case W_SEASON -> {
                TemperateAir.setSeason(cmd.getSeason());
                TemperateAir.setNewSeasonCount(0);
            }
            case W_HIKING -> {
                MountainAir.setNumberOfHikers(cmd.getNumberOfHikers());
                MountainAir.setHikingEventCount(0);
            }
            default -> {
                return ERR_WEATHER;
            }
        }
        return "The weather has changed.";
    }

    private String scanObjects(final CommandInput cmd) {
        if (currSimulation.getEnergyPoints() < COST_SCAN) {
            return ERR_NO_ENERGY;
        }

        String color = cmd.getColor();
        String sound = cmd.getSound();
        String type;

        if ("none".equals(color)) {
            if (!grid.hasEntity(coordonateX, coordonateY, EntityLayer.WATER)) {
                return ERR_OBJECT_NOT_FOUND;
            }
            Water w = grid.getWaterAt(coordonateX, coordonateY);
            performScanEntity(w, ScannedLayer.WATER, cmd);
            type = "water";
        } else if ("none".equals(sound)) {
            if (!grid.hasEntity(coordonateX, coordonateY, EntityLayer.PLANT)) {
                return ERR_OBJECT_NOT_FOUND;
            }
            Plant p = grid.getPlantAt(coordonateX, coordonateY);
            performScanEntity(p, ScannedLayer.PLANT, cmd);
            type = "a plant";
        } else {
            if (!grid.hasEntity(coordonateX, coordonateY, EntityLayer.ANIMAL)) {
                return ERR_OBJECT_NOT_FOUND;
            }
            Animal a = grid.getAnimalAt(coordonateX, coordonateY);
            performScanEntity(a, ScannedLayer.ANIMAL, cmd);
            type = "an animal";
        }
        currSimulation.setEnergyPoints(currSimulation.getEnergyPoints() - COST_SCAN);

        return "The scanned object is " + type + ".";
    }

    private void performScanEntity(final Entity entity, final ScannedLayer layer,
            final CommandInput cmd) {
        RobotMemory.getInstance().addScannedEntity(entity.getName());
        RobotMemory.getInstance().addToDictionary(entity.getName(), entity.getType());
        scannedGrid.addScannedEntity(coordonateX, coordonateY, layer,
                new ScannedEntity(entity, cmd.getTimestamp()));
    }

    private String learnFact(final CommandInput cmd) {
        if (currSimulation.getEnergyPoints() < COST_LEARN) {
            return ERR_NO_BATTERY;
        }

        RobotMemory memory = RobotMemory.getInstance();
        String key = cmd.getComponents();
        if (!memory.isScanned(key) || memory.haveFact(key, cmd.getSubject())) {
            return ERR_SUBJECT_NOT_SAVED;
        }
        currSimulation.setEnergyPoints(currSimulation.getEnergyPoints() - COST_LEARN);
        memory.addFact(key, cmd.getSubject());
        return "The fact has been successfully saved in the database.";
    }

    private ArrayNode printKnowledgeBase() {
        ArrayNode outputArr = mapper.createArrayNode();
        Map<String, List<String>> memory = RobotMemory.getInstance().getMemory();

        for (Map.Entry<String, List<String>> entry : memory.entrySet()) {
            ObjectNode topicNode = mapper.createObjectNode();
            ArrayNode factsArr = mapper.createArrayNode();
            List<String> factsList = entry.getValue();

            if (factsList == null || factsList.isEmpty()) {
                continue;
            }
            topicNode.put(KEY_TOPIC, entry.getKey());
            for (String fact : factsList) {
                factsArr.add(fact);
            }

            topicNode.set(KEY_FACTS, factsArr);
            outputArr.add(topicNode);
        }

        return outputArr;
    }

    private String improveEnvironment(final CommandInput cmd) {
        if (currSimulation.getEnergyPoints() < COST_IMPROVE) {
            return ERR_NO_BATTERY;
        }

        String name = cmd.getName();
        RobotMemory memory = RobotMemory.getInstance();

        if (!memory.isNameInDictionary(name)) {
            return ERR_SUBJECT_NOT_SAVED;
        }
        if (!memory.haveFact(name)) {
            return ERR_FACT_NOT_SAVED;
        }

        String improvementType = cmd.getImprovementType();
        String mess = null;

        if (improvementType.contains(IMP_VEGETATION)) {
            Air air = grid.getAirAt(coordonateX, coordonateY);
            if (air != null) {
                air.setOxygenLevel(air.getOxygenLevel() + BOOST_OXYGEN);
                mess = "The " + name + " was planted successfully.";
            }
        } else if (improvementType.contains(IMP_SOIL)) {
            Soil soil = grid.getSoilAt(coordonateX, coordonateY);
            if (soil != null) {
                soil.setOrganicMatter(soil.getOrganicMatter() + BOOST_ORGANIC);
                mess = "The soil was successfully fertilized using " + name;
            }
        } else if (improvementType.contains(IMP_HUMIDITY)) {
            Air air = grid.getAirAt(coordonateX, coordonateY);
            if (air != null) {
                air.setHumidity(air.getHumidity() + BOOST_HUMIDITY);
                mess = "The humidity was successfully increased using " + name;
            }
        } else if (improvementType.contains(IMP_MOISTURE)) {
            Soil soil = grid.getSoilAt(coordonateX, coordonateY);
            if (soil != null) {
                soil.setWaterRetention(soil.getWaterRetention() + BOOST_MOISTURE);
                mess = "The moisture was successfully increased using " + name;
            }
        }

        if (mess != null) {
            currSimulation.setEnergyPoints(currSimulation.getEnergyPoints() - COST_IMPROVE);
            return mess;
        }

        return ERR_IMPROVE_TYPE;
    }
}
