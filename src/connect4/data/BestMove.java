package connect4.data;

public class BestMove {
    public Position position;
    public int score;

    public BestMove(Position position, int score) {
        this.position = position;
        this.score = score;
    }
}
