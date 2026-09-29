package entities.animals;

import fileio.AnimalInput;

/**
 * Represents Omnivore animals.
 * These animals have a moderate probability of avoiding attacks.
 */
public final class Omnivores extends Animal {

    private static final double NOT_ATTACKED_PROBABILITY = 60.0;

    /**
     * Default constructor.
     */
    public Omnivores() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the animal.
     * @param mass Mass of the animal.
     */
    public Omnivores(final String name,
            final double mass) {
        super(name, mass, NOT_ATTACKED_PROBABILITY);
    }

    /**
     * Constructor based on file input.
     *
     * @param animalInput The input data object.
     */
    public Omnivores(final AnimalInput animalInput) {
        super(animalInput.getName(),
                animalInput.getMass(),
                NOT_ATTACKED_PROBABILITY);
    }
}
