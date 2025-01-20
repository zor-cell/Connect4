package connect4;

import connect4.data.*;

public class Connector {
    public static MovePayload makeBestMove(int[][] board, int player) {
        SolverConfig config = new SolverConfig(board, player, 2000, 0);
        BestMove bestMove = Solver.startSolver(config);

        return new MovePayload(board, bestMove.position, GameState.RUNNING);
    }

    public static MovePayload makeMove(int[][] board, int player, int j) {
        return GameChecker.makeMove(board, player, j);
    }

    public static MovePayload undoMove(int[][] board, Position position) {
        board[position.i][position.j] = 0;

        return new MovePayload(board, position, GameState.RUNNING);
    }
}
