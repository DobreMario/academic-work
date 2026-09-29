package grid;

import entities.Entity;
import entities.ScannedEntity;
import entities.Water;
import entities.airs.Air;
import entities.animals.Animal;
import entities.plants.Plant;
import entities.soils.Soil;
import factory.AirFactory;
import factory.AnimalFactory;
import factory.PlantFactory;
import factory.SoilFactory;
import factory.WaterFactory;
import fileio.TerritorySectionParamsInput;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the main simulation grid.
 * Stores the actual state of all entities at every position.
 */
public final class Grid implements GridEngine {

    private final int width;
    private final int height;
    private final TreeMap<EntityLayer, Entity>[][] entities;

    /**
     * Constructor.
     * Initializes the grid structure.
     *
     * @param width  Grid width.
     * @param height Grid height.
     */
    @SuppressWarnings("unchecked")
    public Grid(final int width, final int height) {
        this.width = width;
        this.height = height;
        // Generic array creation requires unchecked suppression
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

    @Override
    public void addSoil(final int x, final int y, final Soil soil) {
        if (isValidPosition(x, y)) {
            entities[x][y].put(EntityLayer.SOIL, soil);
        }
    }

    @Override
    public void addPlant(final int x, final int y, final Plant plant) {
        if (isValidPosition(x, y)) {
            entities[x][y].put(EntityLayer.PLANT, plant);
        }
    }

    @Override
    public void addWater(final int x, final int y, final Water water) {
        if (isValidPosition(x, y)) {
            entities[x][y].put(EntityLayer.WATER, water);
        }
    }

    @Override
    public void addAnimal(final int x, final int y, final Animal animal) {
        if (isValidPosition(x, y)) {
            entities[x][y].put(EntityLayer.ANIMAL, animal);
        }
    }

    @Override
    public void addAir(final int x, final int y, final Air air) {
        if (isValidPosition(x, y)) {
            entities[x][y].put(EntityLayer.AIR, air);
        }
    }

    @Override
    public void removeSoil(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(EntityLayer.SOIL);
        }
    }

    @Override
    public void removePlant(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(EntityLayer.PLANT);
        }
    }

    @Override
    public void removeWater(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(EntityLayer.WATER);
        }
    }

    @Override
    public void removeAnimal(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(EntityLayer.ANIMAL);
        }
    }

    @Override
    public void removeAir(final int x, final int y) {
        if (isValidPosition(x, y)) {
            entities[x][y].remove(EntityLayer.AIR);
        }
    }

    @Override
    public Soil getSoilAt(final int x, final int y) {
        if (isValidPosition(x, y)) {
            return (Soil) entities[x][y].get(EntityLayer.SOIL);
        }
        return null;
    }

    @Override
    public Plant getPlantAt(final int x, final int y) {
        if (isValidPosition(x, y)) {
            return (Plant) entities[x][y].get(EntityLayer.PLANT);
        }
        return null;
    }

    @Override
    public Water getWaterAt(final int x, final int y) {
        if (isValidPosition(x, y)) {
            return (Water) entities[x][y].get(EntityLayer.WATER);
        }
        return null;
    }

    @Override
    public Animal getAnimalAt(final int x, final int y) {
        if (isValidPosition(x, y)) {
            return (Animal) entities[x][y].get(EntityLayer.ANIMAL);
        }
        return null;
    }

    @Override
    public Air getAirAt(final int x, final int y) {
        if (isValidPosition(x, y)) {
            return (Air) entities[x][y].get(EntityLayer.AIR);
        }
        return null;
    }

    @Override
    public Map<EntityLayer, Entity> getAllEntities(final int x, final int y) {
        if (isValidPosition(x, y)) {
            return entities[x][y];
        }
        return null;
    }

    /**
     * Gets a list of all entities at a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return List of entities, or null if invalid position.
     */
    public List<Entity> getAllEntitiesAt(final int x, final int y) {
        if (!isValidPosition(x, y)) {
            return null;
        }
        return List.copyOf(entities[x][y].values());
    }

    @Override
    public Map<ScannedLayer, ScannedEntity> getAllScannedEntities(final int x, final int y) {
        return null;
    }

    @Override
    public boolean hasEntity(final int x, final int y, final EntityLayer layer) {
        return isValidPosition(x, y) && entities[x][y].containsKey(layer);
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
     * Initializes the grid entities from the input parameters.
     * Uses factories to create entity instances.
     *
     * @param params The input parameters containing entity definitions.
     */
    public void init(final TerritorySectionParamsInput params) {
        for (var soil : params.getSoil()) {
            for (var pos : soil.getSections()) {
                Soil soilEntity = SoilFactory.createSoil(soil);
                addSoil(pos.getX(), pos.getY(), soilEntity);
            }
        }

        for (var plant : params.getPlants()) {
            for (var pos : plant.getSections()) {
                Plant plantEntity = PlantFactory.createPlant(plant);
                addPlant(pos.getX(), pos.getY(), plantEntity);
            }
        }

        for (var animal : params.getAnimals()) {
            for (var pos : animal.getSections()) {
                Animal animalEntity = AnimalFactory.createAnimal(animal);
                addAnimal(pos.getX(), pos.getY(), animalEntity);
            }
        }

        for (var water : params.getWater()) {
            for (var pos : water.getSections()) {
                Water waterEntity = WaterFactory.createWater(water);
                addWater(pos.getX(), pos.getY(), waterEntity);
            }
        }

        for (var air : params.getAir()) {
            for (var pos : air.getSections()) {
                Air airEntity = AirFactory.createAir(air);
                addAir(pos.getX(), pos.getY(), airEntity);
            }
        }
    }

    @Override
    public boolean hasEntity(final int x, final int y, final ScannedLayer layer) {
        return false;
    }
}
