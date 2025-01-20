package connect4;

import connect4.data.BestMove;
import connect4.data.GameState;
import connect4.data.Position;
import connect4.data.SolverConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Solver extends Thread {
    private SolverConfig config;
    private final int rows;
    private final int cols;

    private BestMove prevBestMove;
    private BestMove bestMove;

    public Solver(SolverConfig config) {
        this.config = config;
        this.rows = config.board.length;
        this.cols = config.board[0].length;
    }

    public static void main(String[] args) {
        int[][] board = {
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 1, -1, 0, 0},
                {0, 0, 0, 1, -1, -1, 0},
        };

        SolverConfig config = new SolverConfig(board, 1, 500, 0);
        BestMove bestMove = startSolver(config);
        System.out.println(bestMove);
    }

    public static BestMove startSolver(SolverConfig config) {
        //find best move in separate thread
        Solver solverThread = new Solver(config);
        solverThread.start();

        //stop finding best move after time limit
        ScheduledExecutorService schedulerService = Executors.newSingleThreadScheduledExecutor();
        schedulerService.schedule(solverThread::interrupt, config.maxTime, TimeUnit.MILLISECONDS);

        try {
            solverThread.join();
        } catch(InterruptedException e) {
            System.out.println("Main Thread interrupted");
        } finally {
            schedulerService.shutdownNow();
        }

        return solverThread.getBestMove();
    }

    public BestMove getBestMove() {
        return bestMove;
    }

    @Override
    public void run() {
        int depth = 1;
        try {
            //iterative deepening
            for(depth = 1;depth <= 9;depth++) {
                prevBestMove = negamax(config.board, depth, config.player, Integer.MIN_VALUE, Integer.MAX_VALUE);
                System.out.println(prevBestMove);

                bestMove = prevBestMove;
            }
            //bestMove = negamax(config.board, 2, config.player, Integer.MIN_VALUE, Integer.MAX_VALUE);
            System.out.println("Solver Thread finished normally");
        } catch(InterruptedException e) {
            System.out.println("Solver Thread interrupted. Reached depth " + depth);
        }
    }

    private BestMove negamax(int[][] board, int depth, int player, int alpha, int beta) throws InterruptedException {
        //break out of computation of thread interrupt
        if(this.isInterrupted()) {
            throw new InterruptedException();
        }

        //check if search or game is over
        GameState gameState = getGameState(board);
        if(depth == 0 || gameState != GameState.RUNNING) {
            int score = player * heuristics(board);
            return new BestMove(null, score);
        }

        //check if player can easily win on next move
        Position winningMove = canWinNextMove(board, player);
        if(winningMove != null) {
            return new BestMove(winningMove, player * 100000);
        }

        BestMove bestMove = new BestMove(null, Integer.MIN_VALUE);

        List<Position> moves = getPossibleMoves(board);
        for(Position move : moves) {
            makeMove(board, move, player);
            int score = -negamax(board, depth - 1, -player, -beta, -alpha).score;
            unmakeMove(board, move);

            //update best move
            if(score > bestMove.score) { //add randomness on equality
                bestMove.position = move;
                bestMove.score = score;
            }

            //alpha-beta pruning
            alpha = Math.max(alpha, score);
            if(alpha >= beta) {
                break;
            }
        }

        return bestMove;
    }

    private Position canWinNextMove(int[][] board, int player) {
        for(int j = 0;j < cols;j++) {
            Position move = getMoveFromCol(board, j);
            if(move == null) continue;

            makeMove(board, move, player);
            GameState gameState = getGameState(board);
            unmakeMove(board, move);

            if(player == 1 && gameState == GameState.PLAYER1) {
                return move;
            } else if(player == -1 && gameState == GameState.PLAYER2) {
                return move;
            }
        }

        return null;
    }

    private int heuristics(int[][] board) {
        int score = 0;
        for(int player : new int[] {-1, 1}) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (board[i][j] != player) continue;

                    int[][] dirs = {
                            {1, 0}, //vertical
                            {0, 1}, //horizontal
                            {1, 1}, //diagonal down
                            {1, -1} //diagonal up
                    };

                    for (int[] dir : dirs) {
                        int countPlayer = 1;
                        int countEmpty = 0;
                        for (int k = 1; k < 4; k++) {
                            Position next = new Position(i + dir[0] * k, j + dir[1] * k);
                            if(!next.isInBounds(rows, cols)) break;

                            if(board[next.i][next.j] == player) {
                                countPlayer++;
                            } else if(board[next.i][next.j] == 0) {
                                countEmpty++;
                            }
                        }

                        if(countPlayer == 4) {
                            //4 in a row
                            score += player * 1000;
                        } else if(countPlayer == 3 && countEmpty == 1) {
                            //3 in row with one empty somewhere between
                            score += player * 10;
                        }
                    }
                }
            }
        }

        //cap score to 1000 and -1000
        return Math.max(Math.min(score, 1000), -1000);
    }

    private GameState getGameState(int[][] board) {
        //check for win
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cur = board[i][j];
                if(cur == 0) continue;

                int[][] dirs = {
                        {1, 0}, //vertical
                        {0, 1}, //horizontal
                        {1, 1}, //diagonal down
                        {1, -1} //diagonal up
                };

                for (int[] dir : dirs) {
                    //check 4 in a row
                    int countPlayer = 1;
                    for (int k = 1; k < 4; k++) {
                        Position next = new Position(i + dir[0] * k, j + dir[1] * k);
                        if(!next.isInBounds(rows, cols)) break;

                        if(board[next.i][next.j] == cur) {
                            countPlayer++;
                        }
                    }

                    if(countPlayer == 4) {
                        return cur == 1 ? GameState.PLAYER1 : GameState.PLAYER2;
                    }
                }
            }
        }

        //check for draw
        boolean draw = true;
        for(int j = 0;j < cols;j++) {
            if(board[0][j] == 0) {
                draw = false;
                break;
            }
        }
        if(draw) return GameState.DRAW;

        return GameState.RUNNING;
    }

    private List<Position> getPossibleMoves(int[][] board) {
        List<Position> positions = new ArrayList<>();

        //order columns for better pruning
        Integer[] orderedCols = {3, 2, 4, 1, 5, 0, 6}; //todo: adjust for dynamic columns
        //search best move from previous iteration first
        if(prevBestMove != null) Arrays.sort(orderedCols, (a, b) -> {
            if(a.equals(prevBestMove.position.j)) return -1;
            if(b.equals(prevBestMove.position.j)) return 1;

            return 0;
        });

        //add valid moves
        for(int j : orderedCols) {
            Position pos = getMoveFromCol(board, j);
            if(pos == null) continue;
            positions.add(pos);
        }
        return positions;
    }

    private Position getMoveFromCol(int[][] board, int col) {
        int i = rows - 1;
        while(i >= 0 && board[i][col] != 0) {
            i--;
        }
        if(i >= 0) {
            return new Position(i, col);
        }

        return null;
    }

    private void makeMove(int[][] board, Position move, int player) {
        board[move.i][move.j] = player;
    }

    private void unmakeMove(int[][] board, Position move) {
        board[move.i][move.j] = 0;
    }
}