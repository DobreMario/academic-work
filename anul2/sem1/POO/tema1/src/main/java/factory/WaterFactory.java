package factory;

import entities.Water;
import fileio.WaterInput;

/**
 * Factory class responsible for creating Water entities.
 * Utility class, cannot be instantiated.
 */
public final class WaterFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private WaterFactory() {
    }

    /**
     * Creates a Water entity based on the input data.
     * Extracting all necessary fields from the input object.
     *
     * @param waterInput The input data object containing properties.
     * @return A new instance of Water.
     */
    public static Water createWater(final WaterInput waterInput) {
        return new Water(
                waterInput.getName(),
                waterInput.getMass(),
                waterInput.getType(),
                waterInput.getSalinity(),
                waterInput.getPH(),
                waterInput.getPurity(),
                waterInput.getContaminantIndex(),
                waterInput.getTurbidity(),
                waterInput.isFrozen());
    }
}
