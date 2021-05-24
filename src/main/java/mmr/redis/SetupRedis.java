package mmr.redis;

import redis.clients.jedis.Tuple;

import java.util.Set;

public class SetupRedis {
    public static void main(String[] args) {
        Redis redis = new Redis("localhost", 6379);
        redis.fulshDB();

        redis.randomData("test");

        System.out.println("________ Week");

        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("Kage patrolien");
        redis.incDay("Kage patrolien");
        Set<Tuple> response = redis.getTopWeek();
        response.forEach(k -> System.out.println(k));

        System.out.println("________ Today");

        Set<Tuple> response1 = redis.getTopToday();
        response1.forEach(x -> System.out.println(x));

        System.out.println("________ addTopUser");

        redis.addTopFollowed("123ID", "MangusTester", 5);
        redis.addTopFollowed("1231233ID", "Kage", 4);
        redis.addTopFollowed("1223453ID", "Ost", 3);
        redis.addTopFollowed("1235345ID", "Syltetøj", 2);
        redis.addTopFollowed("123ID", "MangusTester", 199);
        Set<Tuple> response2 = redis.getTopFollowed();
        response2.forEach(y -> System.out.println(y));
    }
}
