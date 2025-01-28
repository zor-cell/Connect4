package connect4.data.requests;

import connect4.data.Position;

import java.util.Arrays;

public class MoveRequest {
    public int[][] board;
    public int player;

    public Position position;

    public MoveRequest(int[][] board, int player, Position position) {
        this.board = board;
        this.player = player;
        this.position = position;
    }

    @Override
    public String toString() {
        return "SolverConfig{" +
                "board=" + Arrays.deepToString(board) +
                ", player=" + player +
                ", position=" + position +
                '}';
    }
}
