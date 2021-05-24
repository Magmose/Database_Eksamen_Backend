package mmr.endpoint;

import com.google.gson.Gson;
import mmr.dto.Movie;
import mmr.neo4j.Neo4j;
import mmr.redis.Redis;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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

    @PostMapping("/like")
    @ResponseStatus(HttpStatus.OK)
    public String userLikesMovie(int userid, String movie) {
        nj.userLikesMovie(userid, movie);
        redis.incDay(movie);
        return "Succes";
    }

    @GetMapping("/movie/top/today")
    public String getTopMovieToday() {
        return gson.toJson(redis.getTopOverall());
    }
    @GetMapping("/movie/top/week")
    public String getTopMovieWeek() {
        return gson.toJson(redis.getTopWeek());
    }
    @GetMapping("/movie/top/month")
    public String getTopMovieMonth() {
        return gson.toJson(redis.getTopMonth());
    }
    @GetMapping("/movie/top/overall")
    public String getTopMovieOverall() {
        return gson.toJson(redis.getTopOverall());
    }

    @GetMapping("/user/top/followed")
    public String getTopUserFollowOverall() {
        return gson.toJson(redis.getTopFollowed());
    }

    @GetMapping("/getAllMovies")
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
