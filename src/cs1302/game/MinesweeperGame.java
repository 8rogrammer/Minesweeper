package cs1302.game;

import java.util.*;
import java.io.*;
import java.lang.*;

/**
 * Minesweeper game class which represents a game.
 */
public class MinesweeperGame {
    private int rounds = 0;
    private int rows;
    private int cols;
    private String[][] minesweeperArray;
    private String[][] noFogArray;
    private boolean[][] withMinesArray;
    private boolean[][] revealedArray;
    private final Scanner stdIn;
    private int numMines = 0;

    /**
     * Creates object instance of the {@link MinesweeperGame} class to read content
     * in the seedfile.
     *
     * @param stdIn    is the input of the user when trying to play the game.
     * @param seedPath is the seedfile that the user tries to test.
     */
    public MinesweeperGame(Scanner stdIn, String seedPath) {
        this.stdIn = stdIn;
        try {
            File configFile = new File(seedPath);
            Scanner configScanner = new Scanner(configFile);
            initializeGameBoardSize(configScanner);
            initializeArray();
            initializeArrayMines(configScanner);
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println("Seed File Not Found Error: " + fnfe.getMessage());
            System.exit(2);
        } catch (Exception ex) {
            System.err.println();
            System.err.println("Error occured: " + ex.getMessage());
            System.exit(2);
        }

    }

    /**
     * Method creates the board of the game using seedfile.
     *
     * @param configScanner reads the seedfile.
     */
    public void initializeGameBoardSize(Scanner configScanner) {
        if (configScanner.hasNextLine()) {
            String line = configScanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            if (lineScanner.hasNextInt()) {
                this.rows = lineScanner.nextInt();
            } else {
                this.rows = -1;
            }
            if (lineScanner.hasNextInt()) {
                this.cols = lineScanner.nextInt();
            } else {
                this.cols = -1;
            }
            if (lineScanner.hasNextInt()) {
                this.numMines = lineScanner.nextInt();
            } else {
                this.numMines = -1;
            }
            if (this.rows < 0 || this.cols < 0) {
                printSeedFileMalformedError();
            }
        }
        if (this.numMines == -1 && configScanner.hasNextLine()) {
            String line = configScanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            if (lineScanner.hasNextInt()) {
                this.numMines = lineScanner.nextInt();
            } else {
                this.numMines = -1;
            }
        }
        if (numMines < 1 || numMines > ((rows * cols) - 1)) {
            printSeedFileMalformedError();
        }
        if (rows < 5 || rows > 10 || cols < 5 || cols > 10) {
            System.out.println(rows + " " + cols);
            printSeedFileMalformedError();
        }
    }

    /**
     * Intializes the array that contains the mines in the game.
     *
     * @param configScanner accepts the scanner as an input and creates size of
     *                      array with mines.
     */
    public void initializeArrayMines(Scanner configScanner) {
        while (configScanner.hasNextLine()) {
            String line = configScanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            int row;
            int col;
            if (lineScanner.hasNextInt()) {
                row = lineScanner.nextInt();
            } else {
                row = -1;
            }
            if (lineScanner.hasNextInt()) {
                col = lineScanner.nextInt();
            } else {
                col = -1;
            }
            if (row < 0 || col < 0) {
                printSeedFileMalformedError();
            }
            withMinesArray[row][col] = true;
        }
    }

