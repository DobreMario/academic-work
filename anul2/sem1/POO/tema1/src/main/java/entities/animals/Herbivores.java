package entities.animals;

import fileio.AnimalInput;

/**
 * Represents Herbivore animals.
 * These animals have a specific probability of avoiding attacks.
 */
public final class Herbivores extends Animal {

    private static final double NOT_ATTACKED_PROBABILITY = 85.0;

    /**
     * Default constructor.
     */
    public Herbivores() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the animal.
     * @param mass Mass of the animal.
     */
    public Herbivores(final String name,
            final double mass) {
        super(name, mass, NOT_ATTACKED_PROBABILITY);
    }

    /**
     * Constructor based on file input.
     *
     * @param animalInput The input data object.
     */
    public Herbivores(final AnimalInput animalInput) {
        super(animalInput.getName(),
                animalInput.getMass(),
                NOT_ATTACKED_PROBABILITY);
    }
}
