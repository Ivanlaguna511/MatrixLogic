/**
 * MatrixLogic - A Binary Puzzle Game
 * @author Iván Moro Cienfuegos
 * @author Diego Martín García
 */

import java.io.*;
import java.util.*;

public class MatrixLogic {

    public static void main(String[] args) {
        printWelcomeMessage();

        int[] scores = new int[2]; // scores[0] = wins, scores[1] = games played
        Random rand = new Random();
        
        int totalBoards = countBoards();
        if (totalBoards <= 0) {
            System.out.println("Error: 'tableros.txt' is missing or empty. Please ensure the file is in the root directory.");
            return;
        }

        int randomIndex = rand.nextInt(totalBoards);
        char[][] gameBoard = loadGameBoard(randomIndex);
        char[][] hiddenBoard = loadHiddenBoard(randomIndex);
        Stack<String> history = new Stack<>();

        printBoard(gameBoard);

        // Main game loop
        while (!playTurn(hiddenBoard, gameBoard, scores, "", history)) {
            printBoard(gameBoard);
            System.out.println();
        }
    }

    /**
     * Prints the initial welcome message and game rules.
     */
    private static void printWelcomeMessage() {
        System.out.println("+---------------------------+");
        System.out.println("|   M a t r i x L o g i c   |");
        System.out.println("+---------------------------+");
        System.out.println("Welcome to MatrixLogic!");
        System.out.println("Fill the grid using 'x' and 'o'. You can cycle through empty spaces and tokens.");
        System.out.println("Cycle order: ' ' ==> x  ==> o  ==> ' '");
        System.out.println("Enter '-' to undo your last move. You can undo to the very beginning.");
        System.out.println("\nRules to win:");
        System.out.println(" * You can only modify empty cells. Initial uppercase 'X' and 'O' are immutable.");
        System.out.println(" * Every cell must be filled (X, x, O, o).");
        System.out.println(" * Each row and column must contain an equal number of X's and O's.");
        System.out.println(" * You cannot have more than two identical consecutive items horizontally or vertically.");
        System.out.println(" * No two rows can be identical. No two columns can be identical.");
        System.out.println("\nTo start, enter the coordinate you want to place your first piece (e.g., 1A).");
        System.out.println("Good luck =)\n");
    }

