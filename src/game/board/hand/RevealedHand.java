package game.board.hand;

import game.board.tile.Tile;
import game.board.tile.TileType;
import game.core.ScoreCalculator;

import java.util.*;

/**
 * A hand containing the tiles landed by a player throughout the round.
 */
public class RevealedHand {
    private final List<List<Tile>> groups = new ArrayList<>();
    private final List<List<Tile>> brightKongs = new ArrayList<>();
    private final List<List<Tile>> darkKongs = new ArrayList<>();
    private final List<Tile> flowers = new ArrayList<>();
    private boolean newGrassFormed = false;
    private boolean newToiFormed = false;

    /**
     * Creates a player's revealed hand.
     */
    public RevealedHand() {}

    /**
     * Retrieves every landed Pong or Sheung in the player's revealed hand.
     * @return the groups in the revealed hand.
     */
    public List<List<Tile>> getGroups() {
        return groups;
    }

    /**
     * Retrieves every Bright Kong in the player's revealed hand.
     * @return the groups of Bright Kongs.
     */
    public List<List<Tile>> getBrightKongs() {
        return brightKongs;
    }

    /**
     * Retrieves every Dark Kong in the player's revealed hand.
     * @return the groups of Dark Kongs.
     */
    public List<List<Tile>> getDarkKongs() {
        return darkKongs;
    }

    /**
     * Retrieves every flower tile in the player's revealed hand.
     * @return a list of flower tiles.
     */
    public List<Tile> getFlowers() {
        return flowers;
    }

    /**
     * Resets the revealed hand for a new game.
     */
    public void clearHand() {
        groups.clear();
        brightKongs.clear();
        darkKongs.clear();
        flowers.clear();
    }

    /**
     * Checks if there is at least one Pong in the revealed tiles.
     * @return true iff there is a pong, false otherwise.
     */
    public boolean containsPong() {
        return groups.stream().anyMatch(group -> group.size() == 3
                                        && group.getFirst() == group.get(1)
                                        && group.getFirst() == group.get(2));
    }

    /**
     * Checks if there is a Pong of the specified tile in the revealed tiles.
     * @param tile the tile to be checked.
     * @return true iff there is a Pong, false otherwise.
     */
    public boolean containsPong(Tile tile) {
        return groups.stream().anyMatch(group -> group.size() == 3
                                        && group.getFirst() == tile
                                        && group.get(1) == tile
                                        && group.get(2) == tile);
    }

    /**
     * Checks if there is at least one Sheung in the revealed tiles.
     * @return true iff there is a Sheung, false otherwise.
     */
    public boolean containsSheung() {
        return groups.stream().anyMatch(group -> group.size() == 3
                                        && group.getFirst().getTileType() == group.get(1).getTileType()
                                        && group.getFirst().getTileType() == group.get(2).getTileType()
                                        && group.getFirst().ordinal() + 1 == group.get(1).ordinal()
                                        && group.getFirst().ordinal() + 2 == group.get(2).ordinal());
    }

    /**
     * Checks if there is a Sheung of the specified tile in the revealed tiles.
     * @param tile the first tile in the Sheung group, in ascending order.
     * @return true iff there is a Sheung, false otherwise.
     */
    public boolean containsSheung(Tile tile) {
        TileType[] invalidTypes = new TileType[]{TileType.WORD_WIND, TileType.WORD_DRAGON,
                TileType.FLOWER_SEASON, TileType.FLOWER_PLANT};
        for (TileType type : invalidTypes) {
            if (tile.getTileType() == type) {
                throw new IllegalArgumentException("Invalid tile type for a sheung!");
            }
        }
        return groups.stream().anyMatch(group -> group.size() == 3
                                        && group.getFirst().getTileType() == group.get(1).getTileType()
                                        && group.getFirst().getTileType() == group.get(2).getTileType()
                                        && tile == group.getFirst()
                                        && tile.ordinal() + 1 == group.get(1).ordinal()
                                        && tile.ordinal() + 2 == group.get(2).ordinal());
    }

    /**
     * Adds a group of tiles to the revealed hand.
     * @param group the group of tiles to be added.
     * @requires the group is valid (i.e. it is a valid Pong or a Sheung).
     */
    public void addGroup(List<Tile> group) {
        Collections.sort(group);
        groups.add(group);
    }

    /**
     * Adds a bright kong to the revealed hand.
     * @param tile the tile of the Bright Kong.
     * @requires the operation is valid (i.e. there is an existing Pong that can be used).
     */
    public void addBrightKong(Tile tile) {
        List<Tile> originalGroup = groups.stream()
                        .filter(group -> group.size() == 3
                                && tile == group.getFirst()
                                && tile == group.get(1)
                                && tile == group.get(2))
                        .findFirst()
                        .orElse(null);

        groups.remove(originalGroup);
        originalGroup.add(tile);
        brightKongs.add(originalGroup);
    }

