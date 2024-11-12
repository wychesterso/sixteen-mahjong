package game.core;

/**
 * An exception for when attempting a Kong when the conditions aren't met.
 */
public class InvalidKongException extends Exception {
    /**
     * Constructs an invalid Kong exception.
     */
    public InvalidKongException() {
        super();
    }

    /**
     * Constructs an invalid Kong exception with an error message.
     * @param message the error message.
     */
    public InvalidKongException(String message) {
        super(message);
    }
}
