package game.player;

import game.board.HandManager;
import game.board.tile.Tile;
import game.player.data.Seat;

import java.util.List;

/**
 * A player participating in the game.
 */
public abstract class Player {
    private String name;
    private Seat seat = null;
    private int score;
    private final HandManager handManager = new HandManager();

    /**
     * Creates a player with a default starting score of 1000.
     * @param name the name of the player.
     */
    protected Player(String name) {
        this.name = name;
        this.score = 1000;
    }

    /**
     * Creates a player with a custom starting score.
     * @param name the name of the player.
     * @param score the starting score.
     */
    protected Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Retrieves the hand manager instance belonging to this player.
     * @return the hand manager instance.
     */
    public HandManager getHandManager() {
        return handManager;
    }

    /**
     * Resets the player's hand for a new game.
     */
    public void clearHand() {
        getHandManager().clearHand();
    }

    /**
     * Retrieves the name of the player.
     * @return the name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the player.
     * @param name the new name of the player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the current seat of the player.
     * @return the seat of the player.
     */
    public Seat getSeat() {
        return seat;
    }

    /**
     * Sets the seat of the player.
     * @param seat the seat of the player.
     */
    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    /**
     * Gets the current score of the player.
     * @return the score of the player.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of the player.
     * @param score the new score of the player.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Add an amount to the player's score.
     * @param amount the amount to add.
     */
    public void addScore(int amount) {
        score += amount;
    }

    /**
     * Remove an amount from the player's score.
     * @param amount the amount to remove.
     */
    public void deductScore(int amount) {
        score -= amount;
    }

    /**
     * Makes the player choose whether to accept a self-draw win condition.
     * @param boardState the current board state.
     * @return true iff the win condition is accepted, false otherwise.
     */
    public abstract boolean decideWin(String boardState);

    /**
     * Makes the player choose whether to accept a win condition when taking a tile from opponent.
     * @param tile the tile to win on.
     * @param boardState the current board state.
     * @return true iff the win condition is accepted, false otherwise.
     */
    public abstract boolean decideWin(Tile tile, String boardState);

    /**
     * Makes the player choose whether to accept a Sheung from opponent.
     * @param tile the tile to perform a Sheung on.
     * @param boardState the current board state.
     * @return true iff the Sheung is accepted, false otherwise.
     */
    public abstract boolean decideSheung(Tile tile, String boardState);

    /**
     * Makes the player choose whether to accept a Pong from opponent.
     * @param tile the tile to perform a Pong on.
     * @param boardState the current board state.
     * @return true iff the Pong is accepted, false otherwise.
     */
    public abstract boolean decidePong(Tile tile, String boardState);

    /**
     * Makes the player choose whether to accept a Dark Kong.
     * @param tile the tile to perform a Dark Kong on.
     * @param boardState the current board state.
     * @return true iff the Dark Kong is accepted, false otherwise.
     */
    public abstract boolean decideDarkKong(Tile tile, String boardState);

    /**
     * Makes the player choose whether to accept a Bright Kong.
     * @param tile the tile to perform a Bright Kong on.
     * @param boardState the current board state.
     * @return true iff the Bright Kong is accepted, false otherwise.
     */
    public abstract boolean decideBrightKong(Tile tile, String boardState);

    /**
     * Makes the player choose whether to accept a Bright Kong from a tile discarded by the
     * opponent.
     * @param tile the tile to perform a Bright Kong on.
     * @param boardState the current board state.
     * @return true iff the Bright Kong is accepted, false otherwise.
     */
    public abstract boolean decideBrightKongNoDraw(Tile tile, String boardState);

    /**
     * Makes the player choose which Sheung combo to accept.
     * @param validSheungs a list of valid Sheung combos.
     * @return the combo that is accepted.
     */
    public abstract List<Tile> pickSheungCombo(List<List<Tile>> validSheungs);

    /**
     * Makes the player choose a tile to discard after a draw.
     * @param boardState the current board state.
     * @return the tile that is discarded.
     */
    public abstract Tile pickDiscardTile(String boardState, List<Tile> discardedTiles);

    /**
     * Makes the player choose a tile to discard, when no tile has been drawn during the turn.
     * @param boardState the current board state.
     * @return the tile that is discarded.
     */
    public abstract Tile pickDiscardTileNoDraw(String boardState, List<Tile> discardedTiles);

    /**
     * Returns a string representation of the player.
     * @return the string representation.
     */
    public String toString() {
        return getName();
    }

    /**
     * Returns a string representation of the player with their seat.
     * @return the string representation.
     */
    public String toStringWithSeat() {
        return getSeat() + " - " + getName();
    }

    /**
     * Returns a string representation of the player that can be used to save their details.
     * @return the string representation.
     */
    public String toStringSave() {
        String seat;
        if (getSeat() != null) {
            seat = getSeat().getSeatNameEng();
        } else {
            seat = "null";
        }
        return getClass().getSimpleName() + "|" + getName() + "|"
                + seat + "|" + getScore();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player player) {
            return getName().equals(player.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getName().hashCode();
        return result;
    }
}
