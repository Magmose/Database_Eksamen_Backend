package mmr.dto;

import java.util.List;

public class Person {
    private String name;
    private int born;
    private List<Movie> actedIn;

    public Person(String name, int born) {
        this.name = name;
        this.born = born;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBorn() {
        return born;
    }

    public void setBorn(int born) {
        this.born = born;
    }

    public List<Movie> getActedIn() {
        return actedIn;
    }

    public void setActedIn(List<Movie> actedIn) {
        this.actedIn = actedIn;
    }
}
