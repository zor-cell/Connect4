package connect4;

import connect4.data.BestMove;
import connect4.data.GameState;
import connect4.data.Position;
import connect4.data.Scores;
import connect4.data.requests.SolverRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolverBitboard extends Thread {
    private final SolverRequest config;
    private long startTime;
    private int nodesVisited = 0;

    private BestMove prevBestMove;
    private BestMove bestMove;

    public SolverBitboard(SolverRequest config) {
        this.config = config;
    }

    public static void main(String[] args) {
        int[][] board = {
               //0, 1, 2, 3, 4, 5, 6
                {0, 0, 0, 0, 0, 0, 0},   //0
                {0, 0, 0, 0, 0, 0, 0},   //1
                {0, 0, 0, 0, 0, 0, 0},   //2
                {0, 0, 0, -1, 0, 0, 0},   //3
                {0, 0, 0, -1, 0, 0, 0}, //4
                {0, 0, 0, 1, 1, 0, 0},   //5
               //0, 1, 2, 3, 4, 5, 6
        };

        //with time = 5000, depth = 11
        SolverRequest request = new SolverRequest(board, 1, 5000, -1);
        BestMove bestMove = startSolver(request);
        System.out.println(bestMove);
    }

    public static BestMove startSolver(SolverRequest request) {
        SolverBitboard solverThread = new SolverBitboard(request);
        solverThread.start();

        try {
            solverThread.join();
        } catch(InterruptedException e) {
            System.out.println("Main Thread interrupted");
        }
        System.out.println("Nodes visited: " + solverThread.nodesVisited);

        return solverThread.getBestMove();
    }

    public BestMove getBestMove() {
        return bestMove;
    }

    @Override
    public void run() {
        int depth = 1;
        try {
            startTime = System.currentTimeMillis();

            //copy board instance because on interrupt, board can be in any state
            Bitboard bitboard = new Bitboard(config.board, config.player);

            //iterative deepening
            int maxDepth = config.maxDepth >= 1 ? config.maxDepth : 42;
            for(depth = 1;depth <= maxDepth;depth++) {
                //the best move score is > 0 when favorable for config.player (no matter which player)
                prevBestMove = negamax(bitboard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

                bestMove = prevBestMove;
            }

            System.out.println("Solver Thread finished!");
        } catch(InterruptedException e) {
            System.out.println("Solver Thread interrupted. Reached depth " + (depth - 1) + "!");
        }
    }

    private BestMove negamax(Bitboard board, int depth, int alpha, int beta) throws InterruptedException {
        //break out of computation when max thinking time is surpassed
        if(config.maxTime >= 0 && System.currentTimeMillis() - startTime > config.maxTime) {
            throw new InterruptedException();
        }

        nodesVisited++;

        //check if player can easily win on next move and instantly make move
        for(int move = 0;move < board.cols;move++) {
            if(board.isWinningMove(move)) {
                return new BestMove(move, Scores.WIN - board.getMoves(), 0);
            }
        }

        //check if search or game is over
        GameState gameState = board.getGameState();
        if(depth == 0 || gameState != GameState.RUNNING) {
            int score = board.heuristics();
            return new BestMove(null, score);
        }

        //go through children positions
        BestMove bestMove = new BestMove(null, Integer.MIN_VALUE, -1);
        for(Integer move : getPossibleMoves(board)) {
            Bitboard moveBoard = new Bitboard(board);
            moveBoard.makeMove(move);

            BestMove child = negamax(moveBoard, depth - 1, invert(beta), invert(alpha));
            int score = -child.score;
            int winDistance = child.winDistance;

            //update best move
            if(score > bestMove.score) {
                bestMove.move = move;
                bestMove.score = score;
                if(winDistance >= 0 && (score >= Scores.WIN - 50 || score <= -Scores.WIN + 50)) {
                    bestMove.winDistance = winDistance + 1;
                }
            }

            //alpha-beta pruning
            alpha = Math.max(alpha, score);
            if(alpha >= beta) {
                break;
            }
        }

        return bestMove;
    }

    private List<Integer> getPossibleMoves(Bitboard board) {
        List<Integer> moves = new ArrayList<>();

        //order columns for better pruning
        Integer[] orderedCols = {3, 2, 4, 1, 5, 0, 6};

        //search best move from previous iteration first
        if(prevBestMove != null) {
            Arrays.sort(orderedCols, (a, b) -> {
                if(a.equals(prevBestMove.move)) return -1;
                if(b.equals(prevBestMove.move)) return 1;

                return 0;
            });

            //previous move should only be looked at in first iteration
            prevBestMove = null;
        }

        //add valid moves
        for(int j : orderedCols) {
            if(board.canMakeMove(j)) {
                moves.add(j);
            }
        }

        return moves;
    }

    //separate function due to asymmetric bounds
    private static int invert(int a) {
        if(a == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        } else if(a == Integer.MAX_VALUE) {
            return Integer.MIN_VALUE;
        }

        return -a;
    }
}