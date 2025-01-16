package connect4.data;

import java.util.Arrays;

public class MovePayload {
    public int[][] board;
    public int moveI;
    public int moveJ;
    public int gameState;

    public MovePayload(int[][] board, int moveI, int moveJ, int gameState) {
        this.board = board;
        this.moveI = moveI;
        this.moveJ = moveJ;
        this.gameState = gameState;
    }

    @Override
    public String toString() {
        return "MovePayload{" +
                "board=" + Arrays.deepToString(board) +
                ", moveI=" + moveI +
                ", moveJ=" + moveJ +
                ", gameState=" + gameState +
                '}';
    }
}
