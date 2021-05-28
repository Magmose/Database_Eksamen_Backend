package mmr.dto;

public class SimilarityPair {
    int userid1;
    int userid2;
    double score;


    public SimilarityPair(int userid1, int userid2, double score) {
        this.userid1 = userid1;
        this.userid2 = userid2;
        this.score = score;
    }

    public int getUserid1() {
        return userid1;
    }

    public void setUserid1(int userid1) {
        this.userid1 = userid1;
    }

    public int getUserid2() {
        return userid2;
    }

    public void setUserid2(int userid2) {
        this.userid2 = userid2;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SimilarityPair{");
        sb.append("userid1='").append(userid1).append('\'');
        sb.append(", userid2='").append(userid2).append('\'');
        sb.append(", score=").append(score);
        sb.append('}');
        return sb.toString();
    }


    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
