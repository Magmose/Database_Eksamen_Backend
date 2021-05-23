package mmr.redis;

import redis.clients.jedis.Tuple;

import java.util.Set;

public class SetupRedis {
    public static void main(String[] args) {
        Redis redis = new Redis("localhost",6379);
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
