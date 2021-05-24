package mmr.redis;

import com.github.javafaker.Faker;
import mmr.dto.User;
import mmr.dto.redis.RedisMovie;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetupRedis {
    public static void main(String[] args) {
        Redis redis = new Redis("localhost", 6379);
        redis.fulshDB();

        redis.randomData("test");

        Faker faker = new Faker();

        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("Kage patrolien");
        redis.incDay("Kage patrolien");

       ;

        System.out.println("________ addTopUser");

        redis.addTopFollowed(new User("123I234D", "kageManden", 5));
        redis.addTopFollowed(new User("123ID", "MangusTester", 195436435));

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
        User user = new User("ID"+i,faker.name().fullName(),i);
        users.add(user);
        }

        redis.addListTopFollowed(users);

        Set<Tuple> response2 = redis.getTopFollowed(5);
        response2.forEach(y -> System.out.println(y));

        System.out.println("________ Today");

        List<RedisMovie> response1 = redis.getTopToday();
        response1.forEach(x -> System.out.println(x));

        System.out.println("________ Week");

        List<RedisMovie>response = redis.getTopWeek();
        response.forEach(k -> System.out.println(k));


        System.out.println("________ Month");

        List<RedisMovie> response3 = redis.getTopMonth();
        response3.forEach(x -> System.out.println(x));

        System.out.println("________ overAll");

        List<RedisMovie> response4 = redis.getTopOverall();
        response4.forEach(x -> System.out.println(x.toString()));


    }
}
