package game.board;

import game.board.hand.Hand;
import game.board.tile.Tile;
import game.board.tile.TileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WinChecker {
    /**
     * Checks if there is a valid self-draw win condition.
     * @param hand the player's current hand.
     * @return true iff the win condition is satisfied, false otherwise.
     */
    public static boolean checkWin(Hand hand) {
        List<Tile> tiles = new ArrayList<>(hand.getTiles());
        Collections.sort(tiles);

        return checkSixteenDisjoint(tiles) || checkThirteenOrphans(tiles)
                || checkLikKuLikKu(tiles) != null || canFormGroups(tiles);
    }

    /**
     * Checks if there is a valid win condition when a tile is taken from an opponent discard.
     * @param hand the player's current hand.
     * @param tile the tile most recently discarded.
     * @return true iff the win condition is satisfied, false otherwise.
     */
    public static boolean checkWin(Hand hand, Tile tile) {
        List<Tile> tiles = new ArrayList<>(hand.getTiles());
        tiles.add(tile);
        Collections.sort(tiles);

        return checkSixteenDisjoint(tiles) || checkThirteenOrphans(tiles)
                || checkLikKuLikKu(tiles) != null || canFormGroups(tiles);
    }

    /**
     * Checks for the special win condition of sixteen disjoint tiles.
     * @param tiles the list of tiles available.
     * @return true iff a win condition is found, false otherwise.
     */
    public static boolean checkSixteenDisjoint(List<Tile> tiles) {
        int n = tiles.size();
        if (n != 17) {
            return false;
        }

        // this determines if a single pair of two identical tiles is found
        boolean pairFlag = false;

        // check every combination of two tiles
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Tile tile1 = tiles.get(i);
                Tile tile2 = tiles.get(j);
                // check for two related tiles of same type
                TileType tileType = tile1.getTileType();
                if (tileType == tile2.getTileType() && tileType != TileType.WORD_WIND
                        && tileType != TileType.WORD_DRAGON) {
                    int ord1 = tile1.ordinal();
                    int ord2 = tile2.ordinal();

                    if ((ord2 == ord1 + 1) || (ord2 == ord1 + 2)) {
                        return false;
                    }
                }

                // check for two identical tiles
                if (tile1 == tile2) {
                    // checks if there is more than one pair
                    if (pairFlag) {
                        return false;
                    }
                    // pair is found so set flag to true
                    pairFlag = true;
                }
            }
        }

        // must have exactly one pair to win (although at this point it should be satisfied anyway)
        return pairFlag;
    }

    /**
     * Checks for the special win condition of thirteen orphan tiles.
     * @param tiles the list of tiles available.
     * @return true iff a win condition is found, false otherwise.
     */
    public static boolean checkThirteenOrphans(List<Tile> tiles) {
        List<Tile> remainingTiles = new ArrayList<>(tiles);

        int n = remainingTiles.size();
        if (n != 17) {
            return false;
        }

        // the tiles that form a Thirteen Orphans hand
        List<Tile> thirteenOrphans = getThirteenOrphans();

        for (Tile tile : thirteenOrphans) {
            boolean containsTile = remainingTiles.remove(tile);
            // checks if any of the required tiles is not in hand
            if (!containsTile) {
                return false;
            }
        }

        List<List<Tile>> groups = new ArrayList<>();

        // check every combination of three tiles
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                for (int k = j + 1; k < 4; k++) {
                    Tile tile1 = remainingTiles.get(i);
                    Tile tile2 = remainingTiles.get(j);
                    Tile tile3 = remainingTiles.get(k);

                    // check for three identical tiles
                    if (tile1 == tile2 && tile2 == tile3) {
                        groups.add(new ArrayList<>(List.of(tile1, tile2, tile3)));
                    }

                    // check for three consecutive tiles of same type
                    if (tile1.getTileType() == tile2.getTileType()
                            && tile2.getTileType() == tile3.getTileType()) {
                        int ord1 = tile1.ordinal();
                        int ord2 = tile2.ordinal();
                        int ord3 = tile3.ordinal();

                        if ((ord2 == ord1 + 1 && ord3 == ord2 + 1)
                                || (ord3 == ord1 + 1 && ord2 == ord3 + 1)
                                || (ord1 == ord2 + 1 && ord3 == ord1 + 1)) {
                            groups.add(new ArrayList<>(List.of(tile1, tile2, tile3)));
                        }
                    }
                }
            }
        }

        // checks if there are valid groups
        if (groups.isEmpty()) {
            return false;
        }

        // for every group, check if the tile not used is one of the required tiles
        for (List<Tile> group : groups) {
            List<Tile> leftoverTiles = new ArrayList<>(remainingTiles);
            for (Tile tile : group) {
                leftoverTiles.remove(tile);
            }
            if (thirteenOrphans.contains(leftoverTiles.getFirst())) {
                return true;
            }
        }
        return false;
    }

    public static List<Tile> getThirteenOrphans() {
        return List.of(Tile.MAAN_1, Tile.MAAN_9, Tile.SOK_1, Tile.SOK_9,
                Tile.TUNG_1, Tile.TUNG_9, Tile.WIND_EAST, Tile.WIND_SOUTH, Tile.WIND_WEST,
                Tile.WIND_NORTH, Tile.WORD_ZHONG, Tile.WORD_FAT, Tile.WORD_BAT);
    }

    /**
     * Checks for the special win condition of Lik Ku Lik Ku.
     * @param tiles the list of tiles available.
     * @return true iff a win condition is found, false otherwise.
     */
    public static Tile checkLikKuLikKu(List<Tile> tiles) {
        int n = tiles.size();
        if (n != 17) {
            return null;
        }

        Tile tile = checkPairs(tiles);
        if (tile == null) {
            return null;
        }
        List<Tile> remainingTiles = new ArrayList<>(tiles);
        remainingTiles.remove(tile);
        remainingTiles.remove(tile);
        if (remainingTiles.remove(tile)) {
            return tile;
        }
        return null;
    }

    /**
     * Checks if every tile other than one of them can form a pair of two identical tiles.
     * @param tiles the tiles to be checked.
     * @return the lone tile iff all other tiles can be paired, else null.
     */
    public static Tile checkPairs(List<Tile> tiles) {
        // base case: only one tile
        if (tiles.size() == 1) {
            return tiles.getFirst();
        }

        int n = tiles.size();

        // check every combination of two tiles
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Tile tile1 = tiles.get(i);
                Tile tile2 = tiles.get(j);

                // check for two identical tiles
                if (tile1 == tile2) {
                    List<Tile> remainingTiles = new ArrayList<>(tiles);
                    remainingTiles.remove(tile1);
                    remainingTiles.remove(tile2);

                    // recursively check if the remaining tiles can form pairs
                    return checkPairs(remainingTiles);
                }
            }
        }

        // return null if no valid pair is found
        return null;
    }

    /**
     * Determines whether groupings can be formed with the given set of tiles such that a win
     * condition is achieved.
     * @param tiles the list of tiles available.
     * @return true iff a win condition is found, false otherwise.
     */
    public static boolean canFormGroups(List<Tile> tiles) {
        // base case: only two identical tiles
        if (tiles.size() == 2 && tiles.get(0) == tiles.get(1)) {
            return true;
        }

        int n = tiles.size();

        // check every combination of three tiles
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    Tile tile1 = tiles.get(i);
                    Tile tile2 = tiles.get(j);
                    Tile tile3 = tiles.get(k);

                    // check for three identical tiles
                    if (tile1 == tile2 && tile2 == tile3) {
                        List<Tile> remainingTiles = new ArrayList<>(tiles);
                        remainingTiles.remove(tile1);
                        remainingTiles.remove(tile2);
                        remainingTiles.remove(tile3);

                        // recursively check if the remaining tiles can form groups
                        if (canFormGroups(remainingTiles)) {
                            return true;
                        }
                    }

                    // check for three consecutive tiles of same type
                    if (tile1.getTileType() == tile2.getTileType()
                            && tile2.getTileType() == tile3.getTileType()) {
                        int ord1 = tile1.ordinal();
                        int ord2 = tile2.ordinal();
                        int ord3 = tile3.ordinal();

                        if ((ord2 == ord1 + 1 && ord3 == ord2 + 1)
                                || (ord3 == ord1 + 1 && ord2 == ord3 + 1)
                                || (ord1 == ord2 + 1 && ord3 == ord1 + 1)) {
                            List<Tile> remainingTiles = new ArrayList<>(tiles);
                            remainingTiles.remove(tile1);
                            remainingTiles.remove(tile2);
                            remainingTiles.remove(tile3);

                            // recursively check if the remaining tiles can form groups
                            if (canFormGroups(remainingTiles)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        // return false if no valid grouping is found
        return false;
    }
}
