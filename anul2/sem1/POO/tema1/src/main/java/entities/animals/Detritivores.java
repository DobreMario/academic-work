package entities.animals;

import fileio.AnimalInput;

/**
 * Represents Detritivore animals.
 * These animals feed on dead organic material and have a very high probability
 * of avoiding attacks.
 */
public final class Detritivores extends Animal {

    private static final double NOT_ATTACKED_PROBABILITY = 90.0;

    /**
     * Default constructor.
     */
    public Detritivores() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the animal.
     * @param mass Mass of the animal.
     */
    public Detritivores(final String name,
            final double mass) {
        super(name, mass, NOT_ATTACKED_PROBABILITY);
    }

    /**
     * Constructor based on file input.
     *
     * @param animalInput The input data object.
     */
    public Detritivores(final AnimalInput animalInput) {
        super(animalInput.getName(),
                animalInput.getMass(),
                NOT_ATTACKED_PROBABILITY);
    }
}
