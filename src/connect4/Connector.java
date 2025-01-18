package connect4;

import connect4.data.GameState;
import connect4.data.MovePayload;
import connect4.data.Position;

public class Connector {
    public static MovePayload makeBestMove(int[][] board, int player) {
        return GameChecker.createMovePayload(board, player, 0);
    }

    public static MovePayload makeMove(int[][] board, int player, int j) {
        return GameChecker.createMovePayload(board, player, j);
    }

    public static MovePayload undoMove(int[][] board, Position position) {
        return new MovePayload(board, position, GameState.RUNNING);
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
