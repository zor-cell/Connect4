package connect4;

import connect4.data.Payload;
import connect4.data.Position;

public class Connector {
    public static Payload computeBestMove(int[][] board) {
        board[0][0] = 2;

        return new Payload(board);
    }

    public static Payload makeMove(int[][] board, int i, int j) {
        board[i][j] = 1;

        return new Payload(board);
    }

    public static void main(String[] args) {

    }
}
