package connect4.data;

public class BestMove {
    public Position position;
    public int score;
    public int winDistance;

    public BestMove(Position position, int score) {
        this(position, score, -1);
    }

    public BestMove(Position position, int score, int winDistance) {
        this.position = position;
        this.score = score;
        this.winDistance = winDistance;
    }

    @Override
    public String toString() {
        return "BestMove{" +
                "position=" + position +
                ", score=" + score +
                ", winDistance=" + winDistance +
                '}';
    }
}
