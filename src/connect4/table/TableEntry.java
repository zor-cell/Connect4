package connect4.table;

import connect4.data.BestMove;

public class TableEntry {
    public long key;
    public int depth;
    public TableFlag flag;
    public BestMove bestMove;

    public TableEntry(long key, int depth, TableFlag flag, BestMove bestMove) {
        this.key = key;
        this.depth = depth;
        this.flag = flag;
        this.bestMove = bestMove;
    }
}

