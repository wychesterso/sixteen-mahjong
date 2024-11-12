package game.player;

import game.board.tile.Tile;
import game.core.Prompter;

import java.util.List;
import java.util.Scanner;

public class RealPlayer extends Player {
    private final Scanner scanner = new Scanner(System.in);

    public RealPlayer(String name) {
        super(name);
    }

    public RealPlayer(String name, int score) {
        super(name, score);
    }

    @Override
    public boolean decideWin(String boardState) {
        printBoardState(boardState);
        Prompter.printLine(getHandManager().toStringSepLastDrawn() + "\n");
        Prompter.printLine(this.toStringWithSeat() + ", win condition satisfied! Eat now? (Y/N) ");
        String chooseWin = scanner.nextLine();
        return (chooseWin.equalsIgnoreCase("Y") || chooseWin.equalsIgnoreCase("Yes"));
    }

    @Override
    public boolean decideWin(Tile tile, String boardState) {
        printBoardState(boardState);
        Prompter.printLine(getHandManager().toStringWithChoice(tile) + "\n");
        Prompter.printLine(this.toStringWithSeat() + ", win condition satisfied! Eat now? (Y/N) ");
        String chooseWin = scanner.nextLine();
        return (chooseWin.equalsIgnoreCase("Y") || chooseWin.equalsIgnoreCase("Yes"));
    }

    @Override
    public boolean decideSheung(Tile tile, String boardState) {
        printBoardState(boardState);
        Prompter.printLine(getHandManager() + "\n");
        Prompter.printLine(this.toStringWithSeat() + ", possible Sheung detected: " + tile + ". Sheung now? (Y/N) ");
        String chooseSheung = scanner.nextLine();
        return (chooseSheung.equalsIgnoreCase("Y") || chooseSheung.equalsIgnoreCase("Yes"));
    }

    @Override
    public boolean decidePong(Tile tile, String boardState) {
        printBoardState(boardState);
        Prompter.printLine(getHandManager() + "\n");
        Prompter.printLine(this.toStringWithSeat() + ", possible Pong detected: " + tile + ". Pong now? (Y/N) ");
        String choosePong = scanner.nextLine();
        return (choosePong.equalsIgnoreCase("Y") || choosePong.equalsIgnoreCase("Yes"));
    }

    @Override
    public boolean decideDarkKong(Tile tile, String boardState) {
        printBoardState(boardState);
        Prompter.printLine(getHandManager().toStringSepLastDrawn() + "\n");
        Prompter.printLine(this.toStringWithSeat() + ", possible Dark Kong detected: " + tile + ". Kong now? (Y/N) ");
        String chooseDarkKong = scanner.nextLine();
        return (chooseDarkKong.equalsIgnoreCase("Y") || chooseDarkKong.equalsIgnoreCase("Yes"));
    }

    @Override
    public boolean decideBrightKong(Tile tile, String boardState) {
        printBoardState(boardState);
        Prompter.printLine(getHandManager().toStringSepLastDrawn() + "\n");
        Prompter.printLine(this.toStringWithSeat() + ", possible Bright Kong detected: " + tile + ". Kong now? (Y/N) ");
        String chooseBrightKong = scanner.nextLine();
        return (chooseBrightKong.equalsIgnoreCase("Y") || chooseBrightKong.equalsIgnoreCase("Yes"));
    }

    @Override
    public boolean decideBrightKongNoDraw(Tile tile, String boardState) {
        printBoardState(boardState);
        Prompter.printLine(getHandManager() + "\n");
        Prompter.printLine(this.toStringWithSeat() + ", possible Bright Kong detected: " + tile + ". Kong now? (Y/N) ");
        String chooseBrightKong = scanner.nextLine();
        return (chooseBrightKong.equalsIgnoreCase("Y") || chooseBrightKong.equalsIgnoreCase("Yes"));
    }

    @Override
    public List<Tile> pickSheungCombo(List<List<Tile>> validSheungs) {
        for (int i = 1; i <= validSheungs.size(); i++) {
            StringBuilder sheung = new StringBuilder();
            for (Tile tile : validSheungs.get(i - 1)) {
                sheung.append(tile);
            }
            Prompter.printLine("Combo " + i + ": " + sheung.toString());
        }
        while (true) {
            Prompter.printLine(this.toStringWithSeat() + ", choose a combo: ");
            String chooseCombo = scanner.nextLine();
            try {
                int combo = Integer.parseInt(chooseCombo);
                return validSheungs.get(combo - 1);
            } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                Prompter.printLine("Invalid combo!");
            }
        }
    }

    @Override
    public Tile pickDiscardTile(String boardState, List<Tile> discardedTiles) {
        printBoardState(boardState);
        while (true) {
            Prompter.printLine(getHandManager().toStringSepLastDrawn() + "\n");
            Prompter.printLine(this.toStringWithSeat() + ", Discard a tile: ");
            String chooseDiscard = scanner.nextLine();

            for (Tile tile : getHandManager().getHand().getTiles()) {
                for (String name : tile.getTileNames()) {
                    if (name.equalsIgnoreCase(chooseDiscard)) {
                        return tile;
                    }
                }
            }
            Prompter.printLine("Not a valid tile to discard!");
            Prompter.printLine();
        }
    }

    @Override
    public Tile pickDiscardTileNoDraw(String boardState, List<Tile> discardedTiles) {
        printBoardState(boardState);
        while (true) {
            Prompter.printLine(getHandManager() + "\n");
            Prompter.printLine(this.toStringWithSeat() + ", Discard a tile: ");
            String chooseDiscard = scanner.nextLine();

            for (Tile tile : getHandManager().getHand().getTiles()) {
                for (String name : tile.getTileNames()) {
                    if (name.equalsIgnoreCase(chooseDiscard)) {
                        return tile;
                    }
                }
            }
            Prompter.printLine("Not a valid tile to discard!");
            Prompter.printLine();
        }
    }

    /**
     * Prints the current board state from the perspective of the real player.
     * @param boardState the current board state.
     */
    private void printBoardState(String boardState) {
        Prompter.printLine();
        Prompter.printLine(boardState);
        Prompter.printLine();
    }
}
