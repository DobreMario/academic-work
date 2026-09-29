package entities.plants;

import fileio.PlantInput;

/**
 * Represents the Mosses plant entity.
 * Defines specific properties for mosses, such as oxygen production and robot
 * interaction.
 */
public final class Mosses extends Plant {

    private static final double ROBOT_STUCK_PROBABILITY = 0.4;
    private static final double OXYGEN_PRODUCED = 0.8;

    /**
     * Default constructor.
     */
    public Mosses() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the plant.
     * @param mass Mass of the plant.
     */
    public Mosses(final String name,
            final double mass) {
        super(name, mass, ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = OXYGEN_PRODUCED;
    }

    /**
     * Constructor based on file input.
     *
     * @param plantInput The input data object.
     */
    public Mosses(final PlantInput plantInput) {
        super(plantInput.getName(),
                plantInput.getMass(),
                ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = OXYGEN_PRODUCED;
    }
}
