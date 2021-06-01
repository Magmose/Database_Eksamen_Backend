package mmr;

import mmr.dto.redis.RedisUser;
import mmr.endpoint.Endpoint;
import mmr.neo4j.Neo4j;
import mmr.neo4j.SetupNeo4j;
import mmr.postgres.UserDBImpl;
import mmr.redis.RedisSession;
import mmr.redis.RedisStats;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class DbApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DbApplication.class, args);


        // Setting connecting to databases and setting up data
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "123";
        Neo4j nj = new Neo4j(uri, user, password);
        SetupNeo4j.setup(nj);

        RedisStats redisStats = new RedisStats("localhost", 6379);
        RedisSession redisSession = new RedisSession("localhost", 6379);

        redisStats.fulshDB();
        redisStats.randomMovieData();
        List<RedisUser> users = nj.getTopFollowed();
        redisStats.addListTopFollowed(users);

        String url = "jdbc:postgresql://localhost/movieusers";
        String userPass = "softdb";
        UserDBImpl postgress = new UserDBImpl(url, userPass, userPass);
        postgress.setupData();



        Endpoint endpoint = new Endpoint(nj, redisStats, redisSession, postgress);


    }

}
