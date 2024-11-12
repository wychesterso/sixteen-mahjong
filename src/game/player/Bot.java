package game.player;

import game.board.tile.Tile;
import game.board.tile.TileType;
import game.core.Prompter;

import java.util.*;

public class Bot extends Player {
    public Bot(String name) {
        super(name);
    }

    public Bot(String name, int score) {
        super(name, score);
    }

    private void pause() {
        try {
            // Thread.sleep(0);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean decideWin(String boardState) {
        return true;
    }

    @Override
    public boolean decideWin(Tile tile, String boardState) {
        return true;
    }

    @Override
    public boolean decideSheung(Tile tile, String boardState) {
        pause();
        return decideBrightKong(tile, boardState);
    }

    @Override
    public boolean decidePong(Tile tile, String boardState) {
        pause();
        return true;
    }

    @Override
    public boolean decideDarkKong(Tile tile, String boardState) {
        pause();
        return decideBrightKong(tile, boardState);
    }

    @Override
    public boolean decideBrightKong(Tile tile, String boardState) {
        pause();
        List<Tile> tilesInHand = new ArrayList<>(getHandManager().getHand().getTiles());
        List<Tile> groupedTiles = new ArrayList<>();
        groupThreeConsecutiveTiles(tilesInHand, groupedTiles);
        return !groupedTiles.contains(tile);
    }

    @Override
    public boolean decideBrightKongNoDraw(Tile tile, String boardState) {
        pause();
        return decideBrightKong(tile, boardState);
    }

    @Override
    public List<Tile> pickSheungCombo(List<List<Tile>> validSheungs) {
        pause();
        return validSheungs.getFirst();
    }

    @Override
    public Tile pickDiscardTile(String boardState, List<Tile> discardedTiles) {
        pause();
        List<Tile> tilesInHand = getHandManager().getHand().getTiles();
        List<Tile> discardOptions = groupTiles(tilesInHand, discardedTiles);
        Tile discardTile = discardOptions.getFirst();
        Prompter.printLine();
        Prompter.printLine(this.toStringWithSeat() + " discarded " + discardTile);
        return discardTile;
    }

    private List<Tile> groupTiles(List<Tile> tiles, List<Tile> discardedTiles) {
        List<Tile> ungroupedTiles = new ArrayList<>(tiles);
        List<Tile> groupedTiles = new ArrayList<>();
        List<Tile> discardList = new ArrayList<>(discardedTiles);
        List<Tile> returnTiles = new ArrayList<>();

        // GROUP 1: Three identical word tiles
        for (TileType type : List.of(TileType.WORD_WIND, TileType.WORD_DRAGON)) {
            groupThreeIdenticalTiles(ungroupedTiles, groupedTiles, type);
        }

        // GROUP 2: Two identical word tiles
        for (TileType type : List.of(TileType.WORD_WIND, TileType.WORD_DRAGON)) {
            groupTwoIdenticalTiles(ungroupedTiles, groupedTiles, discardList, type);
        }

        // Remove all tiles of type WORD_WIND or WORD_DRAGON from further grouping
        for (Tile tile : ungroupedTiles) {
            if (tile.getTileType() == TileType.WORD_WIND || tile.getTileType() == TileType.WORD_DRAGON) {
                returnTiles.add(tile);
            }
        }
        ungroupedTiles.removeIf(tile -> tile.getTileType() == TileType.WORD_WIND || tile.getTileType() == TileType.WORD_DRAGON);

        // GROUP 3: Six tiles of same type with ordinals n, n+1, n+1, n+1, n+1, n+2
        groupNNNN12(ungroupedTiles, groupedTiles, 6);

        // GROUP 4: Five tiles of same type with ordinals n, n+1, n+1, n+1, n+2
        groupNNNN12(ungroupedTiles, groupedTiles, 5);

        // GROUP 5: Three identical tiles
        groupThreeIdenticalTiles(ungroupedTiles, groupedTiles, null);

        // GROUP 6: Three consecutive tiles of same type
        groupThreeConsecutiveTiles(ungroupedTiles, groupedTiles);

        // GROUP 7: Two identical tiles
        groupTwoIdenticalTiles(ungroupedTiles, groupedTiles, discardList, null);

        // GROUP 8: Two consecutive tiles of same type
        groupTwoConsecutiveTiles(ungroupedTiles, groupedTiles);

        // GROUP 9: Two tiles of same type with ordinals n, n+2
        groupTwoTilesWithGap(ungroupedTiles, groupedTiles);

        // GROUP 10: Two identical tiles
        groupTwoIdenticalTiles(ungroupedTiles, groupedTiles, new ArrayList<>(), null);

        // decide discard order by the frequency of that tile found in discard pile
        returnTiles.sort((tile1, tile2) -> {
            int count1 = Collections.frequency(discardedTiles, tile1);
            int count2 = Collections.frequency(discardedTiles, tile2);
            return Integer.compare(count2, count1);
        });
        ungroupedTiles.sort((tile1, tile2) -> {
            int count1 = Collections.frequency(discardedTiles, tile1);
            int count2 = Collections.frequency(discardedTiles, tile2);
            return Integer.compare(count2, count1);
        });

        // Returns a
        returnTiles.addAll(ungroupedTiles);
        returnTiles.addAll(groupedTiles.reversed());
        return new ArrayList<>(returnTiles);
    }

    /**
     * Attempt to group tiles from the ungrouped list if there are three identical instances.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     * @param type the tile type to be grouped.
     */
    private void groupThreeIdenticalTiles(List<Tile> ungroupedTiles, List<Tile> groupedTiles, TileType type) {
        for (int i = 0; i < ungroupedTiles.size() - 2; i++) {
            Tile tile = ungroupedTiles.get(i);
            if ((type == null || tile.getTileType() == type)
                    && Collections.frequency(ungroupedTiles, tile) >= 3) {
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                ungroupedTiles.removeAll(Arrays.asList(tile, tile, tile));
            }
        }
    }

    /**
     * Attempt to group tiles from the ungrouped list if there are two identical instances.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     * @param discardList the tiles already discarded during the game.
     * @param type the tile types to be grouped.
     * @ensures only forms groups when a Pong is still possible, i.e. not more than one instance of
     * the tile found in the discard list.
     */
    private void groupTwoIdenticalTiles(List<Tile> ungroupedTiles, List<Tile> groupedTiles, List<Tile> discardList, TileType type) {
        for (int i = 0; i < ungroupedTiles.size() - 1; i++) {
            Tile tile = ungroupedTiles.get(i);
            if ((type == null || tile.getTileType() == type) &&
                    Collections.frequency(ungroupedTiles, tile) >= 2 &&
                    Collections.frequency(discardList, tile) <= 1) {
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                ungroupedTiles.removeAll(Arrays.asList(tile, tile));
            }
        }
    }

    /**
     * Attempt to group tiles in the form n, n+1, ... , n+1, n+2 of the same type.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     * @param groupSize the size of the group.
     */
    private void groupNNNN12(List<Tile> ungroupedTiles, List<Tile> groupedTiles, int groupSize) {
        for (int i = 0; i < ungroupedTiles.size(); i++) {
            Tile tile = ungroupedTiles.get(i);
            TileType type = tile.getTileType();
            int ord = tile.ordinal();

            long countN = ungroupedTiles.stream().filter(t -> t == tile).count();
            long countN1 = ungroupedTiles.stream().filter(t -> t.ordinal() == ord + 1 && t.getTileType() == type).count();
            long countN2 = ungroupedTiles.stream().filter(t -> t.ordinal() == ord + 2 && t.getTileType() == type).count();

            if (countN >= (groupSize - 2) && countN1 >= 3 && countN2 >= 1) {
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                if (groupSize == 6) groupedTiles.add(tile);

                ungroupedTiles.removeAll(Arrays.asList(tile, tile, tile));
                ungroupedTiles.removeIf(t -> t.ordinal() == ord + 1 && t.getTileType() == type);
                ungroupedTiles.removeIf(t -> t.ordinal() == ord + 2 && t.getTileType() == type);
            }
        }
    }

    /**
     * Attempt to group three consecutive tiles of the same type.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     */
    private void groupThreeConsecutiveTiles(List<Tile> ungroupedTiles, List<Tile> groupedTiles) {
        for (int i = 0; i < ungroupedTiles.size() - 2; i++) {
            Tile tile1 = ungroupedTiles.get(i);
            TileType type = tile1.getTileType();

            for (int j = i + 1; j < ungroupedTiles.size() - 1; j++) {
                Tile tile2 = ungroupedTiles.get(j);
                for (int k = j + 1; k < ungroupedTiles.size(); k++) {
                    Tile tile3 = ungroupedTiles.get(k);

                    if (tile1.getTileType() == tile2.getTileType() && tile2.getTileType() == tile3.getTileType()) {
                        int ord1 = tile1.ordinal(), ord2 = tile2.ordinal(), ord3 = tile3.ordinal();
                        if ((ord1 + 1 == ord2 && ord2 + 1 == ord3) || (ord1 + 2 == ord2 && ord2 + 2 == ord3)) {
                            groupedTiles.add(tile1);
                            groupedTiles.add(tile2);
                            groupedTiles.add(tile3);
                            ungroupedTiles.removeAll(Arrays.asList(tile1, tile2, tile3));
                        }
                    }
                }
            }
        }
    }

    /**
     * Attempt to group two consecutive tiles of the same type.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     */
    private void groupTwoConsecutiveTiles(List<Tile> ungroupedTiles, List<Tile> groupedTiles) {
        for (int i = 0; i < ungroupedTiles.size() - 1; i++) {
            Tile tile1 = ungroupedTiles.get(i);
            for (int j = i + 1; j < ungroupedTiles.size(); j++) {
                Tile tile2 = ungroupedTiles.get(j);
                if (tile1.getTileType() == tile2.getTileType() && tile1.ordinal() + 1 == tile2.ordinal()) {
                    groupedTiles.add(tile1);
                    groupedTiles.add(tile2);
                    ungroupedTiles.removeAll(Arrays.asList(tile1, tile2));
                }
            }
        }
    }

    /**
     * Attempt to group two consecutive tiles with a gap (e.g. 2 & 4, 5 & 7) of the same type.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     */
    private void groupTwoTilesWithGap(List<Tile> ungroupedTiles, List<Tile> groupedTiles) {
        for (int i = 0; i < ungroupedTiles.size() - 1; i++) {
            Tile tile1 = ungroupedTiles.get(i);
            for (int j = i + 1; j < ungroupedTiles.size(); j++) {
                Tile tile2 = ungroupedTiles.get(j);
                if (tile1.getTileType() == tile2.getTileType() && tile1.ordinal() + 2 == tile2.ordinal()) {
                    groupedTiles.add(tile1);
                    groupedTiles.add(tile2);
                    ungroupedTiles.removeAll(Arrays.asList(tile1, tile2));
                }
            }
        }
    }

    @Override
    public Tile pickDiscardTileNoDraw(String boardState, List<Tile> discardedTiles) {
        pause();
        return pickDiscardTile(boardState, discardedTiles);
    }
}
