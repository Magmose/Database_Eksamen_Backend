package mmr.endpoint;

import com.google.gson.Gson;
import mmr.dto.Movie;
import mmr.dto.redis.RedisUser;
import mmr.dto.request.RequestBodyLogin;
import mmr.dto.request.RequestBodyMovie;
import mmr.neo4j.Neo4j;
import mmr.redis.RedisSession;
import mmr.redis.RedisStats;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Endpoint {
    private Neo4j nj;
    private RedisStats redisStats;
    private RedisSession redisSession;
    private Gson gson;

    public Endpoint() {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "123";
        nj = new Neo4j(uri, user, password);
        redisStats = new RedisStats("localhost", 6379);
        redisSession = new RedisSession("localhost", 6379);
        gson = new Gson();
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        //TODO Evt. return beans fra vores databaser
        return "Test Response from: " + name;
    }

    @PostMapping(path = "/like", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> userLikesMovie(@RequestBody RequestBodyMovie requestBodyMovie, @RequestHeader("sessionID") String sessionID) {
        if (redisSession.getSessionStatus(sessionID)) {
            nj.userLikesMovie(requestBodyMovie.getUserid(), requestBodyMovie.getMovie());
            redisStats.incDay(requestBodyMovie.getMovie());
            return new ResponseEntity<>(gson.toJson(requestBodyMovie), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"session\":\"UNAUTHORIZED\"} ", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/movie/top/today", produces = "application/json")
    public String getTopMovieToday() {
        return gson.toJson(redisStats.getTopOverall());
    }

    @GetMapping(path = "/movie/top/week", produces = "application/json")
    public String getTopMovieWeek() {
        return gson.toJson(redisStats.getTopWeek());
    }

    @GetMapping(path = "/movie/top/month", produces = "application/json")
    public String getTopMovieMonth() {
        return gson.toJson(redisStats.getTopMonth());
    }

    @GetMapping(path = "/movie/top/overall", produces = "application/json")
    public String getTopMovieOverall() {
        return gson.toJson(redisStats.getTopOverall());
    }

    @GetMapping(path = "/user/top/followed", produces = "application/json")
    public String getTopUserFollowOverall() {
        return gson.toJson(redisStats.getTopFollowed());
    }

    @GetMapping(path = "/user/top/update", produces = "application/json")
    public String updateUserFollowOverall() {
        List<RedisUser> users = nj.getTopFollowed();
        redisStats.addListTopFollowed(users);
        return gson.toJson(users);
    }

    @PostMapping(path = "/session/start", consumes = "application/json", produces = "application/json")
    public String startSession(@RequestBody RequestBodyLogin RequestBodyLogin) {
        //Hent data fra postgresss
        Map<String, String> response = new HashMap<String, String>();
        String sessionID = redisSession.startSession(response);
        return "{\"sessionID\":\"" + sessionID + "\"}";
    }

    @GetMapping(path = "/session", produces = "application/json")
    public ResponseEntity<String> getSession(@RequestHeader("sessionID") String sessionID) {
        if (redisSession.getSessionStatus(sessionID)) {
            return new ResponseEntity<>(gson.toJson(redisSession.getSessionData(sessionID)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"session\":\"UNAUTHORIZED\"} ", HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping(path = "/getAllMovies")
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
