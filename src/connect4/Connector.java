package connect4;

import connect4.data.MovePayload;
import connect4.data.Payload;
import connect4.data.Position;

public class Connector {
    public static Payload makeBestMove(int[][] board, int player) {
        return new Payload(board, true);
    }

    public static MovePayload makeMove(int[][] board, int player, int i, int j) {
        return GameChecker.createMovePayload(board, player, j);
    }

    public static void main(String[] args) {

    }
}
