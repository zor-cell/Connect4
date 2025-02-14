package connect4.board;

import connect4.data.GameState;

public class SimpleBoard implements Board {
    @Override
    public boolean canMakeMove(int col) {
        return false;
    }

    @Override
    public boolean isWinningMove(int col) {
        return false;
    }

    @Override
    public void makeMove(int col) {

    }

    @Override
    public GameState getGameState() {
        return null;
    }

    @Override
    public int getMoves() {
        return 0;
    }

    @Override
    public int getColumns() {
        return 0;
    }

    @Override
    public long getHash() {
        return 0;
    }

    @Override
    public int heuristics() {
        return 0;
    }
}
