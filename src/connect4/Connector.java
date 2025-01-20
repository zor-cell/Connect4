package connect4;

import connect4.data.GameState;
import connect4.data.MovePayload;
import connect4.data.Position;

public class Connector {
    public static MovePayload makeBestMove(int[][] board, int player) {
        return GameSolver.findBestMove(board, player);
    }

    public static MovePayload makeMove(int[][] board, int player, int j) {
        return GameChecker.makeMove(board, player, j);
    }

    public static MovePayload undoMove(int[][] board, Position position) {
        board[position.i][position.j] = 0;

        return new MovePayload(board, position, GameState.RUNNING);
    }
}
