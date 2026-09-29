package grid;

import entities.Entity;
import entities.ScannedEntity;
import entities.Water;
import entities.airs.Air;
import entities.animals.Animal;
import entities.plants.Plant;
import entities.soils.Soil;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the scanned version of the grid, stored in memory.
 * Uses a TreeMap to organize entities by layer at each coordinate.
 */
public final class ScannedGrid implements GridEngine {

    private final int width;
    private final int height;
    private final TreeMap<ScannedLayer, ScannedEntity>[][] entities;

    /**
     * Constructor.
     * Initializes the grid with empty TreeMaps.
     *
     * @param width  Grid width.
     * @param height Grid height.
     */
    @SuppressWarnings("unchecked")
    public ScannedGrid(final int width, final int height) {
        this.width = width;
        this.height = height;
        // Generic array creation requires unchecked cast suppression
        this.entities = new TreeMap[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                entities[i][j] = new TreeMap<>();
            }
        }
    }

    @Override
    public boolean isValidPosition(final int x, final int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Adds a scanned entity wrapper to a specific layer.
     *
     * @param x      X coordinate.
     * @param y      Y coordinate.
     * @param layer  The layer enum.
     * @param entity The scanned entity wrapper.
     */
    public void addScannedEntity(final int x,
            final int y,
            final ScannedLayer layer,
            final ScannedEntity entity) {
        if (isValidPosition(x, y)) {
            entities[x][y].put(layer, entity);
        }
    }

    @Override
    public void addSoil(final int x, final int y, final Soil soil) {
        addScannedEntity(x, y, ScannedLayer.SOIL, new ScannedEntity(soil));
    }

    @Override
    public void addPlant(final int x, final int y, final Plant plant) {
        addScannedEntity(x, y, ScannedLayer.PLANT, new ScannedEntity(plant));
    }

    @Override
    public void addWater(final int x, final int y, final Water water) {
        addScannedEntity(x, y, ScannedLayer.WATER, new ScannedEntity(water));
    }

    @Override
    public void addAnimal(final int x, final int y, final Animal animal) {
        addScannedEntity(x, y, ScannedLayer.ANIMAL, new ScannedEntity(animal));
    }

    @Override
    public void addAir(final int x, final int y, final Air air) {
        addScannedEntity(x, y, ScannedLayer.AIR, new ScannedEntity(air));
    }

    @Override
    public void removeSoil(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(ScannedLayer.SOIL);
        }
    }

    @Override
    public void removePlant(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(ScannedLayer.PLANT);
        }
    }

    @Override
    public void removeWater(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(ScannedLayer.WATER);
        }
    }

    @Override
    public void removeAnimal(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(ScannedLayer.ANIMAL);
        }
    }

    @Override
    public void removeAir(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(ScannedLayer.AIR);
        }
    }

    /**
     * Gets a specific scanned entity from a layer.
     *
     * @param x     X coordinate.
     * @param y     Y coordinate.
     * @param layer The layer to retrieve.
     * @return The scanned entity or null.
     */
    public ScannedEntity getEntity(final int x, final int y, final ScannedLayer layer) {
        if (isValidPosition(x, y)) {
            return entities[x][y].get(layer);
        }
        return null;
    }

    @Override
    public Soil getSoilAt(final int x, final int y) {
        ScannedEntity scanned = getEntity(x, y, ScannedLayer.SOIL);
        return scanned != null ? (Soil) scanned.getEntity() : null;
    }

    @Override
    public Plant getPlantAt(final int x, final int y) {
        ScannedEntity scanned = getEntity(x, y, ScannedLayer.PLANT);
        return scanned != null ? (Plant) scanned.getEntity() : null;
    }

    @Override
    public Water getWaterAt(final int x, final int y) {
        ScannedEntity scanned = getEntity(x, y, ScannedLayer.WATER);
        return scanned != null ? (Water) scanned.getEntity() : null;
    }

    @Override
    public Animal getAnimalAt(final int x, final int y) {
        ScannedEntity scanned = getEntity(x, y, ScannedLayer.ANIMAL);
        return scanned != null ? (Animal) scanned.getEntity() : null;
    }

    @Override
    public Air getAirAt(final int x, final int y) {
        ScannedEntity scanned = getEntity(x, y, ScannedLayer.AIR);
        return scanned != null ? (Air) scanned.getEntity() : null;
    }

    @Override
    public Map<ScannedLayer, ScannedEntity> getAllScannedEntities(final int x, final int y) {
        if (isValidPosition(x, y)) {
            return entities[x][y];
        }
        return null;
    }

    @Override
    public Map<EntityLayer, Entity> getAllEntities(final int x, final int y) {
        // ScannedGrid does not store raw EntityLayer maps.
        return null;
    }

    /**
     * Checks if a scanned entity exists on a specific layer.
     *
     * @param x     X coordinate.
     * @param y     Y coordinate.
     * @param layer The scanned layer.
     * @return true if exists.
     */
    public boolean hasEntity(final int x, final int y, final ScannedLayer layer) {
        return isValidPosition(x, y) && entities[x][y].containsKey(layer);
    }

    @Override
    public boolean hasEntity(final int x, final int y, final EntityLayer layer) {
        // ScannedGrid works with ScannedLayer, not EntityLayer directly in this context
        return false;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Initializes the scanned grid by copying visible entities from the main grid.
     *
     * @param grid The source Grid.
     */
    public void initFromGrid(final Grid grid) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (grid.hasEntity(i, j, EntityLayer.SOIL)) {
                    addSoil(i, j, grid.getSoilAt(i, j));
                }
                if (grid.hasEntity(i, j, EntityLayer.AIR)) {
                    addAir(i, j, grid.getAirAt(i, j));
                }
            }
        }
    }
}
