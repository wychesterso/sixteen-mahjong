package game.core;

import game.board.HandManager;
import game.board.tile.Tile;
import game.core.turn.data.TurnEnder;
import game.player.Bot;
import game.player.Player;
import game.player.RealPlayer;
import game.player.data.Seat;

import java.io.*;
import java.util.*;

/**
 * The main command interface for the game application.
 */
public class GameInterface {
    private final List<Player> playerList;

    /**
     * Creates a game interface instance.
     */
    public GameInterface() {
        this.playerList = new ArrayList<>();
    }

    /**
     * Creates a game interface instance with a list of players.
     */
    public GameInterface(List<Player> playerList) {
        this.playerList = playerList;
    }

    /**
     * Runs the interface for home menu.
     */
    public void run(boolean loadPlayers) throws IOException {
        if (loadPlayers) {
            loadPlayers();
        }
        boolean activeFlag = true;
        Prompter.printLine("\n\n\n🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙 港式十六張麻雀 🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙🀙");
        Prompter.printLine("🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫 Hong Kong-Style Taiwanese Mahjong 🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫🀫");
        String helpMessage = Prompter.getLine() + """
                    \n== HOME ==
                    Options:
                     - q: Quit app
                     - players: Manage players
                     - start: Start new game
                     - save: Save scores
                    """;
        while (activeFlag) {
            Prompter.printLine("\n" + helpMessage);
            Set<String> validCommands = Set.of("q", "players", "start", "save");
            switch (Prompter.prompterCommand("Home", validCommands,
                    false, helpMessage)) {
                case "q" -> activeFlag = false;
                case "players" -> runPlayers();
                case "start" -> startGame();
                case "save" -> runSavePlayers();
            }
        }
        savePlayers();
    }

    /**
     * Runs the interface for player management.
     */
    public void runPlayers() {
        boolean activeFlag = true;
        String helpMessage = Prompter.getLine() + """
                    \n== PLAYER MANAGEMENT ==
                    Options:
                     - q: Quit player management
                     - view: View player leaderboard
                     - create: Create new player
                     - edit: Edit existing player
                     - delete: Delete existing player
                    """;
        Prompter.printLine("\n" + helpMessage);
        while (activeFlag) {
            Set<String> validCommands = Set.of("q", "view", "create", "edit", "delete");
            switch (Prompter.prompterCommand("Players", validCommands,
                    false, helpMessage)) {
                case "q" -> activeFlag = false;
                case "view" -> runPlayerView();
                case "create" -> runPlayerCreate();
                case "edit" -> runPlayerEdit();
                case "delete" -> runPlayerDelete();
            }
        }
    }

    /**
     * Prints the list of players.
     */
    public void runPlayerView() {
        Prompter.printLine();
        if (playerList.isEmpty()) {
            Prompter.printLine("No players found!");
        } else {
            int maxNameLength = playerList.stream()
                    .mapToInt(player -> player.getName().length())
                    .max()
                    .orElse(4);

            String headerFormat = " %-" + (maxNameLength + 1) + "s| %s%n";
            System.out.printf(headerFormat, "Name", "Score");
            Prompter.printLine(" " + "-".repeat(maxNameLength + 1) + "+-------");

            String rowFormat = " %-" + (maxNameLength + 1) + "s| %d%n";

            List<Player> sortedPlayers = playerList.stream()
                    .sorted(Comparator.comparingInt(Player::getScore).reversed())
                    .toList();
            for (Player player : sortedPlayers) {
                System.out.printf(rowFormat, player.getName(), player.getScore());
            }
        }
        Prompter.printLine();
    }

