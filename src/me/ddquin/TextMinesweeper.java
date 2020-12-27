package me.ddquin;

import java.util.Arrays;
import java.util.Scanner;

public class TextMinesweeper {

    private Board board;

    public TextMinesweeper() {
        initBoardVariables();
        displayBoard();
        boolean finished = false;
        while (!finished) {
            String[] tokens = takeInput();
            if (tokens[0].equals("flag")){
                board.flagTile(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
            } else if (tokens[0].equals("mine")) {
                Board.GameStatus status = board.sweepTile(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
                if (status == Board.GameStatus.LOST) {
                    System.out.println("You lost!");
                    board.makeAllVisible();
                    finished = true;
                } else if (status == Board.GameStatus.WON) {
                    System.out.println("You won!");
                    board.makeAllVisible();
                    finished = true;
                } else {
                    // normal tile
                }
            } else {
                System.out.println("Somthing went wrong");
            }
            displayBoard();
        }
    }

    public String[] takeInput() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter flag x y or mine x y");
        String input = scan.nextLine();
        String[] tokens = input.split(" ");
        while(tokens.length < 3) {
            System.out.println("There must be 3 arguments, flag/mine x y");
            return takeInput();
        }
        while (!(tokens[0].equals("flag") || tokens[0].equals("mine"))) {
            System.out.println("The first argument must be flag/mine!");
            return takeInput();
        }
        try {
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            while (board.coordOutOfBounds(x,y)) {
                System.out.println("The numbers must be in bounds!");
                return takeInput();
            }
        } catch(NumberFormatException e) {
            System.out.println("The second and third arguments must be numbers!");
            return takeInput();
        }
        return tokens;
    }

    private void initBoardVariables() {
        Scanner scan = new Scanner(System.in);
        scan = new Scanner(System.in);
        System.out.println("What should the width of the board be?");
        int width = scan.nextInt();
        while (width < 1) {
            System.out.println("The width of the board must be more than 0!");
            width = scan.nextInt();
        }

        System.out.println("What should the height of the board be?");
        int height = scan.nextInt();
        while (height < 1) {
            System.out.println("The height of the board must be more than 0!");
            height = scan.nextInt();
        }

        System.out.println("How many mines should be in the board?");
        int mines = scan.nextInt();
        while (mines < 0 | mines > (width * height) - 1) {
            System.out.println("There should be no negative number of mines and it can not exceed " + ((width * height) - 1));
            mines = scan.nextInt();
        }
        board = new Board(width, height, mines);
    }

    private void displayBoard() {
        System.out.print("x  ");
        Tile tiles[][] = board.getTiles();
        int width = board.getWidth();
        int height = board.getHeight();
        for (int i = 0; i < width; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.print("y  ");
        for (int i = 0; i < width; i++) {
            System.out.print("_ ");
        }
        for (int row = 0; row < height; row++) {
            System.out.println();
            System.out.print(row + "| ");
            for (int column = 0; column < width; column++) {
                Tile tile = tiles[row][column];
                System.out.print(getTileString(tile) + " ");
            }
        }
        System.out.println("Flags: " + board.getFlags());
    }

    public String getTileString(Tile tile) {
        if (tile.isFlagged()) {
            return "F";
        }
        if (tile.isHidden()) {
            return "H";
        }
        if (tile.isMine()) {
            return "x";
        }
        if (tile.getMinesAdjacent() == 0) return "O";
        return "" + tile.getMinesAdjacent();
    }


}
