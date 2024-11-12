package game.core.turn;

import game.board.HandManager;
import game.core.InvalidKongException;
import game.core.Prompter;
import game.core.turn.data.TurnEnder;
import game.board.tile.Tile;
import game.board.tile.TileType;
import game.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A player turn.
 */
public class Turn {
    private final Player player;
    private final List<Player> otherPlayers;
    private final String boardState;
    private Tile drawnTile = null;
    private Tile discardTile = null;
    private final List<Tile> discardedTiles;

    /**
     * Starts a new turn with a specified player.
     * @param player the player in control of the turn.
     */
    public Turn(Player player, List<Player> otherPlayers, String boardState,
                List<Tile> discardedTiles) {
        this.player = player;
        this.otherPlayers = otherPlayers;
        this.boardState = boardState;
        this.discardedTiles = new ArrayList<>(discardedTiles);
    }

    /**
     * Retrieves the tile drawn during this turn.
     * @return the tile that was drawn.
     */
    public Tile getDrawnTile() {
        return drawnTile;
    }

    /**
     * Retrieves the tile discarded during this turn.
     * @return the tile that was discarded.
     */
    public Tile getDiscardTile() {
        return discardTile;
    }

    /**
     * Retrieves the player in control of the turn.
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retrieves the player's hand manager instance.
     * @return the hand manager instance.
     */
    public HandManager getHandManager() {
        return getPlayer().getHandManager();
    }

    /**
     * Checks if the tile is a flower, and adds it to the revealed hand if applicable.
     * @param tile the tile to be checked.
     * @return true iff the tile was a flower, false otherwise.
     */
    private boolean checkFlower(Tile tile) {
        if (tile.getTileType() == TileType.FLOWER_SEASON
                || tile.getTileType() == TileType.FLOWER_PLANT) {
            getHandManager().addFlower(tile);
            return true;
        }
        return false;
    }

    /**
     * Checks if a win condition is satisfied AND if the player accepts the win condition.
     * @return true iff a win condition is accepted, false otherwise.
     */
    private boolean checkWin() {
        if (getHandManager().checkWin()) {
            return getPlayer().decideWin(boardState);
        }
        return false;
    }

    /**
     * Checks if a kong can be created AND if the player accepts the kong.
     * @return the turn ending event if a kong was accepted, null otherwise.
     * @throws InvalidKongException if an invalid kong is attempted.
     */
    private TurnEnder checkKong() throws InvalidKongException {
        for (Tile t : getHandManager().getHand().getTiles()) {
            if (getHandManager().checkDarkKong(t)) {
                if (player.decideDarkKong(t, boardState)) {
                    Prompter.printLine();
                    Prompter.printLine(player.toStringWithSeat() + " performed Dark Kong: "
                            + "🀫🀫🀫🀫");
                    handleDarkKong(t);
                    for (Player otherPlayer : otherPlayers) {
                        otherPlayer.deductScore(5);
                        Prompter.printLine(otherPlayer.toStringWithSeat() + ": -5");
                    }
                    player.addScore(15);
                    Prompter.printLine(player.toStringWithSeat() + ": +15");
                    return TurnEnder.DARK_KONG;
                }
            } else if (getHandManager().checkBrightKongSelfDraw(t)) {
                if (player.decideBrightKong(t, boardState)) {
                    Prompter.printLine();
                    Prompter.printLine(player.toStringWithSeat() + " performed Bright Kong: "
                            + t + t + t + t);
                    player.getHandManager().discardTile(t);
                    handleBrightKong(t);
                    return TurnEnder.BRIGHT_KONG;
                }
            }
        }
        return null;
    }

    /**
     * Start the turn during a first draw (the first round).
     * @return the turn ending event.
     */
    public TurnEnder startTurnFirstDraw() throws InvalidKongException {
        // CHECK WIN
        if (checkWin()) {
            // there is about a 0.000001% chance of this code ever executing during a round :)
            return TurnEnder.END_GAME_WIN_SELFDRAW;
        }

        // CHECK KONG
        TurnEnder kongResult = checkKong();
        if (kongResult != null) {
            return kongResult;
        }

        // DISCARD TILE
        discardTile = player.pickDiscardTileNoDraw(boardState, discardedTiles);
        getHandManager().discardTile(discardTile);
        return TurnEnder.DISCARD_TILE;
    }

    /**
     * Start the turn when a tile is drawn.
     * @param tile the tile drawn from the unrevealed pile.
     * @return the turn ending event.
     * @throws InvalidKongException if an invalid kong is attempted.
     */
    public TurnEnder startTurnDrawTile(Tile tile) throws InvalidKongException {
        drawnTile = tile;

        // CHECK FLOWER
        if (checkFlower(tile)) {
            return TurnEnder.DRAW_FLOWER;
        }

        // ADD TILE TO HAND
        getHandManager().addToHand(tile);

        // CHECK WIN
        if (checkWin()) {
            return TurnEnder.END_GAME_WIN_SELFDRAW;
        }

        // CHECK KONG
        TurnEnder kongResult = checkKong();
        if (kongResult != null) {
            return kongResult;
        }

        // DISCARD TILE
        discardTile = player.pickDiscardTile(boardState, discardedTiles);
        getHandManager().discardTile(discardTile);
        return TurnEnder.DISCARD_TILE;
    }

    /**
     * Start the turn when a tile is taken from an opponent discard to form a group.
     * @param takenTile the tile taken from the opponent.
     * @param existingTiles the tiles in the hand used to form the group.
     * @return the turn ending event (this is always DISCARD_TILE).
     * @requires every tile in existingTiles is in the hand.
     * @requires the group is valid (i.e. it is a valid Pong or a Sheung).
     */
    public TurnEnder startTurnTakeTile(Tile takenTile, List<Tile> existingTiles) {
        // ADD GROUP TO REVEALED HAND
        getHandManager().addGroup(takenTile, existingTiles);

        // DISCARD TILE
        discardTile = player.pickDiscardTileNoDraw(boardState, discardedTiles);
        getHandManager().discardTile(discardTile);
        return TurnEnder.DISCARD_TILE;
    }

    /**
     * Start the turn when a Bright Kong is performed.
     * @param tile the tile taken from the opponent.
     * @return the turn ending event (this is always BRIGHT_KONG).
     * @throws InvalidKongException if the tile cannot be used to make a Bright Kong.
     */
    public TurnEnder startTurnBrightKongFromOpponent(Tile tile) throws InvalidKongException {
        handleBrightKongFromOpponent(tile);
        return TurnEnder.BRIGHT_KONG;
    }

    /**
     * Performs a Dark Kong with the specified tile.
     * @param tile the tile to perform a Dark Kong with.
     * @throws InvalidKongException if a Dark Kong cannot be performed with the specified tile.
     */
    public void handleDarkKong(Tile tile) throws InvalidKongException {
        getHandManager().darkKong(tile);
    }

    /**
     * Performs a Bright Kong with the specified tile.
     * @param tile the tile to perform a Bright Kong with.
     * @throws InvalidKongException if a Bright Kong cannot be performed with the specified tile.
     */
    public void handleBrightKong(Tile tile) throws InvalidKongException {
        getHandManager().brightKong(tile);
    }

    /**
     * Performs a Bright Kong with the specified tile, taken from an opponent discard.
     * @param tile the tile to perform a Bright Kong with.
     * @throws InvalidKongException if a Bright Kong cannot be performed with the specified tile.
     */
    public void handleBrightKongFromOpponent(Tile tile) throws InvalidKongException {
        getHandManager().brightKongFromOpponent(tile);
    }
}
