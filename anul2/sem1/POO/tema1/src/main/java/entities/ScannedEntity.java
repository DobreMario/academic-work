package entities;

/**
 * Wrapper class representing an entity that has been scanned.
 * Stores the entity reference and the timestamp of the scan.
 */
public final class ScannedEntity {
    private Entity entity;
    private int scannedTimestamp;
    private int movedTimestamp;

    /**
     * Constructor initializing with an entity and default timestamp 0.
     *
     * @param entity The entity being scanned.
     */
    public ScannedEntity(final Entity entity) {
        this.entity = entity;
        this.scannedTimestamp = 0;
        this.movedTimestamp = 0;
    }

    /**
     * Parameterized constructor.
     *
     * @param entity           The entity being scanned.
     * @param scannedTimestamp The time when it was scanned.
     */
    public ScannedEntity(final Entity entity, final int scannedTimestamp) {
        this.entity = entity;
        this.scannedTimestamp = scannedTimestamp;
        this.movedTimestamp = scannedTimestamp;
    }

    /**
     * Checks if the entity is interacting based on time cycles.
     *
     * @param currTimestamp   The current simulation timestamp.
     * @param interactingTime The interaction interval.
     * @return true if interacting, false otherwise.
     */
    public boolean isInteracting(final int currTimestamp, final int interactingTime) {
        return ((currTimestamp - scannedTimestamp) % interactingTime == 0);
    }

    /**
     * Checks if the entity is moving based on timestamp tracking.
     * Updates the moved timestamp if movement is detected (interval of 2).
     *
     * @param currTimestamp The current timestamp.
     * @return true if moving, false otherwise.
     */
    public boolean isMoving(final int currTimestamp) {
        if (currTimestamp - this.movedTimestamp == 2) {
            this.movedTimestamp = currTimestamp;
            return true;
        }
        return false;
    }

    /**
     * Gets the wrapped entity.
     *
     * @return The entity object.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Sets the wrapped entity.
     *
     * @param entity The new entity object.
     */
    public void setEntity(final Entity entity) {
        this.entity = entity;
    }

    /**
     * Gets the scanned timestamp.
     *
     * @return The timestamp.
     */
    public int getScannedTimestamp() {
        return scannedTimestamp;
    }

    /**
     * Sets the scanned timestamp.
     *
     * @param timestampValue The new timestamp value.
     */
    public void setScannedTimestamp(final int timestampValue) {
        this.scannedTimestamp = timestampValue;
    }
}