    /**
     * Handles a single turn of the game.
     *
     * @param hiddenBoard The immutable initial state mask ('0' = editable, '1' = immutable).
     * @param gameBoard   The visible board.
     * @param scores      Array storing [wins, games played].
     * @param input       Predefined coordinate (used for rapid input handling).
     * @param history     Stack storing the history of board states for the Undo feature.
     * @return true if the game cycle is finished and player chose not to continue.
     */
    public static boolean playTurn(char[][] hiddenBoard, char[][] gameBoard, int[] scores, String input, Stack<String> history) {
        int rows = gameBoard.length;
        int cols = gameBoard[0].length;
        boolean isBoardFull = true;

        if (input.equals("")) {
            System.out.print("ENTER A BOARD COORDINATE (1 - " + rows + ")(A - " + (char) (cols + 64) + ") OR '-' TO UNDO: ");
        }

        readInput(gameBoard, hiddenBoard, input, history, scores);

        // Check if the board is completely filled
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gameBoard[i][j] == ' ') {
                    isBoardFull = false;
                }
            }
        }

        // If the board is full, ask to submit or keep editing
        if (isBoardFull) {
            printBoard(gameBoard);
            System.out.print("Board is full! Press ENTER to submit, or enter a coordinate to change a piece: ");
            Scanner scanner = new Scanner(System.in);
            String action = scanner.nextLine();

            if (action.equals("")) {
                checkBoard(gameBoard, scores);
                System.out.print("Do you want to play another game? (Yes / No): ");
                String playAgain = scanner.nextLine().trim();

                if (playAgain.equalsIgnoreCase("YES")) {
                    history.clear(); // Reset history for new game
                    Random rand = new Random();
                    int randomIndex = rand.nextInt(countBoards());
                    
                    // Reload arrays by reference modification wouldn't work easily here, 
                    // so we deep copy or reassign for the new loop.
                    char[][] newGameBoard = loadGameBoard(randomIndex);
                    char[][] newHiddenBoard = loadHiddenBoard(randomIndex);
                    
                    // Copy new references to old ones
                    for(int i=0; i<rows; i++) {
                        System.arraycopy(newGameBoard[i], 0, gameBoard[i], 0, cols);
                        System.arraycopy(newHiddenBoard[i], 0, hiddenBoard[i], 0, cols);
                    }
                    
                    printBoard(gameBoard);
                    return false; // Keep playing
                    
                } else {
                    System.out.println("\nGame Over.");
                    System.out.println("Boards Played: " + scores[1] + " | Boards Solved: " + scores[0]);
                    int winRate = (scores[1] == 0) ? 0 : (scores[0] * 100) / scores[1];
                    System.out.println("Win Rate: " + winRate + "%");
                    return true; // End application
                }
            } else {
                // Keep playing current board with provided coordinate
                while (!playTurn(hiddenBoard, gameBoard, scores, action, history)) {
                    action = "";
                    printBoard(gameBoard);
                    System.out.println();
                }
            }
        }
        return isBoardFull;
    }

    /**
     * Counts the total number of boards available in "tableros.txt".
     *
     * @return Total boards count or -1 if file not found.
     */
    public static int countBoards() {
        int count = 0;
        // Using Try-with-resources for automatic resource management
        try (BufferedReader br = new BufferedReader(new FileReader("tableros.txt"))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            System.out.println("Error reading 'tableros.txt'.");
            return -1;
        }
        return count;
    }

    /**
     * Creates a new game board reading from "tableros.txt".
     */
    public static char[][] loadGameBoard(int index) {
        char[][] board = null;

        try (BufferedReader br = new BufferedReader(new FileReader("tableros.txt"))) {
            for (int i = 0; i < index; i++) {
                br.readLine();
            }

            String line = br.readLine();
            int cols = line.indexOf(" ");
            line = line.replace(" ", "");
            int rows = line.length() / cols;

            board = new char[rows][cols];

            int charIndex = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = line.charAt(charIndex);
                    if (c == '0') board[i][j] = ' ';
                    else if (c == '1') board[i][j] = 'X';
                    else board[i][j] = 'O';
                    charIndex++;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load game board.");
        }
        return board;
    }

    /**
     * Creates the hidden mask identifying which tiles are immutable.
     */
    public static char[][] loadHiddenBoard(int index) {
        char[][] hiddenBoard = null;

        try (BufferedReader br = new BufferedReader(new FileReader("tableros.txt"))) {
            for (int i = 0; i < index; i++) {
                br.readLine();
            }

            String line = br.readLine();
            int cols = line.indexOf(" ");
            line = line.replace(" ", "");
            int rows = line.length() / cols;

            hiddenBoard = new char[rows][cols];

            int charIndex = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (line.charAt(charIndex) == '0') hiddenBoard[i][j] = '0'; // Editable
                    else hiddenBoard[i][j] = '1'; // Immutable
                    charIndex++;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load hidden board.");
        }
        return hiddenBoard;
    }

    /**
     * Checks if the board satisfies all winning conditions.
     */
    public static void checkBoard(char[][] board, int[] scores) {
        boolean isValid = true;
        int countXRow = 0, countORow = 0;
        int countXCol = 0, countOCol = 0;
        String failReason = "";

        // Clone board and convert to lowercase for homogeneous checking
        char[][] auxBoard = new char[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                auxBoard[i][j] = Character.toLowerCase(board[i][j]);
            }
        }

        // Rule 1 & 2: Row counts and max 2 contiguous
        for (int i = 0; i < auxBoard.length; i++) {
            for (int j = 0; j < auxBoard[0].length; j++) {
                if (auxBoard[i][j] == 'x') countXRow++;
                else countORow++;

                // Contiguous check
                if (j + 2 < auxBoard[0].length) {
                    if (auxBoard[i][j] == auxBoard[i][j + 1] && auxBoard[i][j] == auxBoard[i][j + 2]) {
                        isValid = false;
                        failReason = "More than 2 identical items contiguous horizontally.";
                    }
                }
                if (i + 2 < auxBoard.length) {
                    if (auxBoard[i][j] == auxBoard[i + 1][j] && auxBoard[i][j] == auxBoard[i + 2][j]) {
                        isValid = false;
                        failReason = "More than 2 identical items contiguous vertically.";
                    }
                }
            }
            if (countORow != countXRow) {
                isValid = false;
                failReason = "Unequal number of X and O in a row.";
            }
            countXRow = 0;
            countORow = 0;
        }

        // Rule 3: Column counts
        for (int j = 0; j < auxBoard[0].length; j++) {
            for (int i = 0; i < auxBoard.length; i++) {
                if (auxBoard[i][j] == 'x') countXCol++;
                else countOCol++;
            }
            if (countOCol != countXCol) {
                isValid = false;
                failReason = "Unequal number of X and O in a column.";
            }
            countXCol = 0;
            countOCol = 0;
        }

        // Rule 4: Identical lines
        if (hasIdenticalLines(auxBoard)) {
            isValid = false;
            failReason = "Two or more rows/columns are identical.";
        }

        if (isValid) {
            System.out.println("\n*** Congratulations! You solved the board perfectly! ***");
            scores[0]++;
            scores[1]++;
        } else {
            System.out.println("\n[!] Incorrect Solution: " + failReason);
            scores[1]++;
        }
    }

    /**
     * Parses user input, handles the Undo logic utilizing a Stack, and updates the board.
     */
    public static void readInput(char[][] gameBoard, char[][] hiddenBoard, String predefinedInput, Stack<String> history, int[] scores) {
        Scanner in = new Scanner(System.in);
        String inputStr = predefinedInput;
        boolean validInput = false;
        boolean isUndo = false;

        // Input validation loop
        while (!validInput) {
            if (inputStr.equals("")) {
                inputStr = in.next().toUpperCase();
            }

            if (inputStr.equals("-")) {
                if (!history.isEmpty()) {
                    isUndo = true;
                    validInput = true;
                } else {
                    System.out.print("CANNOT UNDO. THIS IS THE INITIAL BOARD STATE: ");
                    inputStr = "";
                }
            } else if (inputStr.length() < 2 || inputStr.length() > 3) {
                System.out.print("INVALID INPUT. ENTER A VALID COORDINATE (e.g. 1A): ");
                inputStr = "";
            } else {
                try {
                    int row = Integer.parseInt(inputStr.substring(0, inputStr.length() - 1));
                    char col = inputStr.charAt(inputStr.length() - 1);

                    if (row >= 1 && row <= gameBoard.length && col >= 'A' && col < 'A' + gameBoard[0].length) {
                        validInput = true;
                    } else {
                        System.out.print("OUT OF BOUNDS. ENTER A VALID COORDINATE: ");
                        inputStr = "";
                    }
                } catch (NumberFormatException e) {
                    System.out.print("INVALID FORMAT. ENTER A VALID COORDINATE: ");
                    inputStr = "";
                }
            }
        }

        // Process Undo via Stack
        if (isUndo) {
            String state = history.pop();
            int index = 0;
            for (int i = 0; i < gameBoard.length; i++) {
                for (int j = 0; j < gameBoard[0].length; j++) {
                    gameBoard[i][j] = state.charAt(index++);
                }
            }
            printBoard(gameBoard);
            playTurn(hiddenBoard, gameBoard, scores, "", history);
            
        } else {
            // Save current state to history Stack before making a move
            StringBuilder stateBuilder = new StringBuilder();
            for (int i = 0; i < gameBoard.length; i++) {
                for (int j = 0; j < gameBoard[0].length; j++) {
                    stateBuilder.append(gameBoard[i][j]);
                }
            }
            history.push(stateBuilder.toString());

            int rowIndex = Integer.parseInt(inputStr.substring(0, inputStr.length() - 1)) - 1;
            int colIndex = inputStr.charAt(inputStr.length() - 1) - 'A';

            if (hiddenBoard[rowIndex][colIndex] == '0') {
                if (gameBoard[rowIndex][colIndex] == ' ') gameBoard[rowIndex][colIndex] = 'x';
                else if (gameBoard[rowIndex][colIndex] == 'x') gameBoard[rowIndex][colIndex] = 'o';
                else gameBoard[rowIndex][colIndex] = ' ';
            } else {
                System.out.println("[!] You cannot modify the immutable tiles of the initial board.");
                history.pop(); // Remove state since no move was made
            }
        }
    }

    public static boolean hasIdenticalLines(char[][] matrix) {
        // Check rows
        for (int i = 0; i < matrix.length - 1; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                if (Arrays.equals(matrix[i], matrix[j])) return true;
            }
        }
        // Check columns
        for (int i = 0; i < matrix[0].length - 1; i++) {
            for (int j = i + 1; j < matrix[0].length; j++) {
                if (areColumnsEqual(matrix, i, j)) return true;
            }
        }
        return false;
    }

    public static boolean areColumnsEqual(char[][] matrix, int col1, int col2) {
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][col1] != matrix[i][col2]) return false;
        }
        return true;
    }

    /**
     * Prints the visual representation of the matrix to the console.
     */
    public static void printBoard(char[][] board) {
        System.out.print("  ");
        for (int i = 0; i < board[0].length; i++) {
            System.out.print("   " + (char) (i + 65));
        }
        System.out.print("\n  +---+");
        for (int i = 0; i < board[0].length - 1; i++) {
            System.out.print("---+");
        }
        System.out.println();
        
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (j == 0) {
                    System.out.printf("%2d|", (i + 1));
                }
                System.out.print(" " + board[i][j] + " |");
            }
            System.out.print("\n  +");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print("---+");
            }
            System.out.println();
        }
    }
}