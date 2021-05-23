package mmr.dto;

import java.util.List;

public class Movie {
    private String director,title,year;
    private double score;
    private int likes;
    private List<Actor> actors;

    public Movie(String director, String title, String year, double score, int likes, List<Actor> actors) {
        this.director = director;
        this.title = title;
        this.year = year;
        this.score = score;
        this.likes = likes;
        this.actors = actors;
    }

    public Movie() {
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }
}
