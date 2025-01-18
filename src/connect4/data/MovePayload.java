package connect4.data;

public class MovePayload {
    public int[][] board;
    public Position position;
    public GameState gameState;

    public MovePayload(int[][] board, Position position, GameState gameState) {
        this.board = board;
        this.position = position;
        this.gameState = gameState;
    }
}
