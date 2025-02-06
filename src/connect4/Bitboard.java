package connect4;

import connect4.data.GameState;
import connect4.data.Scores;

import java.util.Arrays;

public class Bitboard {
    public final int rows;
    public final int cols;

    private long currentPlayer = 0L;
    private long allPlayers = 0L;
    private int moves = 0;

    public Bitboard(Bitboard bitboard) {
        this.rows = bitboard.rows;
        this.cols = bitboard.cols;
        this.currentPlayer = bitboard.currentPlayer;
        this.allPlayers = bitboard.allPlayers;
    }

    public Bitboard(int[][] board, int player) {
        this.rows = board.length;
        this.cols = board[0].length;

        for(int i = 0;i < rows;i++) {
            for(int j = 0;j < cols;j++) {
                if(board[i][j] == 0) {
                    continue;
                }

                long pos = 1L << (rows - 1 - i) << (rows + 1) * j;
                if(board[i][j] == player) {
                    currentPlayer |= pos;
                }
                allPlayers |= pos;
            }
        }
    }

    public static void main(String[] args) {
        int[][] board = {
                //0, 1, 2, 3, 4, 5, 6
                {0, 0, 0, 0, 0, 0, 0},   //0
                {0, 0, 0, 0, 0, 0, 0},   //1
                {0, 0, 1, 1, 1, 0, 0},   //2
                {0, 0, 0, 1, 1, 0, 0},   //3
                {0, 0, 1, 1, 1, 0, 0}, //4
                {0, -1, 0, 0, -1, 0, 0},   //5
                //0, 1, 2, 3, 4, 5, 6
        };

        Bitboard bitboard = new Bitboard(board, 1);
        System.out.println(bitboard);

        System.out.println(bitboard.heuristics());

        /*int[] moves = {3, 3, 3};
        for(int move : moves) {
            System.out.println("Move: " + move);
            if(bitboard.canMakeMove(move)) {
                bitboard.makeMove(move);
                System.out.println(bitboard);
            }
        }

        System.out.println(bitboard.isWinning(bitboard.currentPlayer));*/
    }

    public boolean canMakeMove(int col) {
        return (allPlayers & topMask(col)) == 0;
    }

    public void makeMove(int col) {
        //switch player
        currentPlayer ^= allPlayers;

        //add new piece in column
        allPlayers |= allPlayers + bottomMask(col);

        moves++;
    }

    public boolean isWinningMove(int col) {
        long copy = currentPlayer;
        copy |= (allPlayers + bottomMask(col)) & columnMask(col); //why column mask???

        return isWinning(copy);
    }

    public boolean isWinning(long pos) {
        long m;

        //the bitshifts to compute 4-in-row
        int[] shifts = {
                rows + 1, //horizontal
                1, //vertical
                rows, //diagonal down
                rows + 2 //diagonal up
        };

        for(int shift : shifts) {
            m = pos & (pos >> shift); //makes sure to not count empty spaces between pieces
            if((m & (m >> 2 * shift)) > 0) {
                return true;
            }
        }

        return false;
    }


    public int heuristics() {
        if(isWinning(currentPlayer)) {
            return Scores.WIN;
        }
        int score = 0;

        int[] shifts = {
                rows + 1, //horizontal
                1, //vertical
                rows, //diagonal down
                rows + 2 //diagonal up
        };
        long pos = currentPlayer;
        for(int shift : shifts) {
            //detect 3 in a row
            long m = pos & (pos >> shift);
            m = m & (m >> shift);

            printLong(allPlayers);
            printLong(pos);
            printLong(m);
            //check if the 4th sport is empty in both directions

            //whenever negation is needed, all bits to the left of the 49th bit
            //should always be 0, since they are not used in the board (only important for right check)
            //also, the topmost row is a sentinel row, so it should also be discarded
            long filterMask = 0b000000000000000_0111111_0111111_0111111_0111111_0111111_0111111_0111111L;
            printLong(filterMask);

            //check left of 3 in row
            long left = (m >> shift) & ~allPlayers;
            left &= filterMask;

            printLong(left);

            //check right of 3 in row
            long right = (m << 3 * shift) & ~allPlayers;
            right &= filterMask;

            printLong(right);

            if(right > 0 || left > 0) {
                score += (Long.bitCount(right) + Long.bitCount(left));
            }
        }

        return score;
    }

    private void printLong(long a) {
        String str = String.format("%64s", Long.toBinaryString(a))
                .replace(' ', '0');
        System.out.println(str);
    }

    private void printArr(int[][] arr) {
        String res = "";
        for(int i = 0;i < arr.length;i++) {
            for(int j = 0;j < arr[0].length;j++) {
                res += arr[i][j];
            }
            res += "\n";
        }
        res += "\n";
        System.out.println(res);
    }

    public GameState getGameState() {
        if(moves == rows * cols) {
            return GameState.DRAW;
        }

        return GameState.RUNNING;
    }



    //a single 1 in the top cell of the given column
    private long topMask(int col) {
        return 1L << (rows - 1) << (rows + 1) * col;
    }

    //a single 1 in the bottom cell of the given column
    private long bottomMask(int col) {
        return 1L << (rows + 1) * col;
    }

    //a 1 in every cell of the given column
    private long columnMask(int col) {
        return ((1L << rows) - 1) << (rows + 1) * col;
    }



    public static int[][] boardFromBitboard(long bitboard) {
        String boardStr = String.format("%49s", Long.toBinaryString(bitboard))
                .replace(' ', '0');

        final int rows = 6;
        final int cols = 7;

        int[][] board = new int[rows + 1][cols];
        int i = 0, l = 0;
        for(int r = 0;r < boardStr.length();r++) {
            if(r > 0 && r % (rows + 1) == 0 || r == boardStr.length() - 1) {
                if(r == boardStr.length() - 1) r++;

                String sub = boardStr.substring(l, r);
                for(int j = 0;j < Math.min(sub.length(), cols);j++) {
                    if(i >= 7) break;
                    board[i][j] = sub.charAt(j) - '0';
                }

                i++;
                l = r;
            }
        }

        //board has wrong orientation
        return rotateCW(board);
    }

    @Override
    public int hashCode() {
        return (int) (currentPlayer + allPlayers);
    }

    @Override
    public String toString() {
        String res = "";

        int[][] currentBoardArr = boardFromBitboard(currentPlayer);
        res += "Current Board:\n";
        for(int i = 0;i < currentBoardArr.length;i++) {
            for(int j = 0;j < currentBoardArr[0].length;j++) {
                res += currentBoardArr[i][j];
            }
            res += "\n";
        }
        res += "\n";

        int[][] maskBoardArr = boardFromBitboard(allPlayers);
        res += "Mask Board:\n";
        for(int i = 0;i < maskBoardArr.length;i++) {
            for(int j = 0;j < maskBoardArr[0].length;j++) {
                res += maskBoardArr[i][j];
            }
            res += "\n";
        }
        res += "\n";


        return res;
    }

    static int[][] rotateCW(int[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        int[][] ret = new int[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M-1-r] = mat[r][c];
            }
        }
        return ret;
    }
}
