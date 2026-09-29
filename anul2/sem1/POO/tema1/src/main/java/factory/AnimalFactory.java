package factory;

import entities.animals.Animal;
import entities.animals.Carnivores;
import entities.animals.Detritivores;
import entities.animals.Herbivores;
import entities.animals.Omnivores;
import entities.animals.Parasites;
import fileio.AnimalInput;

/**
 * Factory class responsible for creating Animal entities.
 * Utility class, cannot be instantiated.
 */
public final class AnimalFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private AnimalFactory() {
    }

    /**
     * Creates a specific Animal entity based on the input data type.
     *
     * @param animalInput The input data object containing properties.
     * @return A new instance of a specific Animal subclass, or null if type is
     *         unknown.
     */
    public static Animal createAnimal(final AnimalInput animalInput) {
        return switch (animalInput.getType()) {
            case "Carnivores" -> new Carnivores(animalInput);
            case "Herbivores" -> new Herbivores(animalInput);
            case "Omnivores" -> new Omnivores(animalInput);
            case "Detritivores" -> new Detritivores(animalInput);
            case "Parasites" -> new Parasites(animalInput);
            default -> null;
        };
    }
}
