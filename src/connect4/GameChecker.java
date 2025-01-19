package connect4;

import connect4.data.GameState;
import connect4.data.MovePayload;
import connect4.data.Position;

public class GameChecker {
    private final int[][] board;
    private final int rows;
    private final int cols;

    public GameChecker(int[][] board) {
        this.board = board;
        this.rows = this.board.length;
        this.cols = this.board[0].length;
    }

    public static MovePayload createMovePayload(int[][] board, int player, int j) {
        GameChecker gameChecker = new GameChecker(board);

        //make move
        Position move = gameChecker.makeMoveInCol(j, player);
        if(move == null) {
            //invalid move
            return new MovePayload(gameChecker.board, null, GameState.RUNNING);
        }

        //check game state with the made move
        GameState gameState = gameChecker.getGameState();

        return new MovePayload(gameChecker.board, move, gameState);
    }

    public GameState getGameState() {
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

    public Position makeMoveInCol(int col, int player) {
        Position position = getMoveFromCol(col);
        if(position == null) return null;

        board[position.i][position.j] = player;

        return position;
    }

    private Position getMoveFromCol(int col) {
        int i = rows - 1;
        while(i >= 0 && board[i][col] != 0) {
            i--;
        }
        if(i >= 0) {
            return new Position(i, col);
        }

        return null;
    }
}
