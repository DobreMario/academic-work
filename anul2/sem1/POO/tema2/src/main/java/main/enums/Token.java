package main.enums;

/**
 * Enumeration representing access control tokens (Allowed/Denied).
 */
public enum Token {
    ACCESS_DENIED(-1),
    ACCESS_ALLOWED(1);

    private final int value;

    /**
     * Constructor.
     *
     * @param value The numerical value of the token.
     */
    Token(final int value) {
        this.value = value;
    }

    /**
     * Gets the token value.
     *
     * @return The value.
     */
    public int getValue() {
        return value;
    }
}
