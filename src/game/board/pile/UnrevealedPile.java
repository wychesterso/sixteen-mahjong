package game.board.pile;

import game.board.tile.Tile;
import game.board.tile.TileType;
import game.core.EmptyPileException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A pile containing tiles that have yet to be drawn.
 */
public class UnrevealedPile {
    private final List<Tile> pile = new ArrayList<>();
    private final int minTilesLeft;

    /**
     * Creates a new unrevealed pile, which includes all the starting tiles with shuffled order.
     * @param minTilesLeft the minimum number of remaining tiles that results in a draw.
     */
    public UnrevealedPile(int minTilesLeft) {
        this.minTilesLeft = minTilesLeft;
        for (Tile tile : Tile.values()) {
            if (tile.getTileType() == TileType.FLOWER_SEASON
                    || tile.getTileType() == TileType.FLOWER_PLANT) {
                pile.add(tile);
            } else {
                for (int i = 0; i < 4; i++) {
                    pile.add(tile);
                }
            }
        }
        Collections.shuffle(pile);
    }

    /**
     * Determines if the minimum number of tiles left is reached.
     * @return true iff the min number is reached, false otherwise.
     */
    public boolean noMoreDraws() {
        return pile.size() <= minTilesLeft;
    }

    /**
     * Draws the first tile from the pile.
     * @return the drawn tile.
     * @throws EmptyPileException if the minimum number of tiles left is reached.
     */
    public Tile drawTile() throws EmptyPileException {
        if (noMoreDraws()) {
            throw new EmptyPileException();
        }
        return pile.removeFirst();
    }

    /**
     * Draws the last tile from the pile, used for bonus draws.
     * @return the drawn tile.
     * @throws EmptyPileException if the minimum number of tiles left is reached.
     */
    public Tile drawBonusTile() throws EmptyPileException {
        if (noMoreDraws()) {
            throw new EmptyPileException();
        }
        return pile.removeLast();
    }

    /**
     * Determines the number of tiles left in the pile.
     * @return the count of tiles.
     */
    public int getRemainingTileCount() {
        return pile.size();
    }
}
