package mmr;

import mmr.neo4j.Neo4j;
import mmr.redis.Redis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Tuple;

import java.util.Set;

@SpringBootApplication
public class DbApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbApplication.class, args);

        Redis redis = new Redis();
        redis.fulshDB();
          redis.randomData("test");
        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("Kage patrolien");
        redis.incDay("Kage patrolien");
        Set<Tuple> response = redis.getTopWeek();
        response.forEach(k-> System.out.println(k));

    }

}
