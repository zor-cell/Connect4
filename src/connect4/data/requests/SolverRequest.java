package connect4.data.requests;

import connect4.data.Version;

import java.util.Arrays;

public class SolverRequest {
    public int[][] board;
    public int player;
    public int maxTime;
    public int maxDepth;
    public int tableSize;
    public Version version;

    public SolverRequest(int[][] board, int player, int maxTime, int maxDepth, int tableSize, Version version) {
        this.board = board;
        this.player = player;
        this.maxTime = maxTime;
        this.maxDepth = maxDepth;
        this.tableSize = tableSize;
        this.version = version;
    }

    @Override
    public String toString() {
        return "SolverConfig{" +
                "board=" + Arrays.deepToString(board) +
                ", player=" + player +
                ", maxTime=" + maxTime +
                ", maxDepth=" + maxDepth +
                ", tableSize=" + tableSize +
                ", version=" + version +
                '}';
    }
}
