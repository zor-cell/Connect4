package connect4.data;

public class BestMovePayload {
    public int[][] board;
    public boolean validMove;

    public BestMovePayload(int[][] board, boolean validMove) {
        this.board = board;
        this.validMove = validMove;
    }
}
