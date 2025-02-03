package connect4;

import connect4.data.*;
import connect4.data.requests.MoveRequest;
import connect4.data.requests.SolverRequest;
import connect4.data.requests.UndoRequest;
import connect4.data.responses.MoveResponse;
import connect4.data.responses.SolverResponse;

public class Connector {
    public static SolverResponse makeBestMove(SolverRequest request) {
        //get best move
        BestMove bestMove = Solver.startSolver(request);

        //make best move
        Solver.makeMove(request.board, bestMove.position, request.player);

        //compute current game state
        GameState gameState = Solver.getGameState(request.board);

        System.out.println(bestMove);

        return new SolverResponse(request.board, gameState, bestMove);
    }

    public static MoveResponse makeMove(MoveRequest request) {
        //get move
        Position move = Solver.getMoveFromCol(request.board, request.position.j);
        //check for invalid move
        if(move == null) {
            return new MoveResponse(request.board, null, GameState.RUNNING);
        }

        //make valid move
        Solver.makeMove(request.board, move, request.player);

        //compute current game state
        GameState gameState = Solver.getGameState(request.board);

        return new MoveResponse(request.board, move, gameState);
    }

    public static MoveResponse undoMove(UndoRequest request) {
        //undo move
        Solver.unmakeMove(request.board, request.position);

        //compute current game state (is not really necessary but why not)
        GameState gameState = Solver.getGameState(request.board);

        return new MoveResponse(request.board, request.position, gameState);
    }
}
