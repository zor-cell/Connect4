package connect4;

import connect4.data.*;
import connect4.data.requests.SolverRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solver extends Thread {
    private final SolverRequest config;
    private final int rows;
    private final int cols;
    private long startTime;

    private BestMove prevBestMove;
    private BestMove bestMove;

    public Solver(SolverRequest config) {
        this.config = config;
        this.rows = config.board.length;
        this.cols = config.board[0].length;
    }

    public static void main(String[] args) {
        int[][] board = {
               //0, 1, 2, 3, 4, 5, 6
                {0, 0, 0, 0, 0, 0, 0},   //0
                {0, 0, 0, 0, 0, 0, 0},   //1
                {0, 0, 0, 0, 0, 0, 0},   //2
                {0, 0, 0, 0, 0, 0, 0},   //3
                {0, 0, 0, 0, 0, 0, 0}, //4
                {0, 0, 0, 1, 0, 0, 0},   //5
               //0, 1, 2, 3, 4, 5, 6
        };

        //with time = 5000, depth = 11
        SolverRequest request = new SolverRequest(board, -1, 5000, -1, 0, Version.V1_0);
        BestMove bestMove = startSolver(request);
        System.out.println(bestMove);
    }

    public static BestMove startSolver(SolverRequest request) {
        Solver solverThread = new Solver(request);
        solverThread.start();

        try {
            solverThread.join();
        } catch(InterruptedException e) {
            System.out.println("Main Thread interrupted");
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
            startTime = System.currentTimeMillis();

            //copy board instance because on interrupt, board can be in any state
            int[][] copy = new int[rows][cols];
            for(int i = 0;i < rows;i++) {
                System.arraycopy(config.board[i], 0, copy[i], 0, cols);
            }

            //iterative deepening
            int maxDepth = config.maxDepth >= 1 ? config.maxDepth : 42;
            for(depth = 1;depth <= maxDepth;depth++) {
                //the best move score is > 0 when favorable for config.player (no matter which player)
                prevBestMove = negamax(copy, depth, config.player, Integer.MIN_VALUE, Integer.MAX_VALUE);

                bestMove = prevBestMove;
            }

            System.out.println("Solver Thread finished!");
        } catch(InterruptedException e) {
            System.out.println("Solver Thread interrupted. Reached depth " + (depth - 1) + "!");
        }
    }

    private BestMove negamax(int[][] board, int depth, int player, int alpha, int beta) throws InterruptedException {
        //break out of computation when max thinking time is surpassed
        if(config.maxTime >= 0 && System.currentTimeMillis() - startTime > config.maxTime) {
            throw new InterruptedException();
        }

        //check if player can easily win on next move and instantly make move
        Position winningMove = canWinNextMove(board, player);
        if(winningMove != null) {
            return new BestMove(winningMove.j, Scores.WIN, 0);
        }

        //check if search or game is over
        GameState gameState = getGameState(board);
        if(depth == 0 || gameState != GameState.RUNNING) {
            int score = player * heuristics(board);
            return new BestMove(null, score);
        }

        //go through children positions
        BestMove bestMove = new BestMove(null, Integer.MIN_VALUE, -1);
        List<Position> moves = getPossibleMoves(board);
        for(Position move : moves) {
            makeMove(board, move, player);
            BestMove child = negamax(board, depth - 1, -player, invert(beta), invert(alpha));
            unmakeMove(board, move);

            int score = -child.score;
            int winDistance = child.winDistance;

            //update best move
            if(score > bestMove.score) { //TODO: add randomness on equality
                bestMove.move = move.j;
                bestMove.score = score;
                if(winDistance >= 0 && (score >= Scores.WIN || score <= -Scores.WIN)) {
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

    //separate function due to asymmetric bounds
    private int invert(int a) {
        if(a == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        } else if(a == Integer.MAX_VALUE) {
            return Integer.MIN_VALUE;
        }

        return -a;
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
        int pieces = 0;
        for(int i = 0;i < rows;i++) {
            for(int j = 0;j < cols;j++) {
                if(board[i][j] != 0) {
                    pieces++;
                }
            }
        }

        int score = 0;
        for(int player : new int[] {-1, 1}) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    //direction is important since no empty can be below v, dl and dr
                    int[][] dirs = {
                            {-1, 0}, //vertical v
                            {0, 1}, //horizontal h
                            {-1, -1}, //diagonal left dl
                            {-1, 1} //diagonal right dr
                    };

                    for (int[] dir : dirs) {
                        int countPlayer = 0;
                        int countEmpty = 0;
                        for (int k = 0; k <= 3; k++) {
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
                            //3 in row with 1 empty somewhere between
                            score += player * Scores.THREE_IN_ROW;
                        } else if(countPlayer == 2 && countEmpty == 2) {
                            //2 in row with 2 empties somewhere between
                            score += player * Scores.TWO_IN_ROW;
                        }
                    }
                }
            }
        }

        int t = rows * cols - pieces;
        return score;
    }

    public static GameState getGameState(int[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        //check for win
        int[][] dirs = {
                {1, 0}, //vertical
                {0, 1}, //horizontal
                {1, 1}, //diagonal down
                {1, -1} //diagonal up
        };

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cur = board[i][j];
                if(cur == 0) continue;

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
            Position pos = getMoveFromCol(board, j);
            if(pos == null) continue;
            positions.add(pos);
        }

        return positions;
    }

    public static Position getMoveFromCol(int[][] board, int col) {
        int i = board.length - 1;
        while(i >= 0 && board[i][col] != 0) {
            i--;
        }
        if(i >= 0) {
            return new Position(i, col);
        }

        return null;
    }

    public static void makeMove(int[][] board, Position move, int player) {
        board[move.i][move.j] = player;
    }

    public static void unmakeMove(int[][] board, Position move) {
        board[move.i][move.j] = 0;
    }
}