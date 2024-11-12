package game.core.turn.data;

/**
 * A collection of all possible events that would cause a turn to end.
 */
public enum TurnEnder {
    NONE(),
    DISCARD_TILE(),
    DRAW_FLOWER(),
    DARK_KONG(),
    BRIGHT_KONG(),
    END_GAME_WIN(),
    END_GAME_WIN_SELFDRAW(),
    END_GAME_DRAW(),
    ;
}
