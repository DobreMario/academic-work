package factory;

import entities.plants.Algae;
import entities.plants.Ferns;
import entities.plants.FloweringPlants;
import entities.plants.GymnospermsPlants;
import entities.plants.Mosses;
import entities.plants.Plant;
import fileio.PlantInput;

/**
 * Factory class responsible for creating Plant entities.
 * Utility class, cannot be instantiated.
 */
public final class PlantFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private PlantFactory() {
    }

    /**
     * Creates a specific Plant entity based on the input data type.
     *
     * @param plantInput The input data object containing properties.
     * @return A new instance of a specific Plant subclass, or null if type is
     *         unknown.
     */
    public static Plant createPlant(final PlantInput plantInput) {
        return switch (plantInput.getType()) {
            case "Algae" -> new Algae(plantInput);
            case "Ferns" -> new Ferns(plantInput);
            case "FloweringPlants" -> new FloweringPlants(plantInput);
            case "GymnospermsPlants" -> new GymnospermsPlants(plantInput);
            case "Mosses" -> new Mosses(plantInput);
            default -> null;
        };
    }
}