package connect4.data.requests;

import java.util.Arrays;

public class SolverRequest {
    public int[][] board;
    public int player;
    public int maxTime;
    public int maxDepth;

    public SolverRequest(int[][] board, int player, int maxTime, int maxDepth) {
        this.board = board;
        this.player = player;
        this.maxTime = maxTime;
        this.maxDepth = maxDepth;
    }

    @Override
    public String toString() {
        return "SolverConfig{" +
                "board=" + Arrays.deepToString(board) +
                ", player=" + player +
                ", maxTime=" + maxTime +
                ", maxDepth=" + maxDepth +
                '}';
    }
}
