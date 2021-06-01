package mmr.redis;

import com.github.javafaker.Faker;
import mmr.dto.redis.RedisMovie;
import mmr.dto.redis.RedisUser;

import java.util.ArrayList;
import java.util.List;

public class SetupRedis {
    public static void main(String[] args) {
       //RedisSession redis = new RedisSession("localhost", 6379);
        RedisStats redis = new RedisStats("localhost", 6379);
        redis.fulshDB();
        redis.randomMovieData();
        redis.getTopMonth(10).forEach(k -> System.out.println(k));
        System.out.println("________________________");
        redis.getTopOverall(10).forEach(k -> System.out.println(k));
        //stats();


    }

    private static void stats() {
        RedisStats redis = new RedisStats("localhost", 6379);
        redis.fulshDB();

        redis.randomData("test");

        Faker faker = new Faker();

        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("Kage patrolien");
        redis.incDay("Kage patrolien");
        

        System.out.println("________ addTopUser");

        redis.addTopFollowed(new RedisUser("1", "Rasmus123", 5));
        redis.addTopFollowed(new RedisUser("2", "magmoz", 195436435));

        List<RedisUser> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            RedisUser user = new RedisUser("ID"+i,faker.name().fullName(),i);
        users.add(user);
        }

        redis.addListTopFollowed(users);

        List<RedisUser> response2 = redis.getTopFollowed(5);
        response2.forEach(y -> System.out.println(y.toString()));

        System.out.println("________ Today");

        List<RedisMovie> response1 = redis.getTopToday();
        response1.forEach(x -> System.out.println(x.toString()));

        System.out.println("________ Week");

        List<RedisMovie>response = redis.getTopWeek();
        response.forEach(k -> System.out.println(k.toString()));


        System.out.println("________ Month");

        List<RedisMovie> response3 = redis.getTopMonth();
        response3.forEach(x -> System.out.println(x.toString()));

        System.out.println("________ overAll");

        List<RedisMovie> response4 = redis.getTopOverall();
        response4.forEach(x -> System.out.println(x.toString()));
    }
}