    /**
     * Runs the interface for player create.
     */
    public void runPlayerCreate() {
        String helpMessageName = Prompter.getLine() + """
                    \n== PLAYER CREATOR ==
                    Options:
                     - q: Quit player creator
                     - <PLAYER_NAME>: Create new player with specified name
                    """;
        String helpMessageBot = Prompter.getLine() + """
                    \n== PLAYER CREATOR ==
                    Options:
                     - q: Quit player creator
                     - <Y/N>: Specify whether new player is a bot (Y/N)
                    """;
        String helpMessageScore = Prompter.getLine() + """
                    \n== PLAYER CREATOR ==
                    Options:
                     - q: Quit player creator
                     - <SCORE>: Specify the starting integer score of a player
                    """;
        Player newPlayer = null;
        String name = null;
        boolean nameFlag = true;
        while (nameFlag) {
            nameFlag = false;
            name = Prompter.promptName("Please enter player's name", helpMessageName);
            if (name.equals("q")) {
                Prompter.printLine();
                return;
            }
            for (Player player : playerList) {
                if (player.getName().equals(name)) {
                    Prompter.printLine("A player with this name already exists!");
                    nameFlag = true;
                }
            }
        }

        String scoreResponse = Prompter.promptInteger("Please enter player starting score",
                Set.of("q"), helpMessageScore);
        if (scoreResponse.equals("q")) {
            Prompter.printLine();
            return;
        }
        int startingScore = Integer.parseInt(scoreResponse);

        switch (Prompter.promptBotStatus(helpMessageBot)) {
            case "q" -> {
                Prompter.printLine();
                return;
            }
            case "y", "yes" -> newPlayer = new Bot(name, startingScore);
            case "n", "no" -> newPlayer = new RealPlayer(name, startingScore);
        }
        if (newPlayer != null) {
            playerList.add(newPlayer);
        }
        Prompter.printLine();
    }

    /**
     * Runs the interface for player edit.
     */
    public void runPlayerEdit() {
        Player activePlayer = null;
        String helpMessage = Prompter.getLine() + """
                    \n== PLAYER EDIT ==
                    Options:
                     - q: Quit player edit
                     - <PLAYER_NAME>: Edit specified player
                    """;
        String helpMessageName = Prompter.getLine() + """
                    \n== PLAYER EDIT ==
                    Options:
                     - q: Quit player edit
                     - <PLAYER_NAME>: Specify player name to change to
                    """;
        String helpMessageScore = Prompter.getLine() + """
                    \n== PLAYER EDIT ==
                    Options:
                     - q: Quit player edit
                     - <SCORE>: Specify player's new score
                    """;
        while (activePlayer == null) {
            String name = Prompter.promptName("Please enter player's name", helpMessage);
            if (name.equals("q")) {
                Prompter.printLine();
                return;
            }
            for (Player player : playerList) {
                if (name.equals(player.getName())) {
                    activePlayer = player;
                }
            }
        }

        // CHANGE NAME
        String newName = Prompter.promptName("Please enter " + activePlayer.getName()
                + "'s new name", helpMessageName);
        if (newName.equals("q")) {
            Prompter.printLine();
            return;
        }
        activePlayer.setName(newName);

        // SET SCORE
        String scoreResponse = Prompter.promptInteger("Please enter "
                        + activePlayer.getName() + "'s new score",
                Set.of("q"), helpMessageScore);
        if (scoreResponse.equals("q")) {
            Prompter.printLine();
            return;
        }
        int startingScore = Integer.parseInt(scoreResponse);
        activePlayer.setScore(startingScore);
        Prompter.printLine();
    }

    /**
     * Runs the interface for player delete.
     */
    public void runPlayerDelete() {
        String helpMessage = Prompter.getLine() + """
                    \n== PLAYER DELETE ==
                    Options:
                     - q: Quit player delete
                     - <PLAYER_DELETE>: Delete specified player
                    """;
        String helpMessageConfirm = Prompter.getLine() + """
                    \n== PLAYER DELETE ==
                    Options:
                     - q: Quit player delete
                     - <Y/N>: Delete specified player (Y/N)
                    """;
        while (true) {
            String name = Prompter.promptName("Please enter player's name", helpMessage);
            if (name.equals("q")) {
                Prompter.printLine();
                return;
            }
            boolean existsPlayer = false;
            for (Player player : playerList) {
                if (name.equals(player.getName())) {
                    existsPlayer = true;
                    String confirm = Prompter.prompterCommand(
                            "Are you sure you want to delete " + player.getName()
                                    + "? (Y/N)", Set.of("y", "yes", "n", "no"),
                            false, helpMessageConfirm);
                    if (confirm.equals("y") || confirm.equals("yes")) {
                        playerList.remove(player);
                        Prompter.printLine();
                        return;
                    }
                }
            }
            if (existsPlayer) {
                Prompter.printLine("Operation cancelled!\n");
            } else {
                Prompter.printLine("Player not found!\n");
            }
        }
    }

