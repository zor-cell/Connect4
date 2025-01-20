package connect4;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Solver implements Runnable {
    private int[][] initialBoard;
    private int player;

    public Solver(int[][] board, int player) {
        this.initialBoard = board;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(4000);
        } catch(InterruptedException e) {
            System.out.println("solver interrupted");
        }
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

        //find best move in separate thread
        Runnable solver = new Solver(board, 1);
        Thread solverThread = new Thread(solver);
        solverThread.start();

        //stop finding best move after time limit
        ScheduledExecutorService schedulerService = Executors.newSingleThreadScheduledExecutor();
        schedulerService.schedule(solverThread::interrupt, 2000, TimeUnit.MILLISECONDS);
        schedulerService.shutdown();
    }
}
