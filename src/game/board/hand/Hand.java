package game.board.hand;

import game.board.tile.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The player's hand, containing tiles that have not been revealed or used in groups.
 */
public class Hand {
    private final List<Tile> hand = new ArrayList<>();
    private Tile lastDrawnTile = null;

    /**
     * Creates a hand.
     */
    public Hand() {}

    /**
     * Retrieves all tiles in the hand.
     * @return a list of tiles.
     */
    public List<Tile> getTiles() {
        return hand;
    }

    /**
     * Resets the hand for a new game.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Determines if the hand contains at least one of the specified tile.
     * @param tile the tile to be checked.
     * @return true iff the hand contains at least one of the specified tile, false otherwise.
     */
    public boolean containsTile(Tile tile) {
        return hand.contains(tile);
    }

    /**
     * Determines if the hand contains the required amount of the specified tile.
     * @param tile the tile to be checked.
     * @return true iff the hand contains the required amount of the specified tile, false
     * otherwise.
     */
    public boolean containsTile(Tile tile, int num) {
        int count = 0;
        for (Tile t : hand) {
            if (t.equals(tile)) {
                count++;
            }
            if (count >= num) {
                return true;
            }
        }
        return count >= num;
    }

    /**
     * Adds a tile to the hand.
     * @param tile the tile to be added.
     */
    public void addToHand(Tile tile) {
        hand.add(tile);
        lastDrawnTile = tile;
        Collections.sort(hand);
    }

    /**
     * Removes a tile from the hand.
     * @param tile the tile to be removed.
     */
    public void discardTile(Tile tile) {
        Boolean success = hand.remove(tile);
        if (!success) {
            throw new RuntimeException("Cannot discard the tile: " + tile + ".");
        }
    }

    /**
     * Retrieves the tile most recently obtained by the player.
     * @return the tile most recently obtained.
     */
    public Tile getLastDrawnTile() {
        return lastDrawnTile;
    }

    /**
     * Retrieves the string display of the hand.
     * @return the string representation.
     */
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Tile tile : hand) {
            output.append(tile);
        }
        return output.toString();
    }

    /**
     * Returns the string display of the hand, with the last drawn tile separated to the right.
     * @return the string representation of the hand.
     */
    public String toStringSepLastDrawn() {
        List<Tile> originalTiles = new ArrayList<>(hand);
        originalTiles.remove(lastDrawnTile);
        StringBuilder output = new StringBuilder();
        for (Tile tile : originalTiles) {
            output.append(tile);
        }
        output.append(" " + lastDrawnTile);
        return output.toString();
    }

    /**
     * Returns the string display of the hand viewed from the opponent's perspective,
     * i.e. all tiles are obscured.
     * @return the string representation of the hand.
     */
    public String toStringOpponentView() {
        StringBuilder output = new StringBuilder();
        for (Tile tile : hand) {
            output.append("🀫");
        }
        return output.toString();
    }
}