    /**
     * Starts a new game.
     */
    public void startGame() {
        if (playerList.size() < 4) {
            Prompter.printLine("Not enough players to start a game!");
            return;
        }

        String helpMessage = Prompter.getLine() + """
                    \n== START GAME ==
                    Options:
                     - exit: Cancel game
                     - <PLAYER>: Add a player to the game
                    """;
        String helpMessageSwitchSeat = Prompter.getLine() + """
                    \n== START GAME ==
                    Options:
                     - exit: Cancel game
                     - <Y/N>: Switch the player's seat
                    """;

        // SELECT PLAYERS
        List<Player> activePlayers = new ArrayList<>();
        List<Seat> availableSeats = new ArrayList<>(List.of(Seat.values()));
        for (int i = 1; i <= 4; i++) {
            boolean activeFlag = true;
            while (activeFlag) {
                String name = Prompter.promptName("Player " + i + ": Enter name", helpMessage);
                if (name.equals("exit")) {
                    return;
                }
                boolean existsPlayer = false;
                for (Player player : playerList) {
                    if (name.equals(player.getName()) && !activePlayers.contains(player)) {
                        Seat seat = player.getSeat();
                        if (seat == null || !availableSeats.contains(seat)) {
                            seat = Prompter.promptSeat(availableSeats);
                        } else {
                            switch (Prompter.prompterCommand("Current seat: " + seat
                                            + ". Do you want to switch seats? (Y/N)",
                                    Set.of("exit", "y", "yes", "n", "no"), false,
                                    helpMessageSwitchSeat)) {
                                case "exit" -> {
                                    return;
                                }
                                case "y", "yes" -> seat = Prompter.promptSeat(availableSeats);
                            }
                        }
                        player.setSeat(seat);
                        availableSeats.remove(seat);
                        activePlayers.add(player);
                        activeFlag = false;
                    } else if (name.equals(player.getName())) {
                        existsPlayer = true;
                    }
                }
                if (activeFlag) {
                    if (existsPlayer) {
                        Prompter.printLine("Player already added!");
                    } else {
                        Prompter.printLine("Player not found!");
                    }
                }
            }
        }

        // START GAME
        try {
            // record starting scores
            Map<Player, Integer> originalScores = new HashMap<>();
            for (Player player : activePlayers) {
                originalScores.put(player, player.getScore());
            }

            // initialize game
            TurnManager turnManager = new TurnManager(activePlayers);
            List<Seat> seats = List.of(Seat.values());
            Seat gameSeat = seats.getFirst();
            Seat roundSeat = seats.getFirst();
            int lumZhongNum = 0;
            Map<Player, Map<Player, Integer>> pullZhong = new HashMap<>();
            for (Player winner : activePlayers) {
                pullZhong.put(winner, new HashMap<>());
            }

            // continuously run rounds until terminated
            boolean gameFlag = true;
            while (gameFlag) {
                // display round banner
                Prompter.printLine();
                Prompter.printLine("\n\n\n");
                Prompter.printLine(gameSeat.getSeatNameEng().toUpperCase() + " GAME "
                        + roundSeat.getSeatNameEng().toUpperCase() + " ROUND");
                Prompter.printLine(gameSeat.getSeatNameChi() + "圈" + roundSeat.getSeatNameChi() + "局");
                Prompter.printLine("\n\n\n");

                // run round
                TurnEnder turnEnder = turnManager.startRound(roundSeat);

                // handle wins
                List<Player> winners = new ArrayList<>();
                String lastEvent = turnManager.getLastEvent();
                int discardCount = turnManager.getDiscardCount();
                if (turnEnder == TurnEnder.END_GAME_WIN) {
                    Player loser = turnManager.getCurrentPlayer();
                    winners.addAll(turnManager.getWinners());
                    for (Player winner : winners) {
                        // CALCULATE BEST HAND AND SCORE
                        HandManager winnerHandManager = winner.getHandManager();
                        List<List<Tile>> highestHand = new ArrayList<>();
                        List<MahjongPoint> highestPoints = new ArrayList<>();
                        int highestScore = calculateScore(highestHand, highestPoints, gameSeat,
                                roundSeat, turnManager, winnerHandManager, winner.getSeat(),
                                loser.getSeat(), winners.size() > 1, lumZhongNum,
                                lastEvent, discardCount);

                        // DISPLAY RESULTS
                        Prompter.printLine(winner.toStringWithSeat() + " won off " + loser.toStringWithSeat() + "!");
                        Prompter.printLine(winnerHandManager.toStringSepLastDrawn());
                        Prompter.printLine(Prompter.pointsToDisplay(highestPoints));
                        Prompter.printLine("Score: " + highestScore);
                        Prompter.printLine();
                        revertPointScores();

                        // HANDLE PULLS
                        for (Map.Entry<Player, Map<Player, Integer>> entry : pullZhong.entrySet()) {
                            Player leader = entry.getKey();
                            Map<Player, Integer> leaderPulls = entry.getValue();
                            if (leader == winner) {
                                try {
                                    int trailingScore = leaderPulls.get(loser);
                                    if (trailingScore == 0) {
                                        throw new NullPointerException();
                                    }
                                    leaderPulls.put(loser, highestScore + (trailingScore * 2));
                                    highestScore += trailingScore;
                                } catch (NullPointerException e) {
                                    leaderPulls.put(loser, highestScore);
                                }
                            } else {
                                for (Map.Entry<Player, Integer> losing : leaderPulls.entrySet()) {
                                    Player trailer = losing.getKey();
                                    int trailingScore = losing.getValue();
                                    if (winner == trailer && loser == leader) {
                                        trailingScore = trailingScore / 2;
                                    }
                                    trailer.deductScore(trailingScore);
                                    leader.addScore(trailingScore);
                                    displayMoneyCollect(leader, trailer, trailingScore);
                                    Prompter.printLine();
                                }
                                leaderPulls.clear();
                            }
                        }
                        displayPullZhong(pullZhong);
                    }
                } else if (turnEnder == TurnEnder.END_GAME_WIN_SELFDRAW) {
                    Player winner = turnManager.getCurrentPlayer();
                    winners.add(winner);

                    // CALCULATE BEST HAND AND SCORE
                    HandManager winnerHandManager = winner.getHandManager();
                    List<List<Tile>> highestHand = new ArrayList<>();
                    List<MahjongPoint> highestPoints = new ArrayList<>();
                    int highestScore = calculateScore(highestHand, highestPoints, gameSeat,
                            roundSeat, turnManager, winnerHandManager, winner.getSeat(),
                            winner.getSeat(), false, lumZhongNum,
                            lastEvent, discardCount);

                    // DISPLAY RESULTS
                    Prompter.printLine(winner.toStringWithSeat() + " won off a self-draw!");
                    Prompter.printLine(winnerHandManager.toStringSepLastDrawn());
                    Prompter.printLine(Prompter.pointsToDisplay(highestPoints));
                    Prompter.printLine("Score: " + highestScore);
                    Prompter.printLine();
                    revertPointScores();

                    // ADJUST SCORES BASED ON SEAT
                    int zhongScore = highestScore + 2 * (2 * lumZhongNum + 1);
                    Map<Player, Integer> lostScores = new HashMap<>();
                    for (Player loser : turnManager.getOtherPlayers()) {
                        if (roundSeat == loser.getSeat()) {
                            lostScores.put(loser, zhongScore);
                        } else {
                            lostScores.put(loser, highestScore);
                        }
                    }

                    // HANDLE PULLS
                    for (Map.Entry<Player, Map<Player, Integer>> entry : pullZhong.entrySet()) {
                        Player leader = entry.getKey();
                        Map<Player, Integer> leaderPulls = entry.getValue();
                        if (leader == winner) {
                            for (Player loser : turnManager.getOtherPlayers()) {
                                int lostScore = lostScores.get(loser);
                                if (leaderPulls.containsKey(loser)) {
                                    int trailingScore = leaderPulls.get(loser);
                                    leaderPulls.put(loser, lostScore + (trailingScore * 2));
                                } else {
                                    leaderPulls.put(loser, lostScore);
                                }
                            }
                        } else {
                            for (Map.Entry<Player, Integer> losing : leaderPulls.entrySet()) {
                                Player trailer = losing.getKey();
                                int trailingScore = losing.getValue();
                                if (winner == trailer) {
                                    trailingScore = trailingScore / 2;
                                }
                                trailer.deductScore(trailingScore);
                                leader.addScore(trailingScore);
                                displayMoneyCollect(leader, trailer, trailingScore);
                                Prompter.printLine();
                            }
                            leaderPulls.clear();
                        }
                    }
                    displayPullZhong(pullZhong);
                } else {
                    Prompter.printLine("Game ended in a tie!");
                    Prompter.printLine();
                }
                for (Player player : playerList) {
                    player.clearHand();
                }

                String response = Prompter.prompter("Start new round? (Y/N)").toLowerCase();
                if (response.equals("y") || response.equals("yes")) {
                    // CHANGES THE ROUND SEAT
                    boolean lumZhongFlag = false;
                    if (turnEnder == TurnEnder.END_GAME_DRAW) {
                        lumZhongFlag = true;
                    } else {
                        for (Player winner : winners) {
                            if (roundSeat == winner.getSeat()) {
                                lumZhongFlag = true;
                                break;
                            }
                        }
                    }
                    if (!lumZhongFlag) {
                        int currentIndex = seats.indexOf(roundSeat);
                        int nextIndex = (currentIndex + 1) % seats.size();
                        roundSeat = seats.get(nextIndex);
                        lumZhongNum = 0;
                    } else {
                        lumZhongNum += 1;
                    }

                    if (roundSeat == Seat.EAST && !lumZhongFlag) {
                        // CHANGES THE GAME SEAT
                        int currentIndexGame = seats.indexOf(gameSeat);
                        int nextIndexGame = (currentIndexGame + 1) % seats.size();
                        gameSeat = seats.get(nextIndexGame);
                    }
                } else {
                    gameFlag = false;
                }
            }
            for (Map.Entry<Player, Map<Player, Integer>> entry : pullZhong.entrySet()) {
                Player leader = entry.getKey();
                for (Map.Entry<Player, Integer> losing : entry.getValue().entrySet()) {
                    int trailingScore = losing.getValue();
                    losing.getKey().deductScore(trailingScore);
                    leader.addScore(trailingScore);
                }
            }

            // display gains and losses
            Prompter.printLine();
            Prompter.printLine("\nNet Earnings:");
            for (Player player : activePlayers) {
                int originalScore = originalScores.get(player);
                int currentScore = player.getScore();
                Prompter.printLine("    " + player + ": " + (currentScore - originalScore));
            }
            Prompter.printLine();

        } catch (InvalidKongException e) {
            Prompter.printLine("Game crashed! Error: " + e);
        } catch (EmptyPileException e) {
            Prompter.printLine("Game crashed! Pile was empty during initial draw!");
        }
    }

