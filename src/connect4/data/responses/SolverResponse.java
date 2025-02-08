package connect4.data.responses;

import connect4.data.BestMove;
import connect4.data.GameState;
import connect4.data.Position;

public class SolverResponse {
    public int[][] board;
    public GameState gameState;
    public Position position;
    public int score;
    public int winDistance;

    public SolverResponse(int[][] board, GameState gameState, Position position, int score, int winDistance) {
        this.board = board;
        this.gameState = gameState;
        this.position = position;
        this.score = score;
        this.winDistance = winDistance;
    }
}
