package entities.animals;

public class Pair {
    private int x;
    private int y;

    public Pair(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for X coordinate.
     *
     * @return X coordinate value.
     */

    public int getX() {
        return x;
    }

    /**
     * Getter for Y coordinate.
     *
     * @return Y coordinate value.
     */

    public int getY() {
        return y;
    }
}