    private int calculateScore(List<List<Tile>> highestHand, List<MahjongPoint> highestPoints,
                               Seat gameSeat, Seat roundSeat, TurnManager turnManager,
                               HandManager winnerHandManager, Seat winnerSeat, Seat loserSeat,
                               boolean multipleWinners, int lumZhongNum,
                               String lastEvent, int discardCount) {
        Tile wonOffTile = winnerHandManager.getHand().getLastDrawnTile();

        int highestScore = 0;
        for (List<List<Tile>> hand : ScoreCalculator.getValidHands(winnerHandManager.getHand().getTiles())) {
            List<MahjongPoint> points = ScoreCalculator.getPoints(gameSeat, roundSeat,
                    winnerSeat, loserSeat,
                    new ArrayList<>(hand), wonOffTile, winnerHandManager,
                    turnManager.getPileManager().getUnrevealedPile().getRemainingTileCount(),
                    turnManager.getPileManager().getDiscardPile().getDiscardedTiles(),
                    multipleWinners, lumZhongNum, lastEvent, discardCount);
            int score = ScoreCalculator.getScore(points);
            if (score > highestScore) {
                highestScore = score;
                highestHand.clear();
                highestHand.addAll(hand);
                highestPoints.clear();
                highestPoints.addAll(points);
            }
        }
        Collections.sort(highestPoints);
        return highestScore;
    }

