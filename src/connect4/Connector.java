package connect4;

import connect4.data.Payload;
import connect4.data.Position;

public class Connector {
    public static Payload computeBestMove(int[][] board) {
        board[0][0] = 2;

        return new Payload(board);
    }

    public static Payload makeMove(int[][] board, int player, int i, int j) {
        int rows = board.length;
        int cols = board[0].length;

        int k = rows - 1;
        while(k >= 0 && board[k][j] != 0) {
            k--;
        }
        if(k >= 0) {
            board[k][j] = player;
        }

        return new Payload(board);
    }

    public static void main(String[] args) {

    }
}
