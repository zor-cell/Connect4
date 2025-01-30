package connect4.data.responses;

import connect4.data.GameState;
import connect4.data.Position;

public class SolverResponse {
    public int[][] board;
    public Position position;
    public GameState gameState;
    public int score;

    public SolverResponse(int[][] board, Position position, GameState gameState, int score) {
        this.board = board;
        this.position = position;
        this.gameState = gameState;
        this.score = score;
    }

    public SolverResponse(int[][] board, Position position, GameState gameState) {
        this(board, position, gameState, 0);
    }
}
