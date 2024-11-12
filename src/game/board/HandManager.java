package game.board;

import game.board.hand.Hand;
import game.board.hand.RevealedHand;
import game.board.tile.Tile;
import game.board.tile.TileType;
import game.core.InvalidKongException;

import java.util.*;

/**
 * A managing class for the player's revealed and unrevealed hands. Deals with adding and
 * discarding tiles, and grouping them into the revealed hand.
 */
public class HandManager {
    private final Hand hand = new Hand();
    private final RevealedHand revealedHand = new RevealedHand();

    /**
     * Creates a hand manager instance.
     */
    public HandManager() {}

    /**
     * Retrieves the player's unrevealed hand.
     * @return the player's hand.
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Resets the player's hands for a new game.
     */
    public void clearHand() {
        hand.clearHand();
        revealedHand.clearHand();
    }

    /**
     * Retrieves the player's revealed hand.
     * @return the player's revealed hand.
     */
    public RevealedHand getRevealedHand() {
        return revealedHand;
    }

    /**
     * Adds a tile to the player's hand.
     * @param tile the tile to be added.
     * @requires the tile is valid, i.e. it is not a flower.
     */
    public void addToHand(Tile tile) {
        hand.addToHand(tile);
    }

    /**
     * Discards a tile from the player's hand.
     * @param tile the tile to be discarded.
     */
    public void discardTile(Tile tile) {
        hand.discardTile(tile);
    }

    /**
     * Adds a revealed group to the revealed hand.
     * @param takenTile the tile taken from the opponent.
     * @param existingTiles the tiles in the hand used to form the group.
     * @requires every tile in existingTiles is in the hand.
     * @requires the group is valid (i.e. it is a valid Pong or a Sheung).
     */
    public void addGroup(Tile takenTile, List<Tile> existingTiles) {
        for (Tile tile : existingTiles) {
            getHand().discardTile(tile);
        }
        existingTiles.add(takenTile);
        revealedHand.addGroup(existingTiles);
    }

    /**
     * Performs a Bright Kong with the specified tile, which was drawn by the player.
     * @param tile the tile to perform a Bright Kong on.
     * @throws InvalidKongException if a Bright Kong cannot be performed on the tile.
     */
    public void brightKong(Tile tile) throws InvalidKongException {
        if (!revealedHand.containsPong(tile)) {
            throw new InvalidKongException("Cannot perform Bright Kong with tile: " + tile
                    + ". No Pong of the tile exists in the revealed hand!");
        }
        revealedHand.addBrightKong(tile);
    }

    /**
     * Performs a Bright Kong with the specified tile, which was taken from an opponent discard.
     * @param tile the tile to perform a Bright Kong on.
     * @throws InvalidKongException if a Bright Kong cannot be performed on the tile.
     */
    public void brightKongFromOpponent(Tile tile) throws InvalidKongException {
        if (!hand.containsTile(tile, 3)) {
            throw new InvalidKongException("Cannot perform Bright Kong with tile: " + tile
                    + ". Insufficient tiles in hand to perform bright kong!");
        }
        for (int i = 0; i < 3; i++) {
            hand.discardTile(tile);
        }
        revealedHand.addBrightKongFromOpponent(tile);
    }

