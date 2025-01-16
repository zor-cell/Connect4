package connect4;

import connect4.data.MovePayload;
import connect4.data.Payload;
import connect4.data.Position;

public class Connector {
    public static Payload makeBestMove(int[][] board, int player) {
        return new Payload(board, true);
    }

    public static MovePayload makeMove(int[][] board, int player, int j) {
        MovePayload a = GameChecker.createMovePayload(board, player, j);
        return a;
    }

    public static void main(String[] args) {
        int[][] board = new int[6][7];
        int player = 1;
        int j = 3;

        MovePayload payload = makeMove(board, player, j);
        System.out.println(payload);

        payload = makeMove(board, player, 3);
        System.out.println(payload);
    }
}
