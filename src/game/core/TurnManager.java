package game.core;

import game.board.PileManager;
import game.board.tile.TileType;
import game.core.turn.data.TurnEnder;
import game.board.tile.Tile;
import game.player.Player;
import game.core.turn.Turn;
import game.player.data.Seat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A managing class that represents a game round, handling turns during a round.
 */
public class TurnManager {
    private final List<Player> playerList;
    private Player currentPlayer = null;
    private final List<Player> winners = new ArrayList<>();
    private Turn currentTurn = null;
    private PileManager pileManager;
    private String lastEvent;
    private int discardCount = 0;

    /**
     * Creates a turn manager instance.
     * @param playerList the players participating in the round.
     */
    public TurnManager(List<Player> playerList) {
        if (playerList.size() != 4) {
            throw new IllegalArgumentException("Incorrect amount of players!");
        }
        playerList.sort(Comparator.comparing(Player::getSeat));
        this.playerList = playerList;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Initializes a new turn.
     * @return a new turn belonging to the current player.
     */
    public Turn initializeTurn() {
        return new Turn(currentPlayer, getOtherPlayers(), boardState(),
                pileManager.getDiscardPile().getDiscardedTiles());
    }

    /**
     * Starts a new round.
     * @param seat the seat of the Zhong player.
     * @throws InvalidKongException if an invalid Kong is attempted.
     */
    public TurnEnder startRound(Seat seat) throws InvalidKongException, EmptyPileException {
        // INITIAL DRAWS
        winners.clear();
        pileManager = new PileManager(8);
        for (Player player : playerList) {
            int numInitialTiles;
            if (player.getSeat() == seat) {
                numInitialTiles = 17;
                currentPlayer = player;
            } else {
                numInitialTiles = 16;
            }
            for (int i = 0; i < numInitialTiles; i++) {
                Tile newTile = pileManager.drawTile();
                while (newTile.getTileType() == TileType.FLOWER_SEASON
                        || newTile.getTileType() == TileType.FLOWER_PLANT) {
                    Prompter.printLine();
                    Prompter.printLine(player.toStringWithSeat() + " drew flower tile: " + newTile);
                    player.getHandManager().addFlower(newTile);
                    if (player.getHandManager().getRevealedHand().newToiFormed()) {
                        Prompter.printLine(player.toStringWithSeat() + " formed a new type of flowers!");
                        for (Player otherPlayer : getOtherPlayers(player)) {
                            otherPlayer.deductScore(10);
                            Prompter.printLine(otherPlayer.toStringWithSeat() + ": -10");
                        }
                        player.addScore(30);
                        Prompter.printLine(player.toStringWithSeat() + ": +30");
                    }
                    if (player.getHandManager().getRevealedHand().newGrassFormed()) {
                        Prompter.printLine(player.toStringWithSeat() + " formed a new set of flowers!");
                        for (Player otherPlayer : getOtherPlayers(player)) {
                            otherPlayer.deductScore(5);
                            Prompter.printLine(otherPlayer.toStringWithSeat() + ": -5");
                        }
                        player.addScore(15);
                        Prompter.printLine(player.toStringWithSeat() + ": +15");
                    }
                    newTile = pileManager.drawBonusTile();
                }
                player.getHandManager().addToHand(newTile);
            }
        }

        // ZHONG STARTS FIRST TURN
        TurnEnder turnEnder = startTurnFirstDraw();

        // LOOP TURNS UNTIL GAME ENDS
        while (turnEnder != TurnEnder.END_GAME_WIN && turnEnder != TurnEnder.END_GAME_DRAW
                && turnEnder != TurnEnder.END_GAME_WIN_SELFDRAW) {
            try {
                turnEnder = startNewTurn(turnEnder);
            } catch (EmptyPileException e) {
                turnEnder = TurnEnder.END_GAME_DRAW;
            }
        }
        Prompter.printLine();
        return turnEnder;
    }

    /**
     * Starts a new turn based on the previous turn ending event.
     * @param prevTurnEnder the previous turn's turn ending event.
     * @return the new turn's turn ending event.
     * @throws InvalidKongException if an invalid Kong is attempted.
     * @throws EmptyPileException when attempting to draw from an empty pile.
     */
    public TurnEnder startNewTurn(TurnEnder prevTurnEnder)
            throws InvalidKongException, EmptyPileException {
        switch (prevTurnEnder) {
            case DRAW_FLOWER -> {
                lastEvent = "flower";
                Prompter.printLine();
                Prompter.printLine(currentPlayer.toStringWithSeat() + " drew flower tile: "
                        + currentTurn.getDrawnTile());
                if (currentPlayer.getHandManager().getRevealedHand().newToiFormed()) {
                    Prompter.printLine(currentPlayer.toStringWithSeat() + " formed a new type of flowers!");
                    for (Player otherPlayer : getOtherPlayers()) {
                        otherPlayer.deductScore(10);
                        Prompter.printLine(otherPlayer.toStringWithSeat() + ": -10");
                    }
                    currentPlayer.addScore(30);
                    Prompter.printLine(currentPlayer.toStringWithSeat() + ": +30");
                }
                if (currentPlayer.getHandManager().getRevealedHand().newGrassFormed()) {
                    Prompter.printLine(currentPlayer.toStringWithSeat() + " formed a new set of flowers!");
                    for (Player otherPlayer : getOtherPlayers()) {
                        otherPlayer.deductScore(5);
                        Prompter.printLine(otherPlayer.toStringWithSeat() + ": -5");
                    }
                    currentPlayer.addScore(15);
                    Prompter.printLine(currentPlayer.toStringWithSeat() + ": +15");
                }
                if (currentPlayer.getHandManager().getRevealedHand().getFlowers().size() == 8) {
                    if (currentPlayer.decideWin(boardState(currentPlayer))) {
                        winners.add(currentPlayer);
                        return TurnEnder.END_GAME_WIN_SELFDRAW;
                    }
                }
                return startTurnBonusDraw();
            }
            case BRIGHT_KONG, DARK_KONG -> {
                if (lastEvent.equals("kong") || lastEvent.equals("double kong")) {
                    lastEvent = "double kong";
                }
                return startTurnBonusDraw();
            }
            case DISCARD_TILE -> {
                lastEvent = "discard";
                discardCount += 1;
                Tile discardedTile = currentTurn.getDiscardTile();
                pileManager.addDiscardedTile(discardedTile);
                List<Player> otherPlayers = getOtherPlayers();

                // CHECK WIN
                for (Player player : otherPlayers) {
                    if (player.getHandManager().checkWin(discardedTile)) {
                        if (player.decideWin(discardedTile, boardState(player))) {
                            player.getHandManager().addToHand(discardedTile);
                            winners.add(player);
                        }
                    }
                }
                if (!winners.isEmpty()) {
                    pileManager.takeLastDiscardedTile();
                    return TurnEnder.END_GAME_WIN;
                }

                // CHECK BRIGHT KONG
                for (Player player : otherPlayers) {
                    if (player.getHandManager().checkBrightKongFromOpponent(discardedTile)) {
                        if (player.decideBrightKongNoDraw(discardedTile, boardState(player))) {
                            currentPlayer = player;
                            Prompter.printLine();
                            Prompter.printLine(currentPlayer.toStringWithSeat() + " performed Bright Kong: "
                                    + discardedTile + discardedTile + discardedTile + discardedTile);
                            return startTurnBrightKongFromOpponent();
                        }
                    }
                }

                // CHECK PONG
                for (Player player : otherPlayers) {
                    if (player.getHandManager().checkPong(discardedTile)) {
                        if (player.decidePong(discardedTile, boardState(player))) {
                            currentPlayer = player;
                            List<Tile> existingTiles = new ArrayList<>();
                            for (int i = 0; i < 2; i++) {
                                existingTiles.add(discardedTile);
                            }
                            Prompter.printLine();
                            Prompter.printLine(currentPlayer.toStringWithSeat() + " performed Pong: "
                                    + discardedTile + discardedTile + discardedTile);
                            return startTurnTakeTile(existingTiles);
                        }
                    }
                }

                // other player's can't interfere no more - switch to immediate next player
                int currentIndex = playerList.indexOf(currentPlayer);
                int nextIndex = (currentIndex + 1) % playerList.size();
                currentPlayer = playerList.get(nextIndex);

                // CHECK SHEUNG
                List<List<Tile>> validSheungs = currentPlayer.getHandManager().checkSheung(discardedTile);
                if (!validSheungs.isEmpty()) {
                    if (currentPlayer.decideSheung(discardedTile, boardState())) {
                        List<Tile> pickedCombo;
                        if (validSheungs.size() == 1) {
                            pickedCombo = new ArrayList<>(validSheungs.getFirst());
                        } else {
                            pickedCombo = new ArrayList<>(currentPlayer.pickSheungCombo(validSheungs));
                        }
                        StringBuilder comboString = new StringBuilder();
                        for (Tile tile : pickedCombo) {
                            comboString.append(tile);
                        }
                        Prompter.printLine();
                        Prompter.printLine(currentPlayer.toStringWithSeat() + " performed Sheung: "
                                + comboString);
                        pickedCombo.remove(discardedTile);
                        return startTurnTakeTile(pickedCombo);
                    }
                }

                return startTurnNormalDraw();
            }
            default -> throw new RuntimeException("Unknown TurnEnder!");
        }
    }

    /**
     * Starts a turn immediately after game start.
     * @return the turn ending event.
     */
    public TurnEnder startTurnFirstDraw() throws InvalidKongException {
        currentTurn = initializeTurn();
        return currentTurn.startTurnFirstDraw();
    }

    /**
     * Starts a turn with a normal draw.
     * @return the turn ending event.
     * @throws EmptyPileException if there are no tiles left to draw.
     * @throws InvalidKongException if an invalid kong is attempted.
     */
    public TurnEnder startTurnNormalDraw() throws EmptyPileException, InvalidKongException {
        Tile drawnTile = pileManager.drawTile();
        currentTurn = initializeTurn();
        return currentTurn.startTurnDrawTile(drawnTile);
    }

    /**
     * Starts a turn with a bonus draw.
     * @return the turn ending event.
     * @throws EmptyPileException if there are no tiles left to draw.
     * @throws InvalidKongException if an invalid kong is attempted.
     */
    public TurnEnder startTurnBonusDraw() throws EmptyPileException, InvalidKongException {
        Tile drawnTile = pileManager.drawBonusTile();
        currentTurn = initializeTurn();
        return currentTurn.startTurnDrawTile(drawnTile);
    }

    /**
     * Starts a turn by taking an opponent discard to form a group, that gets sent to the
     * revealed hand.
     * @param existingTiles the tiles (not including the discard tile) used to form the group.
     * @return the turn ending event.
     * @requires the group is valid (i.e. it is a valid Pong or Sheung when combined with the
     * discard tile).
     */
    public TurnEnder startTurnTakeTile(List<Tile> existingTiles) {
        Tile takenTile = pileManager.takeLastDiscardedTile();
        currentTurn = initializeTurn();
        return currentTurn.startTurnTakeTile(takenTile, existingTiles);
    }

    /**
     * Starts a turn by taking an opponent discard to form a Bright Kong, that gets sent to the
     * revealed hand.
     * @return the turn ending event.
     * @throws InvalidKongException if an invalid kong is attempted.
     */
    public TurnEnder startTurnBrightKongFromOpponent() throws InvalidKongException {
        Tile takenTile = pileManager.takeLastDiscardedTile();
        currentTurn = initializeTurn();
        return currentTurn.startTurnBrightKongFromOpponent(takenTile);
    }

    public List<Player> getWinners() {
        return winners;
    }

    /**
     * Gets a list of players that aren't the current player, sorted by their relative seat
     * position in relation to the current player's seat.
     * @return the list of players that are not the current player.
     */
    public List<Player> getOtherPlayers() {
        return playerList.stream()
                .filter(player -> !player.equals(currentPlayer))
                .sorted(Comparator.comparingInt(player -> {
                    int currentSeat = currentPlayer.getSeat().ordinal();
                    int playerSeat = player.getSeat().ordinal();
                    return (playerSeat - currentSeat + 4) % 4;
                }))
                .toList();
    }

    /**
     * Gets a list of players that aren't the specified player, sorted by their relative seat
     * position in relation to the current player's seat.
     * @return the list of players that are not the specified player.
     */
    public List<Player> getOtherPlayers(Player specifiedPlayer) {
        return playerList.stream()
                .filter(player -> !player.equals(specifiedPlayer))
                .sorted(Comparator.comparingInt(player -> {
                    int currentSeat = specifiedPlayer.getSeat().ordinal();
                    int playerSeat = player.getSeat().ordinal();
                    return (playerSeat - currentSeat + 4) % 4;
                }))
                .toList();
    }

    public PileManager getPileManager() {
        return pileManager;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public int getDiscardCount() {
        return discardCount;
    }

    /**
     * Gets the board state from the perspective of the current player.
     * @return the string output of the board state.
     */
    public String boardState() {
        StringBuilder output = new StringBuilder();
        List<Player> otherPlayers = getOtherPlayers();
        output.append("Discards:\n").append(pileManager.getDiscardPile())
                .append("\n")
                .append(Prompter.getLine())
                .append("\n")
                .append("Left:   ")
                .append(otherPlayers.get(2).getHandManager().toStringOpponentView())
                .append("\n")
                .append("Across: ")
                .append(otherPlayers.get(1).getHandManager().toStringOpponentView())
                .append("\n")
                .append("Right:  ")
                .append(otherPlayers.get(0).getHandManager().toStringOpponentView())
                .append("\n\n")
                .append("Remaining Tiles: ")
                .append(pileManager.getUnrevealedPile().getRemainingTileCount());
        return output.toString();
    }

    /**
     * Gets the board state from the perspective of the specified player.
     * @return the string output of the board state.
     */
    public String boardState(Player player) {
        StringBuilder output = new StringBuilder();
        List<Player> otherPlayers = getOtherPlayers(player);
        output.append("Discards:\n").append(pileManager.getDiscardPile())
                .append("\n")
                .append(Prompter.getLine())
                .append("\n")
                .append("Left:   ")
                .append(otherPlayers.get(2).getHandManager().toStringOpponentView())
                .append("\n")
                .append("Across: ")
                .append(otherPlayers.get(1).getHandManager().toStringOpponentView())
                .append("\n")
                .append("Right:  ")
                .append(otherPlayers.get(0).getHandManager().toStringOpponentView())
                .append("\n\n")
                .append("Remaining Tiles: ")
                .append(pileManager.getUnrevealedPile().getRemainingTileCount());
        return output.toString();
    }
}
