package connect4;

import connect4.data.MovePayload;
import connect4.data.BestMovePayload;

public class Connector {
    public static BestMovePayload makeBestMove(int[][] board, int player) {
        return new BestMovePayload(board, true);
    }

    public static MovePayload makeMove(int[][] board, int player, int j) {
        return GameChecker.createMovePayload(board, player, j);
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
