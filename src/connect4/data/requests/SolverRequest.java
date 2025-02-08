package connect4.data.requests;

import java.util.Arrays;

public class SolverRequest {
    public int[][] board;
    public int player;
    public int maxTime;
    public int maxDepth;
    public int tableSize;

    public SolverRequest(int[][] board, int player, int maxTime, int maxDepth, int tableSize) {
        this.board = board;
        this.player = player;
        this.maxTime = maxTime;
        this.maxDepth = maxDepth;
        this.tableSize = tableSize;
    }

    @Override
    public String toString() {
        return "SolverConfig{" +
                "board=" + Arrays.deepToString(board) +
                ", player=" + player +
                ", maxTime=" + maxTime +
                ", maxDepth=" + maxDepth +
                ", tableSize=" + tableSize +
                '}';
    }
}