    /**
     * Adds a bright kong to the revealed hand, using a tile discarded by an opponent.
     * @param tile the tile of the Bright Kong.
     */
    public void addBrightKongFromOpponent(Tile tile) {
        List<Tile> group = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            group.add(tile);
        }
        brightKongs.add(group);
    }

    /**
     * Adds a dark kong to the revealed hand.
     * @param group the group of tiles to be added.
     * @requires the group is valid (i.e. it is a valid Kong).
     */
    public void addDarkKong(List<Tile> group) {
        darkKongs.add(group);
    }

    /**
     * Adds a flower to the revealed hand.
     * @param tile the flower tile to be added.
     * @throws IllegalArgumentException if the tile is not a flower.
     */
    public void addFlower(Tile tile) {
        if (tile.getTileType() != TileType.FLOWER_SEASON && tile.getTileType() != TileType.FLOWER_PLANT) {
            throw new IllegalArgumentException("Cannot add non-flower tile.");
        }
        int originalGrass = countGrass();
        int originalTois = countTois();
        flowers.add(tile);
        Collections.sort(flowers);
        int newGrass = countGrass();
        int newTois = countTois();
        newGrassFormed = originalGrass != newGrass;
        newToiFormed = originalTois != newTois;
    }

    private String flowersString() {
        StringBuilder output = new StringBuilder();
        for (Tile tile : flowers) {
            output.append(tile);
        }
        return output.toString();
    }

    private int countGrass() {
        int count = 0;
        int numFlowers = flowers.size();
        if (numFlowers >= 4) {
            for (int i = 0; i < numFlowers; i++) {
                for (int j = i + 1; j < numFlowers; j++) {
                    for (int k = j + 1; k < numFlowers; k++) {
                        for (int l = k + 1; l < numFlowers; l++) {
                            Set<Integer> tileNums = new HashSet<>();
                            Tile tile1 = flowers.get(i);
                            tileNums.add(tile1.getTileNum() % 4);
                            Tile tile2 = flowers.get(j);
                            tileNums.add(tile2.getTileNum() % 4);
                            Tile tile3 = flowers.get(k);
                            tileNums.add(tile3.getTileNum() % 4);
                            Tile tile4 = flowers.get(l);
                            tileNums.add(tile4.getTileNum() % 4);
                            if (tileNums.size() == 4
                                    && !ScoreCalculator.checkSameType(List.of(tile1, tile2,
                                    tile3, tile4))) {
                                count += 1;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    private int countTois() {
        int count = 0;
        if (flowers.contains(Tile.FLOWER_SPRING) && flowers.contains(Tile.FLOWER_SUMMER)
                && flowers.contains(Tile.FLOWER_AUTUMN) && flowers.contains(Tile.FLOWER_WINTER)) {
            count += 1;
        }
        if (flowers.contains(Tile.FLOWER_MUI) && flowers.contains(Tile.FLOWER_LAN)
                && flowers.contains(Tile.FLOWER_KUK) && flowers.contains(Tile.FLOWER_CHUK)) {
            count += 1;
        }
        return count;
    }

    public boolean newGrassFormed() {
        return newGrassFormed;
    }

    public boolean newToiFormed() {
        return newToiFormed;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        String flowersString = flowersString();
        if (!flowersString.isBlank()) {
            output.append(flowersString + " ");
        }

        for (List<Tile> group : groups) {
            for (Tile tile : group) {
                output.append(tile);
            }
            output.append(" ");
        }
        for (List<Tile> kongGroup : brightKongs) {
            for (Tile tile : kongGroup) {
                output.append(tile);
            }
            output.append(" ");
        }
        for (List<Tile> darkKongGroup : darkKongs) {
            for (Tile tile : darkKongGroup) {
                output.append(tile);
            }
            output.append(" ");
        }
        if (output.isEmpty()) {
            return "";
        }
        return output.substring(0, output.length() - 1);
    }

    public String toStringOpponentView() {
        StringBuilder output = new StringBuilder();
        String flowersString = flowersString();
        if (!flowersString.isBlank()) {
            output.append(flowersString + " ");
        }

        for (List<Tile> group : groups) {
            for (Tile tile : group) {
                output.append(tile);
            }
            output.append(" ");
        }
        for (List<Tile> kongGroup : brightKongs) {
            for (Tile tile : kongGroup) {
                output.append(tile);
            }
            output.append(" ");
        }
        for (List<Tile> darkKongGroup : darkKongs) {
            for (Tile tile : darkKongGroup) {
                output.append("🀫");
            }
            output.append(" ");
        }
        if (output.isEmpty()) {
            return "";
        }
        return output.substring(0, output.length() - 1);
    }
}
