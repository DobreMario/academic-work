package entities.plants;

import fileio.PlantInput;

/**
 * Represents the Algae plant entity.
 * Defines specific properties for algae, such as oxygen production and robot
 * interaction.
 */
public final class Algae extends Plant {

    private static final double ROBOT_STUCK_PROBABILITY = 0.2;
    private static final double OXYGEN_PRODUCED = 0.5;

    /**
     * Default constructor.
     */
    public Algae() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the plant.
     * @param mass Mass of the plant.
     */
    public Algae(final String name,
            final double mass) {
        super(name, mass, ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = OXYGEN_PRODUCED;
    }

    /**
     * Constructor based on file input.
     *
     * @param plantInput The input data object.
     */
    public Algae(final PlantInput plantInput) {
        super(plantInput.getName(),
                plantInput.getMass(),
                ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = OXYGEN_PRODUCED;
    }
}
