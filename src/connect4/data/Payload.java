package connect4.data;

public class Payload {
    public int[][] board;
    public boolean validMove;

    public Payload(int[][] board, boolean validMove) {
        this.board = board;
        this.validMove = validMove;
    }
}
