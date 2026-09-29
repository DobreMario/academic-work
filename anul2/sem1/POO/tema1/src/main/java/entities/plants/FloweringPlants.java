package entities.plants;

import fileio.PlantInput;

/**
 * Represents the Flowering Plant entity.
 * Defines specific properties for flowering plants, such as high oxygen
 * production and robot interaction.
 */
public final class FloweringPlants extends Plant {

    private static final double ROBOT_STUCK_PROBABILITY = 0.9;
    private static final double OXYGEN_PRODUCED = 6.0;

    /**
     * Default constructor.
     */
    public FloweringPlants() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the plant.
     * @param mass Mass of the plant.
     */
    public FloweringPlants(final String name,
            final double mass) {
        super(name, mass, ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = OXYGEN_PRODUCED;
    }

    /**
     * Constructor based on file input.
     *
     * @param plantInput The input data object.
     */
    public FloweringPlants(final PlantInput plantInput) {
        super(plantInput.getName(),
                plantInput.getMass(),
                ROBOT_STUCK_PROBABILITY);
        this.oxygenFromPlant = OXYGEN_PRODUCED;
    }
}
