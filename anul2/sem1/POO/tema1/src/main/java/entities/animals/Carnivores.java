package entities.animals;

import fileio.AnimalInput;
import grid.Grid;
import grid.ScannedGrid;

/**
 * Represents the Carnivore entity.
 * Carnivores hunt other animals and have a specific probability of avoiding
 * attacks.
 */
public final class Carnivores extends Animal {

    private static final double NOT_ATTACKED_PROBABILITY = 30.0;
    private static final double ORGANIC_MATTER_GAIN = 0.5;
    private static final int DIRECTIONS_COUNT = 4;

    /**
     * Default constructor.
     */
    public Carnivores() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name Name of the carnivore.
     * @param mass Mass of the carnivore.
     */
    public Carnivores(final String name,
            final double mass) {
        super(name, mass, NOT_ATTACKED_PROBABILITY);
    }

    /**
     * Constructor based on file input.
     *
     * @param animalInput The input data object.
     */
    public Carnivores(final AnimalInput animalInput) {
        super(animalInput.getName(),
                animalInput.getMass(),
                NOT_ATTACKED_PROBABILITY);
    }

    /**
     * Helper method to process eating logic.
     * Increases mass and organic matter produced.
     *
     * @param pos  The position of the prey.
     * @param grid The grid instance.
     * @return The position where the interaction happened.
     */
    private Pair eatAnimal(final Pair pos, final Grid grid) {
        Animal prey = grid.getAnimalAt(pos.getX(), pos.getY());
        if (prey == null) {
            return pos;
        }
        this.organicMatterProduced = ORGANIC_MATTER_GAIN;
        this.mass += prey.getMass();

        return pos;
    }

    /**
     * Determines the best move for the carnivore.
     * Priorities are sorted by water quality, then it attempts to find a valid move
     * to eat.
     *
     * @param scannedGrid The scanned surroundings.
     * @param grid        The main grid.
     * @return The target position or null if no move is possible.
     */
    @Override
    protected Pair bestMove(final ScannedGrid scannedGrid, final Grid grid) {
        sortByWaterQuality(scannedGrid, neighborPriority.get(0));
        sortByWaterQuality(scannedGrid, neighborPriority.get(2));
        for (int i = 0; i < DIRECTIONS_COUNT; i++) {
            for (Pair pos : neighborPriority.get(i)) {
                int x = pos.getX();
                int y = pos.getY();
                if (!grid.isValidPosition(x, y)) {
                    continue;
                }

                return eatAnimal(pos, grid);
            }
        }
        return null;
    }
}
