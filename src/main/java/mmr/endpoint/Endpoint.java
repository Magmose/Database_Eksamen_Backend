package mmr.endpoint;

import com.google.gson.Gson;
import mmr.dto.Movie;
import mmr.dto.redis.RedisUser;
import mmr.dto.request.RequestBodyMovie;
import mmr.neo4j.Neo4j;
import mmr.redis.Redis;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Endpoint {
    private Neo4j nj;
    private Redis redis;
    private Gson gson;

    public Endpoint() {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "123";
        nj = new Neo4j(uri, user, password);
        redis = new Redis("localhost", 6379);
        gson = new Gson();
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        //TODO Evt. return beans fra vores databaser
        return "Test Response from: " + name;
    }

    @PostMapping(path ="/like",consumes = "application/json", produces = "application/json")
    public String userLikesMovie(@RequestBody RequestBodyMovie requestBodyMovie) {
        nj.userLikesMovie(requestBodyMovie.getUserid(), requestBodyMovie.getMovie());
        redis.incDay(requestBodyMovie.getMovie());
        return gson.toJson(requestBodyMovie);
    }

    @GetMapping(path ="/movie/top/today", produces = "application/json")
    public String getTopMovieToday() {
        return gson.toJson(redis.getTopOverall());
    }
    @GetMapping(path ="/movie/top/week", produces = "application/json")
    public String getTopMovieWeek() {
        return gson.toJson(redis.getTopWeek());
    }
    @GetMapping(path ="/movie/top/month", produces = "application/json")
    public String getTopMovieMonth() {
        return gson.toJson(redis.getTopMonth());
    }
    @GetMapping(path ="/movie/top/overall", produces = "application/json")
    public String getTopMovieOverall() {
        return gson.toJson(redis.getTopOverall());
    }

    @GetMapping(path ="/user/top/followed", produces = "application/json")
    public String getTopUserFollowOverall() {
        return gson.toJson(redis.getTopFollowed());
    }

    @GetMapping(path ="/user/top/update",produces = "application/json")
    public String updateUserFollowOverall(){
        List<RedisUser> users = nj.getTopFollowed();
        redis.addListTopFollowed(users);
        return gson.toJson(users);
    }

    @GetMapping(path ="/getAllMovies")
    public ArrayList<Movie> getMovies() {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "123";
        Neo4j nj = new Neo4j(uri, user, password);

        try {

            return nj.getAllMovies();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            nj.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
