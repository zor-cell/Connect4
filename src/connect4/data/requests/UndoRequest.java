package connect4.data.requests;

import connect4.data.Position;

import java.util.Arrays;

public class UndoRequest {
    public int[][] board;

    public Position position;

    public UndoRequest(int[][] board, Position position) {
        this.board = board;
        this.position = position;
    }

    @Override
    public String toString() {
        return "SolverConfig{" +
                "board=" + Arrays.deepToString(board) +
                ", position=" + position +
                '}';
    }
}
