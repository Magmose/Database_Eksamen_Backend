package mmr.redis;

import mmr.dto.User;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetupRedis {
    public static void main(String[] args) {
        Redis redis = new Redis("localhost", 6379);
        redis.fulshDB();

        redis.randomData("test");


        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("YEEW the movie");
        redis.incDay("Kage patrolien");
        redis.incDay("Kage patrolien");

       ;

        System.out.println("________ addTopUser");

        redis.addTopFollowed(new User("123ID", "MangusTester", 5));
        redis.addTopFollowed(new User("1231233ID", "Kage", 4));
        redis.addTopFollowed(new User("1223453ID", "Ost", 3));
        redis.addTopFollowed(new User("1235345ID", "Syltetøj", 2));
        redis.addTopFollowed(new User("123ID", "MangusTester", 195436435));

        User user1 = new User("12312","kaj",312);
         User user2 = new User("12346557312","kasdgaj",412312);
         User user3 = new User("12356712","xekadsaj",13142312);
         User user4 = new User("123546712","ewqkaj",313122);
         User user5 = new User("12375612","asdkaj",63612);

         List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        redis.addListTopFollowed(users);

        Set<Tuple> response2 = redis.getTopFollowed();
        response2.forEach(y -> System.out.println(y));

        System.out.println("________ Today");

        Set<Tuple> response1 = redis.getTopToday();
        response1.forEach(x -> System.out.println(x));

        System.out.println("________ Week");

        Set<Tuple> response = redis.getTopWeek();
        response.forEach(k -> System.out.println(k));


        System.out.println("________ Month");

        Set<Tuple> response3 = redis.getTopMonth();
        response3.forEach(x -> System.out.println(x));

        System.out.println("________ overAll");

        Set<Tuple> response4 = redis.getTopOverall();
        response4.forEach(x -> System.out.println(x));


    }
}
