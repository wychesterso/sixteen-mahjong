package game.core;

/**
 * An exception for when attempting to draw a tile from an empty pile.
 */
public class EmptyPileException extends Exception {
    /**
     * Constructs an empty pile exception.
     */
    public EmptyPileException() {
        super();
    }
}
