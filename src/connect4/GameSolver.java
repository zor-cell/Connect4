package connect4;

import connect4.data.BestMove;
import connect4.data.GameState;
import connect4.data.Position;

import java.util.ArrayList;
import java.util.List;

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

    public BestMove findBestMove(int[][] board) {
        return null;
    }

    public BestMove negamax(int[][] board, int depth, int player) {
        GameState gameState = getGameState(board);
        if(depth == 0 || gameState != GameState.RUNNING) {
            int score = player * heuristics(board);
        }

        int value = Integer.MIN_VALUE;
        List<Position> moves = getPossiblePositions(board);
        for(Position move : moves) {
            int[][] copy = copy(board);
            copy[move.i][move.j] = player;

            value = Math.max(value, -negamax(copy, depth - 1, -player).score);
        }
        return null;
        //return value;
    }

    int[][] copy(int[][] arr) {
        int[][] cpy = new int[arr.length][arr[0].length];
        for(int i = 0;i < arr.length;i++) {
            for(int j = 0;j < arr[0].length;j++) {
                cpy[i][j] = arr[i][j];
            }
        }
        return cpy;
    }

    public int heuristics(int[][] board) {
        return 0;
    }


    public List<Position> getPossiblePositions(int[][] board) {
        List<Position> positions = new ArrayList<>();
        for(int j = 0;j < cols;j++) {
            Position pos = getMoveFromCol(board, j);
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
