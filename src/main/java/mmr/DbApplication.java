package mmr;

import mmr.noe4j.Neo4j;
import mmr.redis.Redis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DbApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbApplication.class, args);
        Neo4j neo4j = new Neo4j();
        Redis redis = new Redis();



    }

}
