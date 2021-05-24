package mmr.dto.redis;

public class RedisMovie {
    private double score;
    private String title;

    public RedisMovie() {
    }

    public RedisMovie(double score, String title) {
        this.score = score;
        this.title = title;
    }

    public double getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "RedisMovie{" +
                "score=" + score +
                ", title='" + title + '\'' +
                '}';
    }
}
