package factory;

import entities.airs.Air;
import entities.airs.DesertAir;
import entities.airs.MountainAir;
import entities.airs.PolarAir;
import entities.airs.TemperateAir;
import entities.airs.TropicalAir;
import fileio.AirInput;

/**
 * Factory class responsible for creating Air entities.
 * Utility class, cannot be instantiated.
 */
public final class AirFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private AirFactory() {
    }

    /**
     * Creates a specific Air entity based on the input data type.
     *
     * @param airInput The input data object containing properties.
     * @return A new instance of a specific Air subclass, or null if type is
     *         unknown.
     */
    public static Air createAir(final AirInput airInput) {
        return switch (airInput.getType()) {
            case "DesertAir" -> new DesertAir(airInput);
            case "MountainAir" -> new MountainAir(airInput);
            case "PolarAir" -> new PolarAir(airInput);
            case "TemperateAir" -> new TemperateAir(airInput);
            case "TropicalAir" -> new TropicalAir(airInput);
            default -> null;
        };
    }

    /**
     * Resets the static state (counters, flags) of all Air subclasses.
     * Should be called when restarting the simulation.
     */
    public static void reset() {
        MountainAir.reset();
        DesertAir.reset();
        PolarAir.reset();
        TemperateAir.reset();
        TropicalAir.reset();
    }
}