    /**
     * Initializes all arrays that are used in the game.
     */
    public void initializeArray() {
        this.minesweeperArray = new String[rows][cols];
        this.withMinesArray = new boolean[rows][cols];
        this.noFogArray = new String[rows][cols];
        this.revealedArray = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                minesweeperArray[i][j] = "   ";
                withMinesArray[i][j] = false;
                revealedArray[i][j] = false;
            }
        }
    }

    /**
     * Prints error message if the user has a seed file malformed error and then
     * exits using System.exit(3).
     */
    public void printSeedFileMalformedError() {
        System.err.println(
                "Seed File Malformed Error: Cannot create a mine" +
                "field with that many rows and/or columns!");
        System.exit(3);
    }

    /**
     * Prints the win statement with the score of the player.
     */
    public void printWin() {
        System.out.println("\n" +
            " ░░░░░░░░░▄░░░░░░░░░░░░░░▄░░░░ \"So Doge\"\n" +
            " ░░░░░░░░▌▒█░░░░░░░░░░░▄▀▒▌░░░\n" +
            " ░░░░░░░░▌▒▒█░░░░░░░░▄▀▒▒▒▐░░░ \"Such Score\"\n" +
            " ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░░\n" +
            " ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐░░░ \"Much Minesweeping\"\n" +
            " ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░░\n"
                + " ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒▌░░ \"Wow\"\n" + " ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░░\n"
                + " ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄▌░\n" + " ░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒▌░\n"
                + " ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒▐░\n" + " ▐▒▒▐▀▐▀▒░▄▄▒▄▒▒▒▒▒▒░▒░▒░▒▒▒▒▌\n"
                + " ▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒▒▒░▒░▒░▒▒▐░\n" + " ░▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒░▒░▒░▒░▒▒▒▌░\n"
                + " ░▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▒▄▒▒▐░░\n" + " ░░▀▄▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▄▒▒▒▒▌░░\n"
                + " ░░░░▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀░░░ CONGRATULATIONS!\n" +
            " ░░░░░░▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀░░░░░ YOU HAVE WON!\n"
                + " ░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▀▀░░░░░░░░ SCORE: " + rows * cols * 100.0 / rounds);
        System.exit(0);
    }

    /**
     * Prints the loss statement if the user triggers the mine.
     */
    public void printLoss() {
        System.out.println("\n Oh no... You revealed a mine!\n" +
            "  __ _  __ _ _ __ ___   ___    _____   _____ _ __\n"
            + " / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|\n"
            + "| (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |\n"
            + " \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|\n" + " |___/\n");
        System.exit(0);
    }

    /**
     * Prints the welcome statement at the beginning of the game.
     */
    public void welcome() {
        System.out.println("        _\n" +
                "  /\\/\\ (F)_ __   ___  _____      _____  ___ _ __   ___ _ __\n"
                + " /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|\n"
                + "/ /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |\n"
                + "\\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|\n"
                + "                             ALPHA EDITION |_| v2021.fa\n");
    }

    /**
     * Helper method that prints the contents for the help command. Increments the
     * number of rounds.
     */
    public void help() {
        System.out.println("\nCommands Available...\n" +
                " - Reveal: r/reveal row col\n" +
                " -   Mark: m/mark   row col\n"
                        +
                " -  Guess: g/guess  row col\n" + " -   Help: h/help\n" + " -   Quit: q/quit");
        rounds++;
    }

    /**
     * Updates the minesweeperArray with a guess.
     *
     * @param row is the row of the guess.
     * @param col is the column of the guess.
     * @return true when guessed.
     */
    public boolean guess(int row, int col) {
        boolean guessedSquare = false;
        if (row >= 0 && row < withMinesArray.length && col >= 0 && col < withMinesArray[0].length) {
            minesweeperArray[row][col] = " ? ";
            rounds++;
            guessedSquare = true;
        } else {
            System.out.println("your guess is out of bounds.");
        }
        return guessedSquare;
    }

    /**
     * Updates the minesweeperArray with a mark.
     *
     * @param row is the row of the mark.
     * @param col is the column of the guess.
     * @return true when marked.
     */
    public boolean mark(int row, int col) {
        boolean markedSquare = false;
        if (row >= 0 && row < withMinesArray.length && col >= 0 && col < withMinesArray[0].length) {
            minesweeperArray[row][col] = " F ";
            rounds++;
            markedSquare = true;
        } else {
            System.out.println("your mark is out of bounds.");
        }
        return markedSquare;
    }

    /**
     * Updates the minesweeperArray with numAdjMines. If bomb
     *
     * @param row is the row of the reveal.
     * @param col is the column of the reveal.
     * @return true is marked when it is revealed.
     */
    public boolean reveal(int row, int col) {
        boolean revealedSquare = false;
        if (row >= 0 && row < withMinesArray.length && col >= 0 && col < withMinesArray[0].length) {
            if (!withMinesArray[row][col]) {
                minesweeperArray[row][col] = " " + getNumAdjMines(row, col) + " ";
                rounds++;
                revealedSquare = true;
                revealedArray[row][col] = true;
                System.out.println();
            } else {
                printLoss();
            }
        }
        return revealedSquare;
    }

    /**
     *
     * Prints quit statement. Exits the game after.
     *
     */
    public void quit() {
        System.out.println();
        System.out.println("You have quit the game!");
        System.out.println("Come back soon.");
        System.exit(0);
    }

    /**
     * finds the number of adj Mines.
     *
     * @param row is the row of which it calculates the num adj mines.
     * @param col is the col of which it calculates the num adj mines.
     * @return the number of mines near the users picked location.
     */
    private int getNumAdjMines(int row, int col) {
        int numAdjMines = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            if (!(i >= 0 && i < minesweeperArray.length)) {
                continue;
            }
            for (int j = col - 1; j <= col + 1; j++) {
                if ((i == row && j == col) || (!(j >= 0 && j < minesweeperArray[0].length))) {
                    continue;
                } else {
                    if (withMinesArray[i][j]) {
                        numAdjMines++;
                    }
                }
            }
        }
        return numAdjMines;
    }

    /**
     * Prints the number of rounds.
     */
    public void rounds() {
        System.out.println("\nRounds Completed: " + rounds + "\n");
    }

    /**
     * Prints minesweeper grid.
     */
    public void printMineField() {
        for (int i = 0; i < minesweeperArray.length; i++) {
            System.out.print(i + " |");
            for (int j = 0; j < minesweeperArray[0].length; j++) {
                System.out.print(minesweeperArray[i][j]);
                if (j < minesweeperArray[i].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println("|");
        }
        System.out.print("    ");
        for (int k = 0; k < minesweeperArray[0].length; k++) {
            System.out.print(k + "   ");
        }
        System.out.println();
    }

    /**
     * prints a boolean value if the won status is met.
     *
     * @return boolean if won is met.
     */
    public boolean isWon() {
        boolean isWon = true;
        for (int i = 0; i < revealedArray.length; i++) {
            for (int j = 0; j < revealedArray[0].length; j++) {
                if (withMinesArray[i][j]) {
                    if (revealedArray[i][j]) {
                        isWon = false;
                        break;
                    }
                } else if (!revealedArray[i][j]) {
                    isWon = false;
                    break;
                }
            }
        }
        return isWon;
    }

    /**
     * prints the bash.
     */
    public void bash() {
        System.out.println();
        System.out.print("minesweeper-alpha: ");
    }

    /**
     * prints the noFog grid. Rounds increment after.
     */
    public void noFog() {
        for (int i = 0; i < noFogArray.length; i++) {
            System.out.print(i + " |");
            for (int j = 0; j < noFogArray[i].length; j++) {
                if (withMinesArray[i][j] && minesweeperArray[i][j].equals(" F ")) {
                    System.out.print("<" + minesweeperArray[i][j].trim() + ">");
                } else if (withMinesArray[i][j]) {
                    System.out.print("< " + minesweeperArray[i][j].trim() + ">");
                } else {
                    System.out.print(minesweeperArray[i][j]);
                }
                if (j < noFogArray[i].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println("|");
        }
        System.out.print("   ");
        for (int k = 0; k < minesweeperArray[0].length; k++) {
            System.out.print(" " + k + "  ");
        }
        rounds++;
        System.out.println();
    }

    /**
     * records the coordinates of the actions and preforms the actions for reveal
     * guess and mark.
     *
     * @param commandScan for user input.
     * @param command     for user input .
     */

    public void revealCell(Scanner commandScan, String command) {
        int row;
        int col;
        if (commandScan.hasNextInt()) {
            row = commandScan.nextInt();
        } else {
            row = -1;
        }
        if (commandScan.hasNextInt()) {
            col = commandScan.nextInt();
        } else {
            col = -1;
        }

        if (row < 0 || col < 0 || row >= minesweeperArray.length
            || col >= minesweeperArray[0].length) {
            System.out.println("\nCould not process command: " + "row or column is out of bounds");
        } else {
            switch (command) {
            case "m":
            case "mark":
                mark(row, col);
                rounds();
                printMineField();
                bash();
                break;
            case "reveal":
            case "r":
                reveal(row, col);
                if (isWon()) {
                    printWin();
                }
                rounds();
                printMineField();
                bash();
                break;
            case "guess":
            case "g":
                guess(row, col);
                rounds();
                printMineField();
                bash();
                break;
            }
        }
    }

    /**
     * Prints game prompt to standard user and interprets the user input and
     * preforms proper actions.
     *
     */
    public void promptUser() {
        String fullCommand = stdIn.nextLine().trim();
        if (fullCommand.split("[\\s\\t]+").length > 3) {
            System.out.println("Invalid Command: Command not recognized!");
            rounds();
            printMineField();
            bash();
            return;
        }
        Scanner commandScan = new Scanner(fullCommand);
        String command;
        if (commandScan.hasNext()) {
            command = commandScan.next().toLowerCase();
        } else {
            command = "-1";
        }
        switch (command) {
        case "h":
        case "help":
            help();
            rounds();
            printMineField();
            bash();
            break;
        case "q":
        case "quit":
            quit();
            System.exit(0);
        case "nofog":
            rounds();
            noFog();
            bash();
            System.out.println();
            break;

        case "f":
        case "m":
        case "r":
        case "g":
        case "guess":
        case "flag":
        case "mark":
        case "reveal":
            revealCell(commandScan, command);
            break;
        default:
            System.out.println();
            System.out.print("Could not process command: Incorrect input.");
            System.out.println();
            printMineField();
            bash();
        }
    }

    /**
     * initiates and executes main game loop.
     */
    public void play() {
        welcome();
        rounds();
        printMineField();
        bash();
        while (true) {
            if (!stdIn.hasNextLine()) {
                System.exit(0);
            }
            promptUser();
        }
    }
}
