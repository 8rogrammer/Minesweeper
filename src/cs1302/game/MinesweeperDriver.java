package cs1302.game;

import java.util.*;
import java.io.*;

/**
 * Main Driver class that runs the game and creates new game.
 *
 */
public class MinesweeperDriver {

    public static void main(String[] args) {
        Scanner stdIn = new Scanner(System.in);
        MinesweeperGame game = new MinesweeperGame(stdIn, args[0]);
        game.play();
    }
}