    /**
     * After calculating scores for a game, reverts enums to their base scores.
     */
    private void revertPointScores() {
        List<MahjongPoint> existingWindPoints = List.of(MahjongPoint.EAST, MahjongPoint.SOUTH,
                MahjongPoint.WEST, MahjongPoint.NORTH);
        for (MahjongPoint point : existingWindPoints) {
            point.setPointScore(1);
        }
        MahjongPoint.ZHONG.setPointScore(1);
    }

    private void displayMoneyCollect(Player leader, Player trailer, int trailingScore) {
        Prompter.printLine("收錢! Money collected!");
        Prompter.printLine("    " + leader.toStringWithSeat() + ": +" + trailingScore);
        Prompter.printLine("    " + trailer.toStringWithSeat() + ": -" + trailingScore);
    }

    private void displayPullZhong(Map<Player, Map<Player, Integer>> pullZhong) {
        for (Map.Entry<Player, Map<Player, Integer>> pull : pullZhong.entrySet()) {
            if (!pull.getValue().isEmpty()) {
                Player leader = pull.getKey();
                Prompter.printLine("拉莊! The following players are being pulled by "
                        + leader.toStringWithSeat() + ":");
                for (Map.Entry<Player, Integer> entry : pull.getValue().entrySet()) {
                    Player trailer = entry.getKey();
                    Prompter.printLine("    " + trailer.toStringWithSeat() + ": " + entry.getValue());
                }
                Prompter.printLine();
            }
        }
    }

