package connect4.data;

public class BestMove {
    public Integer move;
    public int score;
    public int winDistance;

    public BestMove(Integer move, int score) {
        this(move, score, -1);
    }

    public BestMove(Integer move, int score, int winDistance) {
        this.move = move;
        this.score = score;
        this.winDistance = winDistance;
    }

    @Override
    public String toString() {
        return "BestMove{" +
                "move=" + move +
                ", score=" + score +
                ", winDistance=" + winDistance +
                '}';
    }
}
