package connect4;

import connect4.data.GameState;
import connect4.data.MovePayload;
import connect4.data.Position;

public class GameChecker {
    private int[][] board;
    private int rows;
    private int cols;

    public GameChecker(int[][] board) {
        this.board = board;
        this.rows = this.board.length;
        this.cols = this.board[0].length;
    }

    public GameState getGameState() {
        //TODO check if anybody won
        return GameState.RUNNING;
    }

    public void makeMove(Position move, int player) {
        if(move == null) return;

        board[move.i][move.j] = player;
    }

    public Position getMoveFromCol(int col) {
        int i = rows - 1;
        while(i >= 0 && board[i][col] != 0) {
            i--;
        }
        if(i >= 0) {
            return new Position(i, col);
        }

        return null;
    }

    public static MovePayload createMovePayload(int[][] board, int player, int j) {
        GameChecker gameChecker = new GameChecker(board);

        Position move = gameChecker.getMoveFromCol(j);
        gameChecker.makeMove(move, player);
        GameState gameState = gameChecker.getGameState();

        return new MovePayload(gameChecker.board, move);
    }
}
