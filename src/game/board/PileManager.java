package game.board;

import game.board.pile.DiscardPile;
import game.board.pile.UnrevealedPile;
import game.board.tile.Tile;
import game.core.EmptyPileException;

/**
 * A managing class for the game's piles.
 */
public class PileManager {
    private final UnrevealedPile unrevealedPile;
    private final DiscardPile discardPile = new DiscardPile();

    /**
     * Creates a new pile manager instance.
     */
    public PileManager(int minTilesLeft) {
        unrevealedPile = new UnrevealedPile(minTilesLeft);
    }

    /**
     * Retrieves the game's discard pile.
     * @return the discard pile.
     */
    public DiscardPile getDiscardPile() {
        return discardPile;
    }

    /**
     * Retrieves the game's unrevealed pile.
     * @return the unrevealed pile.
     */
    public UnrevealedPile getUnrevealedPile() {
        return unrevealedPile;
    }

    /**
     * Draws a tile from the unrevealed pile.
     * @return the tile that is drawn.
     * @throws EmptyPileException if the pile is empty.
     */
    public Tile drawTile() throws EmptyPileException {
        return unrevealedPile.drawTile();
    }

    /**
     * Draws a bonus tile as a result of a Flower or a Kong.
     * @return the tile that is drawn.
     * @throws EmptyPileException if the pile is empty.
     */
    public Tile drawBonusTile() throws EmptyPileException {
        return unrevealedPile.drawBonusTile();
    }

    /**
     * Adds a discarded tile to the discard pile.
     * @param tile the tile that was discarded.
     */
    public void addDiscardedTile(Tile tile) {
        discardPile.addNewDiscard(tile);
    }

    /**
     * Retrieves the last discarded tile in the game.
     * @return the last discarded tile.
     */
    public Tile getLastDiscardedTile() {
        return discardPile.getLastDiscard();
    }

    /**
     * Removes and returns the last discarded tile in the game from the discard pile.
     * @return the last discarded tile.
     */
    public Tile takeLastDiscardedTile() {
        return discardPile.takeLastDiscard();
    }
}
