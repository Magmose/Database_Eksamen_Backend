package mmr.dto.request;

public class RequestBodyMovie {
    private String movie;

    public RequestBodyMovie() {
    }

    public RequestBodyMovie( String movie) {
        this.movie = movie;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }
}
