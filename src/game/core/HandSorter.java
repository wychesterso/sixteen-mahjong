package game.core;

import game.board.tile.Tile;

import java.util.List;
import java.util.stream.IntStream;

public class HandSorter {
    public static void sortLists(List<List<Tile>> lists) {
        lists.sort((list1, list2) -> {
            // First, compare by size of inner lists
            int sizeComparison = Integer.compare(list2.size(), list1.size());
            if (sizeComparison != 0) {
                return sizeComparison;
            }

            // If sizes are equal, compare by elements in the inner lists
            int minLength = Math.min(list1.size(), list2.size());
            return IntStream.range(0, minLength)
                    .map(i -> list1.get(i).compareTo(list2.get(i)))
                    .filter(result -> result != 0)
                    .findFirst()
                    .orElse(0);
        });
    }

    public static void sortPongs(List<List<Tile>> lists) {
        lists.sort((list1, list2) -> {
            int minLength = Math.min(list1.size(), list2.size());
            return IntStream.range(0, minLength)
                    .map(i -> list1.get(i).compareTo(list2.get(i)))
                    .filter(result -> result != 0)
                    .findFirst()
                    .orElse(0);
        });
    }

    public static void sortSheungsByNum(List<List<Tile>> lists) {
        lists.sort((list1, list2) -> {
            Tile tile1 = list1.getFirst();
            Tile tile2 = list2.getFirst();
            int starterComparison = tile1.getTileNum() - tile2.getTileNum();
            if (starterComparison != 0) {
                return starterComparison;
            }

            return tile1.ordinal() - tile2.ordinal();
        });
    }

    public static boolean groupEquals(List<Tile> group1, List<Tile> group2) {
        return group1.size() == group2.size()
                && IntStream.range(0, group1.size())
                .allMatch(i -> group1.get(i) == group2.get(i));
    }

    public static boolean handEquals(List<List<Tile>> hand1, List<List<Tile>> hand2) {
        return hand1.size() == hand2.size()
                && IntStream.range(0, hand1.size())
                .allMatch(i -> hand1.get(i) == hand2.get(i));
    }
}
