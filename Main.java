package tictactoe;

import java.util.*;
import java.util.stream.Collectors;

import static tictactoe.BoardItem.toBoardItem;

class Board {
    BoardItem[][] board;

    Board(int rows, int cols) {
        this(rows, cols, null);
    }

    Board(char[] boardState) {
        this(3, 3, boardState);
    }

    Board(int rows, int cols, char[] boardState) {
        board = new BoardItem[rows][cols];

        if (boardState == null) {
            boardState = new char[rows * cols];
            Arrays.fill(boardState, BoardItem.EMPTY.toString().charAt(0));
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = toBoardItem(boardState[i * rows + j]);
            }
        }
    }

    public boolean setMove(int x, int y, BoardItem item) {
        if (board[x - 1][y - 1] == BoardItem.EMPTY) {
            board[x - 1][y - 1] = item;
            return true;
        }
        return false;
    }

    private int[] getTypeNumberOfBoardItem() {
        return Arrays.stream(board).map(row -> {
            int[] counts = new int[3];
            counts[0] += Arrays.stream(row).filter(boardItem -> boardItem.equals(BoardItem.X)).count();
            counts[1] += Arrays.stream(row).filter(boardItem -> boardItem.equals(BoardItem.O)).count();
            counts[2] += Arrays.stream(row).filter(boardItem -> boardItem.equals(BoardItem.EMPTY)).count();
            return counts;
        }).reduce((a, b) -> {
            a[0] += b[0];
            a[1] += b[1];
            a[2] += b[2];
            return a;
        }).orElse(new int[3]);
    }

    private int[][] getBoardItemsInARow() {
        int[] countsHorizontal = new int[3];
        for (int i = 0; i < board.length; i++) {
            BoardItem item = board[i][0];
            for (int j = 0; j < board[i].length; j++) {
                if (item != board[i][j]) {
                    break;
                }
                if (!item.equals(BoardItem.EMPTY)) {
                    countsHorizontal[i]++;
                }
            }
        }

        int[] countsVertical = new int[3];
        for (int i = 0; i < board.length; i++) {
            BoardItem item = board[0][i];
            for (BoardItem[] boardItems : board) {
                if (item != boardItems[i]) {
                    break;
                }
                if (!item.equals(BoardItem.EMPTY)) {
                    countsVertical[i]++;
                }
            }
        }
        return new int[][]{countsHorizontal, countsVertical};
    }

    public boolean isDiagonalWinning() {
        if (board[0][0] == BoardItem.EMPTY || board[board.length - 1][0] == BoardItem.EMPTY) {
            return false;
        }

        BoardItem initial = board[0][0];
        for (int i = 0; i < board.length; i++) {
            if (!board[i][i].equals(initial)) {
                break;
            }
            if (i == board.length - 1) {
                return true;
            }
        }

        initial = board[board.length - 1][0];
        int i = board.length - 1;
        for (BoardItem[] boardItems : board) {
            if (!boardItems[i--].equals(initial)) {
                return false;
            }
        }

        return true;
    }

    public String getResult() {
        int[] countBoardItems = getTypeNumberOfBoardItem();
        if (Math.abs(countBoardItems[0] - countBoardItems[1]) > 1) {
            return "Impossible";
        }

        if (isDiagonalWinning()) {
            return board[board.length / 2][board.length / 2] + " wins";
        }
        int[][] boardItemsInARow = getBoardItemsInARow();

        Map<Integer, int[]> results = new HashMap<>();
        for (int i = 0; i < boardItemsInARow.length; i++) {
            for (int j = 0; j < boardItemsInARow[i].length; j++) {
                if (boardItemsInARow[i][j] == boardItemsInARow[i].length) {
                    if (results.containsKey(i)) {
                        return "Impossible";
                    }
                    int[] winnerPosition;
                    if (i == 0) {
                        winnerPosition = new int[]{j, 0};
                    } else {
                        winnerPosition = new int[]{0, j};
                    }
                    results.put(i, winnerPosition);
                }
            }
        }

        if (!results.isEmpty()) {
            int[] pos = results.values().stream().findFirst().orElse(null);
            return board[pos[0]][pos[1]].toString() + " wins";
        }

        if (countBoardItems[2] > 0) {
            return "Game not finished";
        }

        return "Draw";
    }

    public void print() {
        System.out.println("---------");

        Arrays.stream(board).forEach(row -> {
            String stringRow = Arrays
                    .stream(row)
                    .map(String::valueOf)
                    .collect(Collectors.joining(" "));
            System.out.printf("| %s |\n", stringRow);
        });

        System.out.println("---------");
    }
}


public class Main {

    private static final String ERROR_NUMBERS_TYPE = "You should enter numbers!";
    private static final String ERROR_NUMBERS_RANGE = "Coordinates should be from 1 to 3!";
    private static final String ERROR_OCCUPIED_CELL = "This cell is occupied! Choose another one!";
    private static final String PROMPT_INPUT = "Enter the coordinates: ";
    private static final Scanner scanner = new Scanner(System.in);

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isValidNumber(int n) {
        return n >= 0 && n <= 3;
    }

    private static int[] getInput() {
        while (true) {
            System.out.print(PROMPT_INPUT);
            List<Integer> input = scanner.tokens()
                    .limit(2)
                    .filter(t -> isDigit(t.charAt(0)))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (input.size() != 2) {
                System.out.println(ERROR_NUMBERS_TYPE);
            } else {
                if (isValidNumber(input.get(0)) && isValidNumber(input.get(1))) {
                    return new int[]{input.get(0), input.get(1)};
                }
                System.out.println(ERROR_NUMBERS_RANGE);
            }
        }
    }

    public static void main(String[] args) {
        Board board = new Board(3, 3);
        board.print();
        BoardItem currentPlayer = BoardItem.X;
        while (true) {
            if (!board.getResult().equals("Game not finished")) {
                System.out.println(board.getResult());
                break;
            }

            int[] inputCoordinates = getInput();
            if (!board.setMove(inputCoordinates[0], inputCoordinates[1], currentPlayer)) {
                System.out.println(ERROR_OCCUPIED_CELL);
            } else {
                board.print();
                currentPlayer = togglePlayer(currentPlayer);
            }
        }
    }

    private static BoardItem togglePlayer(BoardItem item) {
        if (item == BoardItem.X) {
            return BoardItem.O;
        }
        return BoardItem.X;
    }
}
