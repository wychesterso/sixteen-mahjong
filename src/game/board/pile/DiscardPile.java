package game.board.pile;

import game.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * A pile containing tiles that have been discarded throughout the game.
 */
public class DiscardPile {
    private final List<Tile> pile = new ArrayList<>();

    /**
     * Creates a new discard pile.
     */
    public DiscardPile() {}

    /**
     * Retrieves a list of all tiles discarded throughout the round.
     * @return the list of discarded tiles.
     */
    public List<Tile> getDiscardedTiles() {
        return new ArrayList<>(pile);
    }

    /**
     * Adds a newly-discarded tile to the pile.
     * @param tile the tile to add.
     */
    public void addNewDiscard(Tile tile) {
        pile.add(tile);
    }

    /**
     * Gets the last discarded tile.
     * @return the last discarded tile.
     */
    public Tile getLastDiscard() {
        return pile.getLast();
    }

    /**
     * Removes and returns the last discarded tile from the pile.
     * @return the last discarded tile.
     */
    public Tile takeLastDiscard() {
        return pile.removeLast();
    }

    /**
     * A string representation of the discard pile for display purposes.
     * @return the string representation.
     */
    public String toString() {
        int count = 0;
        StringBuilder output = new StringBuilder();
        for (Tile tile : pile) {
            output.append(tile);
            count++;
            if (count % 30 == 0) {
                output.append("\n");
            }
        }
        if (output.isEmpty()) {
            return "Empty";
        }
        return output.toString();
    }
}