    /**
     * Creates a string representation of the player list for saving to file.
     * @param playerList the list of players.
     * @return the string representation.
     */
    private String playerListToStringSave(List<Player> playerList) {
        StringBuilder output = new StringBuilder();
        output.append(playerList.size());
        output.append("\n");
        for (Player player : playerList) {
            output.append(player.toStringSave());
            output.append("\n");
        }
        return output.substring(0, output.length() - 1);
    }

    /**
     * Runs the program to save players to file.
     * @throws IOException if an I/O Exception occurs.
     */
    private void runSavePlayers() throws IOException {
        savePlayers();
        Prompter.printLine("Scores saved!");
    }

    /**
     * Saves players to the file "players.txt".
     * @throws IOException if an I/O Exception occurs.
     */
    private void savePlayers() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("players.txt"));
        writer.write(playerListToStringSave(playerList));
        writer.close();
    }

    /**
     * Loads the list of players from the file "players.txt".
     * @throws IOException if an I/O Exception occurs.
     */
    private void loadPlayers() throws IOException {
        playerList.clear();
        BufferedReader reader = new BufferedReader(new FileReader("players.txt"));
        String playerCount = reader.readLine();
        try {
            int numPlayers = Integer.parseInt(playerCount);
            for (int i = 0; i < numPlayers; i++) {
                String playerLine = reader.readLine();
                String[] lineParts = playerLine.split("\\|");
                if (lineParts.length != 4) {
                    throw new IOException("Player is not of correct format!");
                }
                String botStatus = lineParts[0];
                String name = lineParts[1];
                String seat = lineParts[2];
                String scoreString = lineParts[3];
                int score = Integer.parseInt(scoreString);
                Player newPlayer;
                if (botStatus.equalsIgnoreCase("bot")) {
                    newPlayer = new Bot(name, score);
                } else {
                    newPlayer = new RealPlayer(name, score);
                }
                for (Seat existingSeat : Seat.values()) {
                    if (existingSeat.getSeatNameEng().equals(seat)) {
                        newPlayer.setSeat(existingSeat);
                        break;
                    }
                }
                playerList.add(newPlayer);
            }
        } catch (NumberFormatException e) {
            throw new IOException("File is not of correct format!");
        }
    }
}

