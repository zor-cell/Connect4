package connect4;

import connect4.board.SimpleBoard;
import connect4.data.*;
import connect4.data.requests.MoveRequest;
import connect4.data.requests.SolverRequest;
import connect4.data.requests.UndoRequest;
import connect4.data.responses.MoveResponse;
import connect4.data.responses.SolverResponse;

/**
 * This class serves as the interaction interface between a frontend worker and
 * Java methods.
 */
public class Connector {
    public static SolverResponse makeBestMove(SolverRequest request) {
        SimpleBoard board = new SimpleBoard(request.board, request.player);

        //get best move
        BestMove bestMove = Solver.startSolver(request);
        Position position = board.getMoveFromCol(bestMove.move);
        assert position != null;

        //make best move
        board.makeMove(position.j);

        //compute current game state
        GameState gameState = board.getGameState();

        return new SolverResponse(board.getBoard(), gameState, position, bestMove.score, bestMove.winDistance);
    }

    public static MoveResponse makeMove(MoveRequest request) {
        SimpleBoard board = new SimpleBoard(request.board, request.player);

        //get move
        Position move = board.getMoveFromCol(request.position.j);

        //check for invalid move
        if(move == null) {
            return new MoveResponse(request.board, null, GameState.RUNNING);
        }

        //make valid move
        board.makeMove(move.j);

        //compute current game state
        GameState gameState = board.getGameState();

        return new MoveResponse(board.getBoard(), move, gameState);
    }

    public static MoveResponse undoMove(UndoRequest request) {
        //does not matter which players turn it is for undo
        SimpleBoard board = new SimpleBoard(request.board, 1);

        //undo move
        board.unmakeMove(request.position);

        //compute current game state (is not really necessary but why not)
        GameState gameState = board.getGameState();

        return new MoveResponse(board.getBoard(), request.position, gameState);
    }
}
