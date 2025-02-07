package connect4;

import connect4.data.GameState;

import java.util.List;

public interface Board {
    boolean canMakeMove(int col);

    boolean isWinningMove(int col);

    void makeMove(int col);

    GameState getGameState();

    int heuristics();

    static Board getInstance() {
        return null;
    }
}
