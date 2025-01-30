package connect4.data.responses;

import connect4.data.GameState;
import connect4.data.Position;

public class MoveResponse {
    public int[][] board;
    public Position position;
    public GameState gameState;

    public MoveResponse(int[][] board, Position position, GameState gameState) {
        this.board = board;
        this.position = position;
        this.gameState = gameState;
    }
}
