package grid;

import entities.Entity;
import entities.ScannedEntity;
import entities.Water;
import entities.airs.DesertAir;
import entities.airs.MountainAir;
import entities.airs.PolarAir;
import entities.airs.TemperateAir;
import entities.airs.TropicalAir;
import entities.animals.Animal;
import entities.plants.Plant;
import grid.GridEngine.ScannedLayer;

/**
 * Utility class handling interactions between entities on the grid.
 * Manages eating, moving, generic interactions, and weather updates.
 */
public final class InteractionEngine {

    // Interaction timing matrices
    private static final int[] AIR_TIMES = { -1, 1, 1, 1, 1 };
    private static final int[] SOIL_TIMES = { 1, -1, 1, 1, 1 };
    private static final int[] WATER_TIMES = { 2, 2, -1, 1, 1 };
    private static final int[] PLANT_TIMES = { 1, 1, 1, -1, 1 };
    private static final int[] ANIMAL_TIMES = { 1, 1, 1, 1, -1 };

    /**
     * Matrix defining interaction intervals between entity types based on their
     * memory keys.
     */
    public static final int[][] INTERACTION_TIMES = {
            AIR_TIMES,
            SOIL_TIMES,
            WATER_TIMES,
            PLANT_TIMES,
            ANIMAL_TIMES
    };

    private InteractionEngine() {
        // Private constructor to prevent instantiation
    }

    /**
     * Triggers the eating behavior for all animals on the grid.
     *
     * @param scannedGrid The scanned grid containing entity data.
     */
    public static void animalsEat(final ScannedGrid scannedGrid) {
        if (scannedGrid == null) {
            return;
        }
        for (int i = 0; i < scannedGrid.getWidth(); i++) {
            for (int j = 0; j < scannedGrid.getHeight(); j++) {
                Animal animal = scannedGrid.getAnimalAt(i, j);
                if (animal != null) {
                    animal.eat(scannedGrid, i, j);
                }
            }
        }
    }

    /**
     * Main loop for processing one simulation step.
     * Handles interactions, eating, movement, grid cleanup, and weather.
     *
     * @param scannedGrid    The scanned grid.
     * @param grid          The main grid.
     * @param currTimestamp The current simulation timestamp.
     */
    public static void processGridInteractions(final ScannedGrid scannedGrid,
            final Grid grid,
            final int currTimestamp) {
        if (grid == null) {
            return;
        }

        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                processCellInteractions(i, j, scannedGrid, currTimestamp);
            }
        }

        animalsEat(scannedGrid);

        processAnimalMovement(scannedGrid, grid, currTimestamp);

        updateGrid(grid);
        updateGrid(scannedGrid);

        processWeatherChange(grid);
    }

    private static void processAnimalMovement(final ScannedGrid scannedGrid,
            final Grid grid,
            final int currTimestamp) {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                ScannedEntity scanned = scannedGrid.getEntity(i, j, ScannedLayer.ANIMAL);
                if (scanned == null) {
                    continue;
                }
                if (scanned.isMoving(currTimestamp)) {
                    Animal animal = (Animal) scanned.getEntity();
                    animal.animalMove(grid, scannedGrid, i, j);
                }
            }
        }
    }

    private static void processCellInteractions(final int x,
            final int y,
            final ScannedGrid grid,
            final int currTimestamp) {
        for (ScannedEntity scanned : grid.getAllScannedEntities(x, y).values()) {
            for (ScannedEntity otherScanned : grid.getAllScannedEntities(x, y).values()) {
                if (shouldInteract(scanned, otherScanned, currTimestamp)) {
                    scanned.getEntity().interactWith(otherScanned.getEntity());
                }
            }
        }
    }

    private static boolean shouldInteract(final ScannedEntity s1,
            final ScannedEntity s2,
            final int timestamp) {
        if (s1 == null || s2 == null || s1 == s2) {
            return false;
        }

        Entity e1 = s1.getEntity();
        Entity e2 = s2.getEntity();

        if (e1 == null || e2 == null || e1 == e2) {
            return false;
        }

        int k1 = e1.getMemoryKey();
        int k2 = e2.getMemoryKey();
        int interval = INTERACTION_TIMES[k1][k2];

        // Only interact if interval is valid (>0) and timing matches
        return interval > 0 && s1.isInteracting(timestamp, interval);
    }

    private static void updateGrid(final Grid grid) {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                Plant plant = grid.getPlantAt(i, j);
                if (plant != null && plant.isDead()) {
                    grid.removePlant(i, j);
                }
                Water w = grid.getWaterAt(i, j);
                if (w != null && w.getMass() <= 0.0) {
                    grid.removeWater(i, j);
                }
            }
        }
    }

    private static void updateGrid(final ScannedGrid grid) {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                Plant p = grid.getPlantAt(i, j);
                if (p != null && p.isDead()) {
                    grid.removePlant(i, j);
                }
                Water w = grid.getWaterAt(i, j);
                if (w != null && w.getMass() <= 0.0) {
                    grid.removeWater(i, j);
                }
            }
        }
    }

    private static void processWeatherChange(final Grid grid) {
        if (grid != null) {
            DesertAir.applyDesertStorm();
            MountainAir.applyHiking();
            PolarAir.applyPolarStorm();
            TropicalAir.applyRainfall();
            TemperateAir.applyNewSeason();
        }
    }
}
