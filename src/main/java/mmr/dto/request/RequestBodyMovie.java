package mmr.dto.request;

public class RequestBodyMovie {
    private int userid;
    private String movie;

    public RequestBodyMovie() {
    }

    public RequestBodyMovie(int userid, String movie) {
        this.userid = userid;
        this.movie = movie;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }
}
