package connect4.data;

public class MovePayload {
    public int[][] board;
    public Position position;
    public GameState gameState;
    public int score;

    public MovePayload(int[][] board, Position position, GameState gameState, int score) {
        this.board = board;
        this.position = position;
        this.gameState = gameState;
        this.score = score;
    }

    public MovePayload(int[][] board, Position position, GameState gameState) {
        this(board, position, gameState, 0);
    }
}
