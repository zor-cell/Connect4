package connect4;

import connect4.data.*;
import connect4.data.requests.MoveRequest;
import connect4.data.requests.SolverRequest;
import connect4.data.requests.UndoRequest;

public class Connector {
    public static MovePayload makeBestMove(SolverRequest config) {
        BestMove bestMove = Solver.startSolver(config);
        config.board[bestMove.position.i][bestMove.position.j] = config.player;

        return new MovePayload(config.board, bestMove.position, GameState.RUNNING);
    }

    public static MovePayload makeMove(MoveRequest request) {
        return GameChecker.makeMove(request.board, request.player, request.position.j);
    }

    public static MovePayload undoMove(UndoRequest request) {
        request.board[request.position.i][request.position.j] = 0;

        return new MovePayload(request.board, request.position, GameState.RUNNING);
    }
}
