package factory;

import entities.soils.DesertSoil;
import entities.soils.ForestSoil;
import entities.soils.GrasslandSoil;
import entities.soils.Soil;
import entities.soils.SwampSoil;
import entities.soils.TundraSoil;
import fileio.SoilInput;

/**
 * Factory class responsible for creating Soil entities.
 * Utility class, cannot be instantiated.
 */
public final class SoilFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private SoilFactory() {
    }

    /**
     * Creates a specific Soil entity based on the input data type.
     *
     * @param soilInput The input data object containing properties.
     * @return A new instance of a specific Soil subclass, or null if type is
     *         unknown.
     */
    public static Soil createSoil(final SoilInput soilInput) {
        return switch (soilInput.getType()) {
            case "DesertSoil" -> new DesertSoil(soilInput);
            case "ForestSoil" -> new ForestSoil(soilInput);
            case "GrasslandSoil" -> new GrasslandSoil(soilInput);
            case "TundraSoil" -> new TundraSoil(soilInput);
            case "SwampSoil" -> new SwampSoil(soilInput);
            default -> null;
        };
    }
}
