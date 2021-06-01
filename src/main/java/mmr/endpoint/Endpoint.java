package mmr.endpoint;

import com.google.gson.Gson;
import mmr.dto.Movie;
import mmr.dto.redis.RedisUser;
import mmr.dto.request.RequestBodyLogin;
import mmr.dto.request.RequestBodyMovie;
import mmr.neo4j.Neo4j;
import mmr.postgres.UserDBImpl;
import mmr.redis.RedisSession;
import mmr.redis.RedisStats;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class Endpoint {
    private Neo4j nj;
    private RedisStats redisStats;
    private RedisSession redisSession;
    private UserDBImpl postgress;
    private Gson gson;

    public Endpoint() {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "123";
        nj = new Neo4j(uri, user, password);

//
//        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();
//        jedisPoolConfig.setMaxTotal(100);
//        jedisPoolConfig.setMaxIdle(20);
//        jedisPoolConfig.setMinIdle(10);
//        jedisPoolConfig.setMaxWaitMillis(10000);
//        JedisPool jedisPool = new JedisPool(jedisPoolConfig,"localhost", 6379);
//        Jedis jedis = jedisPool.getResource();

        Jedis jedis = new Jedis("localhost", 6379);
        redisStats = new RedisStats(jedis);
        redisSession = new RedisSession(jedis);


        String url = "jdbc:postgresql://localhost/movieusers";
        String userPass = "softdb";
        postgress = new UserDBImpl(url, userPass, userPass);

        gson = new Gson();
    }



    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        //TODO Evt. return beans fra vores databaser
        return "Test Response from: " + name;
    }

    @PostMapping(path = "/like", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> userLikesMovie(@RequestBody RequestBodyMovie requestBodyMovie, @RequestHeader("sessionID") String sessionID) {
        System.out.println("Before: " + sessionID);

        Map<String, String> session = redisSession.getSessionData(sessionID);
        if (session == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        redisStats.incDay(requestBodyMovie.getMovie());
        nj.userLikesMovie(Integer.parseInt(session.get("id")), requestBodyMovie.getMovie());
        return new ResponseEntity<>(gson.toJson(requestBodyMovie), HttpStatus.OK);


    }

    @GetMapping(path = "/movie/top/today", produces = "application/json")
    public String getTopMovieToday() {
        return gson.toJson(redisStats.getTopToday());
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
        return gson.toJson(redisStats.getTopFollowed(3));
    }

    @GetMapping(path = "/user/top/update", produces = "application/json")
    public String updateUserFollowOverall() {
        List<RedisUser> users = nj.getTopFollowed();
        redisStats.addListTopFollowed(users);
        return gson.toJson(users);
    }

    @PostMapping(path = "/session/start", consumes = "application/json", produces = "application/json")
    public String startSession(@RequestBody RequestBodyLogin RequestBodyLogin) throws SQLException {
        Map<String, String> response = postgress.getUser(RequestBodyLogin.getUsername());

        for (Map.Entry<String, String> entry: response.entrySet()
             ) {
            System.out.println(entry.getKey() + " TEST " + entry.getValue() );
        }
        String sessionID = redisSession.startSession(response);
        return "{\"sessionID\":\"" + sessionID + "\"}";
    }

    @GetMapping(path = "/session", produces = "application/json")
    public ResponseEntity<String> getSession(@RequestHeader("sessionID") String sessionID) {
        if (redisSession.isSessionValid(sessionID)) {
            return new ResponseEntity<>(gson.toJson(redisSession.getSessionData(sessionID)), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping(path = "/getAllMovies")
    public ArrayList<Movie> getMovies() {
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


    @GetMapping(path = "/getUserRoleLog")
    public ArrayList<String> getUserRoleLog() {

        try {
            return postgress.getUserRoleLog();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

}
