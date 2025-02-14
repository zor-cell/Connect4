package connect4.board;

import connect4.data.GameState;
import connect4.data.Version;
import connect4.data.requests.SolverRequest;

public interface Board {
    /**
     * Indicates whether a column on the current board is a valid move.
     */
    boolean canMakeMove(int col);

    /**
     * Indicates whether making a move on the given column wins directly.
     */
    boolean isWinningMove(int col);

    /**
     * Make a move on the given column.
     */
    void makeMove(int col);

    /**
     * Returns the game state of the current board position. (Win, Draw, still running)
     */
    GameState getGameState();

    /**
     * Gets the number of moves that have already been played in the current position.
     */
    int getMoves();

    /**
     * Gets the number of columns of the current board.
     */
    int getColumns();

    /**
     * Gets a hash value of the current board position that can be used as
     * a key in a transposition table.
     */
    long getHash();

    /**
     * Returns a heuristics score that ranks the current board position.
     * Positive results are good for the current player, negative results are bad for the current player.
     */
    int heuristics();

    static Board getInstance(Board board) {
        if(board instanceof Bitboard) {
            return new Bitboard((Bitboard) board);
        } else if(board instanceof SimpleBoard) {
            return new SimpleBoard((SimpleBoard) board);
        }

        return null;
    }

    static Board getInstance(SolverRequest request) {
        if(request.version == Version.V2_0 || request.version == Version.V2_1) {
            return new Bitboard(request.board, request.player);
        } else if(request.version == Version.V1_0 || request.version == Version.V1_1) {
            return new SimpleBoard(request.board, request.player);
        }

        return null;
    }
}
