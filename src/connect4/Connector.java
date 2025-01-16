package connect4;

import connect4.data.Payload;
import connect4.data.Position;

public class Connector {
    public static Payload computeBestMove() {
        int[][] board = new int[6][7];

        int i = (int) (Math.random() * 6);
        int j = (int) (Math.random() * 7);
        board[i][j] = 1;

        return new Payload(board);
    }

    public static Payload makeMove(Position pos) {
        return computeBestMove();
    }

    public static void main(String[] args) {

    }
}
