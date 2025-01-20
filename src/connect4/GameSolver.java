package connect4;

import connect4.data.BestMove;
import connect4.data.GameState;
import connect4.data.MovePayload;
import connect4.data.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameSolver {
    private final int rows;
    private final int cols;

    //.  .  .  .  .  .  .
    //5 12 19 26 33 40 47
    //4 11 18 25 32 39 46
    //3 10 17 24 31 38 45
    //2  9 16 23 30 37 44
    //1  8 15 22 29 36 43
    //0  7 14 21 28 35 42
    private long bitboard = 0;

    public GameSolver(int[][] board) {
        rows = board.length;
        cols = board[0].length;
    }

    public static void main(String[] args) {
        int[][] board = {
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0, -1, 0},
                {0, 0, 1, 1, -1, -1, 0},
        };
    }

    public static MovePayload findBestMove(int[][] board, int player) {
        GameSolver solver = new GameSolver(board);

        //has to be called in extra thread to be able to stop execution
        BestMove bestMove = solver.negamax(board, 6, player);
        board[bestMove.position.i][bestMove.position.j] = player;

        System.out.println(bestMove);

        return new MovePayload(board, bestMove.position, GameState.RUNNING, bestMove.score);
    }

    public BestMove negamax(int[][] board, int depth, int player) {
        GameState gameState = getGameState(board); //improve game state getting
        if(depth == 0 || gameState != GameState.RUNNING) {
            int score = player * heuristics(board);
            return new BestMove(null, score);
        }

        BestMove bestMove = new BestMove(null, Integer.MIN_VALUE);

        List<Position> moves = getPossiblePositions(board);
        for(Position move : moves) {
            makeMove(board, move, player);
            int score = -negamax(board, depth - 1, -player).score;
            unmakeMove(board, move);

            if(score > bestMove.score) { //add randomness on equality
                bestMove.position = move;
                bestMove.score = score;
            }
        }

        return bestMove;
    }

    private void makeMove(int[][] board, Position move, int player) {
        board[move.i][move.j] = player;
    }

    private void unmakeMove(int[][] board, Position move) {
        board[move.i][move.j] = 0;
    }

    public int heuristics(int[][] board) {
        int score = 0;
        for(int player : new int[] {-1, 1}) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    int cur = board[i][j];
                    if (cur != player) continue;

                    int[][] dirs = {
                            {1, 0}, //vertical
                            {0, 1}, //horizontal
                            {1, 1}, //diagonal down
                            {1, -1} //diagonal up
                    };

                    for (int[] dir : dirs) {
                        //check 4 in row
                        boolean fourInRow = true;
                        for (int k = 1; k < 4; k++) {
                            Position next = new Position(i + dir[0] * k, j + dir[1] * k);
                            if (!next.isInBounds(rows, cols) || board[next.i][next.j] != cur) {
                                fourInRow = false;
                                break;
                            }
                        }
                        if (fourInRow) score += player * 1000;

                        //check 3 in row (with empty after)
                        boolean threeInRow = true;
                        for (int k = 1; k < 3; k++) {
                            Position next = new Position(i + dir[0] * k, j + dir[1] * k);
                            if (!next.isInBounds(rows, cols) || board[next.i][next.j] != cur) {
                                threeInRow = false;
                                break;
                            }
                        }

                        if(threeInRow) {
                            //check first for empty
                            Position first = new Position(i + dir[0] * -1, j + dir[1] * -1);
                            boolean firstIsEmpty = first.isInBounds(rows, cols) && board[first.i][first.j] == 0;
                            if (firstIsEmpty) score += player * 10;

                            //check last for empty
                            Position last = new Position(i + dir[0] * 3, j + dir[1] * 3);
                            boolean lastIsEmpty = last.isInBounds(rows, cols) && board[last.i][last.j] == 0;
                            if (lastIsEmpty) score += player * 10;
                        }
                    }
                }
            }
        }

        return Math.min(score, 1000); //cap score to 1000
    }


    public List<Position> getPossiblePositions(int[][] board) {
        //add move ordering

        List<Position> positions = new ArrayList<>();
        for(int j = 0;j < cols;j++) {
            Position pos = getMoveFromCol(board, j);
            if(pos == null) continue;
            positions.add(pos);
        }
        return positions;
    }

    public Position makeMoveInCol(int[][] board, int col, int player) {
        Position position = getMoveFromCol(board, col);
        if(position == null) return null;

        board[position.i][position.j] = player;

        return position;
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

    public GameState getGameState(int[][] board) {
        //check for draw
        boolean draw = true;
        for(int j = 0;j < cols;j++) {
            if(board[0][j] == 0) {
                draw = false;
                break;
            }
        }
        if(draw) return GameState.DRAW;

        //check for winner
        for(int i = 0;i < rows;i++) {
            for(int j = 0;j < cols;j++) {
                int cur = board[i][j];
                if(cur == 0) continue;

                //vertical
                boolean ver = true;
                for(int k = 1;k < 4;k++) {
                    Position next = new Position(i + k, j);
                    if(!next.isInBounds(rows, cols)) {
                        ver = false;
                        break;
                    }

                    if(board[next.i][next.j] != cur) {
                        ver = false;
                        break;
                    }
                }
                if(ver) return cur == 1 ? GameState.PLAYER1 : GameState.PLAYER2;

                //horizontal
                boolean hor = true;
                for(int k = 1;k < 4;k++) {
                    Position next = new Position(i, j + k);
                    if(!next.isInBounds(rows, cols)) {
                        hor = false;
                        break;
                    }

                    if(board[next.i][next.j] != cur) {
                        hor = false;
                        break;
                    }
                }
                if(hor) return cur == 1 ? GameState.PLAYER1 : GameState.PLAYER2;


                //diagonal down
                boolean ddia = true;
                for(int k = 1;k < 4;k++) {
                    Position next = new Position(i + k, j + k);
                    if(!next.isInBounds(rows, cols)) {
                        ddia = false;
                        break;
                    }

                    if(board[next.i][next.j] != cur) {
                        ddia = false;
                        break;
                    }
                }
                if(ddia) return cur == 1 ? GameState.PLAYER1 : GameState.PLAYER2;

                //right dia
                boolean udia = true;
                for(int k = 1;k < 4;k++) {
                    Position next = new Position(i - k, j + k);
                    if(!next.isInBounds(rows, cols)) {
                        udia = false;
                        break;
                    }

                    if(board[next.i][next.j] != cur) {
                        udia = false;
                        break;
                    }
                }
                if(udia) return cur == 1 ? GameState.PLAYER1 : GameState.PLAYER2;
            }
        }

        return GameState.RUNNING;
    }
}
