package entities.plants;

import fileio.PlantInput;

/**
 * Represents the Ferns plant entity.
 * Defines specific properties for ferns, such as oxygen production and robot
 * interaction.
 */
public final class Ferns extends Plant {

    private static final double ROBOT_STUCK_PROBABILITY = 0.3;
    private static final double BASE_OXYGEN = 0.0;

    /**
     * Default constructor.
     */
    public Ferns() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the plant.
     * @param mass Mass of the plant.
     */
    public Ferns(final String name,
            final double mass) {
        super(name, mass, ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = BASE_OXYGEN;
    }

    /**
     * Constructor based on file input.
     *
     * @param plantInput The input data object.
     */
    public Ferns(final PlantInput plantInput) {
        super(plantInput.getName(),
                plantInput.getMass(),
                ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = BASE_OXYGEN;
    }
}
