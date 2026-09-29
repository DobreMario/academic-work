package main.exceptions;

/**
 * Custom exception thrown when a validation rule is violated.
 * Used throughout the application to signal invalid input or state.
 */
public final class ValidationException extends RuntimeException {

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message The detail message explaining the validation error.
     */
    public ValidationException(final String message) {
        super(message);
    }
}
