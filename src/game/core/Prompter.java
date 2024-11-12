package game.core;

import game.board.tile.Tile;
import game.player.data.Seat;

import java.util.*;

/**
 * A class that handles outputs and prompts to the player.
 */
public class Prompter {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Creates a new prompter.
     */
    public Prompter() {}

    /**
     * Prints a line to the interface.
     * @param message the message to print.
     */
    public static void printLine(String message) {
        System.out.println(message);
    }

    /**
     * Prompts for a player's name.
     * @param instruction the prompt instruction.
     * @param helpMessage the help message.
     * @return the player name prompted from the user.
     */
    public static String promptName(String instruction, String helpMessage) {
        String name = null;
        while (name == null || name.length() < 2) {
            name = prompter(instruction).trim();
            if (name.isBlank()) {
                System.out.println("Name cannot be empty!");
            } else if (name.equalsIgnoreCase("h")
                    || name.equalsIgnoreCase("help")) {
                System.out.println(helpMessage);
            } else if (name.length() < 2 && !name.equalsIgnoreCase("q")) {
                System.out.println("Name must have at least 2 characters!");
            } else if (name.contains("|")) {
                System.out.println("Name cannot have the '|' character!");
                name = null;
            }
        }
        return name;
    }

    /**
     * Prompts for whether a player is a bot or not.
     * @param helpMessage the help message.
     * @return the response prompted from the user.
     */
    public static String promptBotStatus(String helpMessage) {
        Set<String> isBotValidCommands = Set.of("q", "y", "n", "yes", "no");
        return prompterCommand("Is this player a bot? (Y/N)", isBotValidCommands,
                false, helpMessage);
    }

    /**
     * Prompts for an integer.
     * @param instruction the prompt instruction.
     * @param validCommands a set of valid commands.
     * @param helpMessage the help message.
     * @return the response prompted from the user.
     */
    public static String promptInteger(String instruction, Set<String> validCommands, String helpMessage) {
        while (true) {
            String response = prompter(instruction);
            try {
                if (validCommands.contains(response)) {
                    return response;
                }
                Integer.parseInt(response);
                return response;
            } catch (NumberFormatException e) {
                System.out.println("Not a valid integer!");
            }
        }
    }

    /**
     * Prompts for a seat.
     * @param availableSeats a list of seats to choose from.
     * @return the seat prompted from the user.
     */
    public static Seat promptSeat(List<Seat> availableSeats) {
        while (true) {
            String response = prompter("Select a seat");
            for (Seat seat : availableSeats) {
                if (seat.getSeatNameEng().equalsIgnoreCase(response)) {
                    return seat;
                }
            }

            boolean seatExists = Arrays.stream(Seat.values())
                    .anyMatch(seat -> seat.getSeatNameEng().equals(response));
            if (seatExists) {
                System.out.println("Seat already taken!");
            } else {
                System.out.println("Invalid seat!");
            }
        }
    }

    /**
     * A generic prompter.
     * @param instruction the prompt instruction.
     * @return the response prompted from the user.
     * @ensures the response contains no trailing whitespaces.
     */
    public static String prompter(String instruction) {
        String response;
        System.out.print(instruction + ": ");
        response = scanner.nextLine().trim();
        return response;
    }

    /**
     * A generic prompter with a set number of commands.
     * @param instruction the prompt instruction.
     * @param validCommands a set of valid commands.
     * @param caseSensitive whether the response should be case-sensitive.
     * @param helpMessage the help message.
     * @return the response prompted from the user.
     */
    public static String prompterCommand(String instruction, Set<String> validCommands,
                                  boolean caseSensitive, String helpMessage) {
        String response;
        while (true) {
            response = prompter(instruction);
            if (!caseSensitive) {
                response = response.toLowerCase();
            }
            if (validCommands.contains(response)) {
                return response;
            } else if (response.equalsIgnoreCase("h")
                    || response.equalsIgnoreCase("help")) {
                System.out.println(helpMessage);
            } else {
                System.out.println("Invalid command!");
            }
        }
    }

    public static String handToDisplay(List<List<Tile>> hand) {
        if (hand == null) {
            return "";
        }
        StringBuilder output = new StringBuilder();
        for (List<Tile> group : hand) {
            for (Tile tile : group) {
                output.append(tile);
            }
            output.append(" ");
        }
        return output.substring(0, output.length() - 1);
    }

    public static String pointsToDisplay(List<MahjongPoint> points) {
        if (points == null) {
            return "";
        }
        StringBuilder output = new StringBuilder();
//        for (MahjongPoint point : points) {
//            output.append(point + "\n");
//        }
//        return output.substring(0, output.length() - 1);
        int scoreLength = 5;
        String headerFormat = " %-" + (scoreLength + 1) + "s| %s%n";
        output.append(String.format(headerFormat, "Score", "Item")).append(" ")
                .append("-".repeat(scoreLength + 1)).append("+-----------\n");
        String rowFormat = " %-" + (scoreLength + 1) + "d| %s%n";

        for (MahjongPoint point : points) {
            output.append(String.format(rowFormat, point.getPointScore(), point.getPointNameChi()));
        }
        return output.toString();
    }

    /**
     * Prints a line containing 50 '-' characters.
     */
    public static void printLine() {
        printLine(getLine());
    }

    /**
     * Creates a line containing a bunch of '-' characters.
     * @return the string output of the line.
     */
    public static String getLine() {
        return "---------------------------------------------------------------------------";
    }
}
