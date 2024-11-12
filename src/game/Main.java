package game;

import game.board.HandManager;
import game.board.WinChecker;
import game.board.pile.DiscardPile;
import game.board.tile.Tile;
import game.core.GameInterface;
import game.core.MahjongPoint;
import game.core.ScoreCalculator;
import game.player.Bot;
import game.player.Player;
import game.player.RealPlayer;
import game.player.data.Seat;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
//        List<Tile> hand = List.of(Tile.TUNG_1, Tile.TUNG_9, Tile.SOK_1,
//                Tile.SOK_9, Tile.MAAN_1, Tile.MAAN_9, Tile.MAAN_9,
//                Tile.TUNG_9, Tile.TUNG_9, Tile.TUNG_9,
//                Tile.WIND_EAST, Tile.WIND_SOUTH, Tile.WIND_WEST, Tile.WIND_NORTH,
//                Tile.WORD_ZHONG, Tile.WORD_FAT, Tile.WORD_BAT);
//        System.out.println(ScoreCalculator.getValidHands(hand));

//        HandManager handManager = new HandManager();
//
//        // CONCEALED HAND
//        List<List<Tile>> hand = List.of(List.of(Tile.TUNG_9, Tile.TUNG_9));
//        for (List<Tile> group : hand) {
//            for (Tile tile : group) {
//                handManager.addToHand(tile);
//            }
//        }
//
//        // REVEALED HAND
//        List<Tile> revealedTiles = List.of(Tile.SOK_2, Tile.SOK_3, Tile.SOK_4, Tile.SOK_5, Tile.SOK_7, Tile.SOK_8,
//                Tile.TUNG_4, Tile.TUNG_5, Tile.TUNG_7, Tile.TUNG_8);
//        for (Tile tile : revealedTiles) {
//            handManager.addToHand(tile);
//        }
//        handManager.addGroup(Tile.SOK_1, new ArrayList<>(List.of(Tile.SOK_2, Tile.SOK_3)));
//        handManager.addGroup(Tile.SOK_6, new ArrayList<>(List.of(Tile.SOK_4, Tile.SOK_5)));
//        handManager.addGroup(Tile.SOK_9, new ArrayList<>(List.of(Tile.SOK_7, Tile.SOK_8)));
//        handManager.addGroup(Tile.TUNG_6, new ArrayList<>(List.of(Tile.TUNG_4, Tile.TUNG_5)));
//        handManager.addGroup(Tile.TUNG_9, new ArrayList<>(List.of(Tile.TUNG_7, Tile.TUNG_8)));
//        System.out.println(handManager.toString());
//        System.out.println();
//        List<MahjongPoint> points = ScoreCalculator.getPoints(Seat.EAST, Seat.EAST, Seat.EAST, Seat.SOUTH,
//                new ArrayList<>(hand), hand.getLast().getLast(), handManager, 40, new ArrayList<>(),
//                false, 0);
//        Collections.sort(points);
//        for (MahjongPoint point : points) {
//            System.out.println(point);
//        }

//        List<Seat> seats = new ArrayList<>(List.of(Seat.values()));
//        List<Player> playerList = new ArrayList<>();
//        for (String name : List.of("Happy Guy", "Botty", "Dick", "Head")) {
//            Player player = new Bot(name);
//            player.setSeat(seats.removeFirst());
//            playerList.add(player);
//        }
//        playerList.add(new RealPlayer("Bruh"));

        GameInterface gameInterface = new GameInterface();
        gameInterface.run(true);
    }
}