    /**
     * Performs a Dark Kong with the specified tile.
     * @param tile the tile to perform a Dark Kong on.
     * @throws InvalidKongException if the Dark Kong cannot be performed on the tile.
     */
    public void darkKong(Tile tile) throws InvalidKongException {
        if (!hand.containsTile(tile, 4)) {
            throw new InvalidKongException("Not enough tiles to perform Dark Kong!");
        }
        List<Tile> group = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            hand.discardTile(tile);
            group.add(tile);
        }
        revealedHand.addDarkKong(group);
    }

    /**
     * Adds a flower to the revealed hand.
     * @param tile the flower tile to be added.
     * @requires the tile is a flower type.
     */
    public void addFlower(Tile tile) {
        revealedHand.addFlower(tile);
    }

    /**
     * Checks if a Dark Kong can be performed with the specified tile.
     * @param tile the tile to be checked.
     * @return true iff a Dark Kong can be performed, false otherwise.
     */
    public boolean checkDarkKong(Tile tile) {
        return hand.containsTile(tile, 4);
    }

    /**
     * Checks if a Bright Kong can be performed with a self-drawn tile.
     * @param tile the tile to be checked.
     * @return true iff a Bright Kong can be performed, false otherwise.
     */
    public boolean checkBrightKongSelfDraw(Tile tile) {
        return revealedHand.containsPong(tile);
    }

    /**
     * Checks if a Bright Kong can be performed with a tile discarded by the opponent.
     * @param tile the tile to be checked.
     * @return true iff a Bright Kong can be performed, false otherwise.
     */
    public boolean checkBrightKongFromOpponent(Tile tile) {
        return hand.containsTile(tile, 3);
    }

    /**
     * Checks if a Pong can be performed with a tile discarded by the opponent.
     * @param tile the tile to be checked.
     * @return true iff a Pong can be performed, false otherwise.
     */
    public boolean checkPong(Tile tile) {
        return hand.containsTile(tile, 2);
    }

    /**
     * Checks if a Sheung can be performed with a tile discarded by the immediate previous player.
     * @param tile the tile to be checked.
     * @return true iff a Sheung can be performed, false otherwise.
     */
    public List<List<Tile>> checkSheung(Tile tile) {
        TileType tileType = tile.getTileType();
        if (tileType == TileType.WORD_WIND || tileType == TileType.WORD_DRAGON
                || tileType == TileType.FLOWER_SEASON || tileType == TileType.FLOWER_PLANT) {
            return new ArrayList<>();
        }
        List<Tile> sameTypeTiles = hand.getTiles()
                .stream()
                .filter(t -> t.getTileType().equals(tileType))
                .toList();
        int tileOrdinal = tile.ordinal();
        List<List<Tile>> validSheungs = new ArrayList<>();

        for (int i = 0; i < sameTypeTiles.size(); i++) {
            for (int j = i + 1; j < sameTypeTiles.size(); j++) {
                Tile first = sameTypeTiles.get(i);
                Tile second = sameTypeTiles.get(j);

                if ((first.ordinal() == tileOrdinal - 2 && second.ordinal() == tileOrdinal - 1) // (x-2, x-1, x)
                        || (first.ordinal() == tileOrdinal - 1 && second.ordinal() == tileOrdinal + 1) // (x-1, x, x+1)
                        || (first.ordinal() == tileOrdinal + 1 && second.ordinal() == tileOrdinal + 2)) { // (x, x+1, x+2)
                    // add valid combo to list of valid Sheungs
                    List<Tile> validCombo = Arrays.asList(first, second, tile);
                    Collections.sort(validCombo);
                    validSheungs.add(validCombo);
                }
            }
        }
        Set<List<Tile>> uniqueGroups = new LinkedHashSet<>(validSheungs);
        return new ArrayList<>(uniqueGroups);
    }

    /**
     * Checks if there is a valid self-draw win condition.
     * @return true iff the win condition is satisfied, false otherwise.
     */
    public boolean checkWin() {
        return WinChecker.checkWin(hand);
    }

    /**
     * Checks if there is a valid win condition when a tile is taken from an opponent discard.
     * @param tile the tile most recently discarded.
     * @return true iff the win condition is satisfied, false otherwise.
     */
    public boolean checkWin(Tile tile) {
        return WinChecker.checkWin(hand, tile);
    }

    /**
     * Checks for the special win condition of sixteen disjoint tiles.
     * @param tiles the list of tiles available.
     * @return true iff a win condition is found, false otherwise.
     */
    private boolean checkSixteenDisjoint(List<Tile> tiles) {
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
    private boolean checkThirteenOrphans(List<Tile> tiles) {
        List<Tile> remainingTiles = new ArrayList<>(tiles);

        int n = remainingTiles.size();
        if (n != 17) {
            return false;
        }

        // the tiles that form a Thirteen Orphans hand
        List<Tile> thirteenOrphans = List.of(Tile.MAAN_1, Tile.MAAN_9, Tile.SOK_1, Tile.SOK_9,
                Tile.TUNG_1, Tile.TUNG_9, Tile.WIND_EAST, Tile.WIND_SOUTH, Tile.WIND_WEST,
                Tile.WIND_NORTH, Tile.WORD_ZHONG, Tile.WORD_FAT, Tile.WORD_BAT);

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

    /**
     * Checks for the special win condition of Lik Ku Lik Ku.
     * @param tiles the list of tiles available.
     * @return true iff a win condition is found, false otherwise.
     */
    private boolean checkLikKuLikKu(List<Tile> tiles) {
        Tile tile = checkPairs(tiles);
        if (tile == null) {
            return false;
        }
        List<Tile> remainingTiles = new ArrayList<>(tiles);
        remainingTiles.remove(tile);
        remainingTiles.remove(tile);
        return remainingTiles.remove(tile);
    }

    /**
     * Checks if every tile other than one of them can form a pair of two identical tiles.
     * @param tiles the tiles to be checked.
     * @return the lone tile iff all other tiles can be paired, else null.
     */
    private Tile checkPairs(List<Tile> tiles) {
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
    private boolean canFormGroups(List<Tile> tiles) {
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

    /**
     * Retrieves a string representation of the player's entire hand, including revealed and
     * unrevealed tiles.
     * @return the string representation.
     */
    public String toString() {
        String revealedHand = getRevealedHand().toString();
        if (revealedHand.isBlank()) {
            return getHand().toString();
        }
        return getRevealedHand() + "    " + getHand();
    }

    /**
     * Retrieves a string representation of the player's entire hand, including revealed and
     * unrevealed tiles, with the last drawn tile separated to the right.
     * @return the string representation.
     */
    public String toStringSepLastDrawn() {
        String revealedHand = getRevealedHand().toString();
        if (revealedHand.isBlank()) {
            return getHand().toStringSepLastDrawn();
        }
        return getRevealedHand() + "    " + getHand().toStringSepLastDrawn();
    }

    /**
     * Retrieves a string representation of the player's entire hand, including revealed and
     * unrevealed tiles, with the last drawn tile separated to the right.
     * @return the string representation.
     */
    public String toStringWithChoice(Tile tile) {
        String revealedHand = getRevealedHand().toString();
        String hand = getHand().toString() + " " + tile;
        if (revealedHand.isBlank()) {
            return hand;
        }
        return getRevealedHand() + "    " + hand;
    }

    /**
     * Retrieves a string representation of the player's entire hand from the opponent's view.
     * Revealed tiles are displayed normally, but unrevealed tiles are obscured.
     * @return the string representation.
     */
    public String toStringOpponentView() {
        String revealedHand = getRevealedHand().toStringOpponentView();
        if (revealedHand.isBlank()) {
            return getHand().toStringOpponentView();
        }
        return getRevealedHand().toStringOpponentView() + "    " + getHand().toStringOpponentView();
    }
}
