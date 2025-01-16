package connect4.data;

import java.util.Arrays;

public class MovePayload {
    public int[][] board;
    public int moveI;
    public int moveJ;

    public MovePayload(int[][] board, int moveI, int moveJ) {
        this.board = board;
        this.moveI = moveI;
        this.moveJ = moveJ;
    }

    public MovePayload(int[][] board, Position movePosition) {
        this.board = board;

        if(movePosition == null) {
            this.moveI = -1;
            this.moveJ = -1;
        } else {
            this.moveI = movePosition.i;
            this.moveJ = movePosition.j;
        }
    }

    @Override
    public String toString() {
        return "MovePayload{" +
                "board=" + Arrays.deepToString(board) +
                ", moveI=" + moveI +
                ", moveJ=" + moveJ +
                '}';
    }
}
