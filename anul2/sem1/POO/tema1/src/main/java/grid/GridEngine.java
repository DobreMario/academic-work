package grid;

import entities.Entity;
import entities.ScannedEntity;
import entities.Water;
import entities.airs.Air;
import entities.animals.Animal;
import entities.plants.Plant;
import entities.soils.Soil;

import java.util.Map;

/**
 * Interface defining the contract for grid operations.
 * Handles entity management layers and coordinate validation.
 */
public interface GridEngine {

    /**
     * Enum representing layers of actual entities in the simulation.
     */
    enum EntityLayer {
        SOIL,
        PLANT,
        ANIMAL,
        WATER,
        AIR
    }

    /**
     * Enum representing layers of scanned entities (wrappers) in memory.
     */
    enum ScannedLayer {
        AIR,
        SOIL,
        WATER,
        PLANT,
        ANIMAL
    }

    /**
     * Checks if a position is valid within the grid boundaries.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return true if valid, false otherwise.
     */
    boolean isValidPosition(int x, int y);

    /**
     * Adds a soil entity to the grid.
     *
     * @param x    X coordinate.
     * @param y    Y coordinate.
     * @param soil The soil entity.
     */
    void addSoil(int x, int y, Soil soil);

    /**
     * Adds a plant entity to the grid.
     *
     * @param x     X coordinate.
     * @param y     Y coordinate.
     * @param plant The plant entity.
     */
    void addPlant(int x, int y, Plant plant);

    /**
     * Adds an animal entity to the grid.
     *
     * @param x      X coordinate.
     * @param y      Y coordinate.
     * @param animal The animal entity.
     */
    void addAnimal(int x, int y, Animal animal);

    /**
     * Adds a water entity to the grid.
     *
     * @param x     X coordinate.
     * @param y     Y coordinate.
     * @param water The water entity.
     */
    void addWater(int x, int y, Water water);

    /**
     * Adds an air entity to the grid.
     *
     * @param x   X coordinate.
     * @param y   Y coordinate.
     * @param air The air entity.
     */
    void addAir(int x, int y, Air air);

    /**
     * Removes the soil from a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    void removeSoil(int x, int y);

    /**
     * Removes the plant from a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    void removePlant(int x, int y);

    /**
     * Removes the animal from a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    void removeAnimal(int x, int y);

    /**
     * Removes the water from a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    void removeWater(int x, int y);

    /**
     * Removes the air from a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    void removeAir(int x, int y);

    /**
     * Gets the soil at a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return The soil entity or null.
     */
    Soil getSoilAt(int x, int y);

    /**
     * Gets the plant at a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return The plant entity or null.
     */
    Plant getPlantAt(int x, int y);

    /**
     * Gets the animal at a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return The animal entity or null.
     */
    Animal getAnimalAt(int x, int y);

    /**
     * Gets the water at a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return The water entity or null.
     */
    Water getWaterAt(int x, int y);

    /**
     * Gets the air at a specific position.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return The air entity or null.
     */
    Air getAirAt(int x, int y);

    /**
     * Gets all actual entities at a position mapped by layer.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Map of EntityLayer to Entity.
     */
    Map<EntityLayer, Entity> getAllEntities(int x, int y);

    /**
     * Gets all scanned entities at a position mapped by layer.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Map of ScannedLayer to ScannedEntity.
     */
    Map<ScannedLayer, ScannedEntity> getAllScannedEntities(int x, int y);

    /**
     * Checks if an actual entity exists on a specific layer.
     *
     * @param x     X coordinate.
     * @param y     Y coordinate.
     * @param layer The entity layer.
     * @return true if exists.
     */
    boolean hasEntity(int x, int y, EntityLayer layer);

    /**
     * Checks if a scanned entity exists on a specific layer.
     *
     * @param x     X coordinate.
     * @param y     Y coordinate.
     * @param layer The scanned layer.
     * @return true if exists.
     */
    boolean hasEntity(int x, int y, ScannedLayer layer);

    /**
     * Gets the grid width.
     *
     * @return width.
     */
    int getWidth();

    /**
     * Gets the grid height.
     *
     * @return height.
     */
    int getHeight();
}
