package entities.plants;

import fileio.PlantInput;

/**
 * Represents the Gymnosperms plant entity.
 * Defines specific properties for gymnosperms, such as oxygen production and
 * robot interaction.
 */
public final class GymnospermsPlants extends Plant {

    private static final double ROBOT_STUCK_PROBABILITY = 0.6;
    private static final double BASE_OXYGEN = 0.0;

    /**
     * Default constructor.
     */
    public GymnospermsPlants() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the plant.
     * @param mass Mass of the plant.
     */
    public GymnospermsPlants(final String name,
            final double mass) {
        super(name, mass, ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = BASE_OXYGEN;
    }

    /**
     * Constructor based on file input.
     *
     * @param plantInput The input data object.
     */
    public GymnospermsPlants(final PlantInput plantInput) {
        super(plantInput.getName(),
                plantInput.getMass(),
                ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = BASE_OXYGEN;
    }
}
