package connect4.data.responses;

import connect4.data.BestMove;
import connect4.data.GameState;
import connect4.data.Position;

public class SolverResponse {
    public int[][] board;
    public GameState gameState;
    public BestMove bestMove;

    public SolverResponse(int[][] board, GameState gameState, BestMove bestMove) {
        this.board = board;
        this.gameState = gameState;
        this.bestMove = bestMove;
    }
}
