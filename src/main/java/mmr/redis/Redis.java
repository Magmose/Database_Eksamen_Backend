package mmr.redis;

import mmr.dto.redis.RedisMovie;
import mmr.dto.redis.RedisUser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Redis {


    // Hive alle ud
    // Fiks build data

    private Jedis jedis;
    private LocalDate currentdate;
    private final static int DEFAULT_OUTPUT_TOP = 3;

    public Redis(String host, int port) {
        currentdate = LocalDate.now();
        jedis = new Jedis(host, port);
    }

    public boolean incDay(String movie) {
        var response = jedis.sismember("top:total:movie", currentdate.toString());
        if (response) {
            jedis.zincrby(currentdate.toString(), 1, movie);
        } else {
            try (var tran = jedis.multi()) {
                tran.sadd("top:total:movie", currentdate.toString());
                tran.zincrby(currentdate.toString(), 1, movie);
                tran.exec();
            }
        }
        return true;
    }


    public List<RedisMovie> getTopToday(int outputNumber) {
        return jedis.zrangeWithScores(currentdate.toString(), -outputNumber, -1).stream().map(tuple -> {
            return new RedisMovie(tuple.getScore(),tuple.getElement());
        }).collect(Collectors.toList());
    }

    public List<RedisMovie> getTopToday() {
        return getTopToday(DEFAULT_OUTPUT_TOP);
    }

    public List<RedisMovie> getTopWeek(int outputNumber) {
        String[] dates = new String[7];
        for (int i = 0; i <= 6; i++) {
            String date = currentdate.minusDays(i).toString();
            dates[i] = date;
        }
        String dstKey = dates[0] + ">" + dates[6];
        jedis.zunionstore(dstKey, dates);
        return jedis.zrangeWithScores(dstKey, -outputNumber, -1).stream().map(tuple -> {
            return new RedisMovie(tuple.getScore(),tuple.getElement());
        }).collect(Collectors.toList());
    }

    public List<RedisMovie> getTopWeek() {
        return getTopWeek(DEFAULT_OUTPUT_TOP);
    }

    public List<RedisMovie> getTopMonth(int outputNumber) {
        YearMonth yearMonth = YearMonth.of(currentdate.getYear(), currentdate.getMonth());
        int daysInMonth = yearMonth.lengthOfMonth();
        String[] dates = new String[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = LocalDate.of(currentdate.getYear(), currentdate.getMonthValue(), i + 1);
            dates[i] = date.toString();
        }
        String dstKey = dates[0] + ">" + dates[daysInMonth - 1];
        jedis.zunionstore(dstKey, dates);
        return jedis.zrangeWithScores(dstKey, -outputNumber, -1).stream().map(tuple -> {
            return new RedisMovie(tuple.getScore(),tuple.getElement());
        }).collect(Collectors.toList());
    }

    public List<RedisMovie> getTopMonth() {
        return getTopMonth(DEFAULT_OUTPUT_TOP);
    }

    public List<RedisMovie> getTopOverall(int outputNumber) {
        var response = jedis.smembers("top:total:movie").toArray(new String[0]);
        String dstKey = "top:total:movie:score";
        jedis.zunionstore(dstKey, response);
        return jedis.zrangeWithScores(dstKey, -outputNumber, -1).stream().map(tuple -> {
            return new RedisMovie(tuple.getScore(),tuple.getElement());
        }).collect(Collectors.toList());
    }

    public List<RedisMovie> getTopOverall() {
        return getTopOverall(DEFAULT_OUTPUT_TOP);
    }

    public void addTopFollowed(RedisUser user) {
        jedis.zadd("top:users", user.getFollowed(), user.getUsername() + ":" + user.getId());
    }

    public void addListTopFollowed(List<RedisUser> users) {
        try (var tran = jedis.multi()) {
            for (RedisUser user : users) {
                tran.zadd("top:users", user.getFollowed(), user.getUsername() + ":" + user.getId());
            }
            tran.exec();
        }
    }

    public List<RedisUser> getTopFollowed(int outputNumber) {
        return jedis.zrangeWithScores("top:users", -outputNumber, -1).stream().map(tuple -> {
            String[] user = tuple.getElement().split(":");
            return new RedisUser(user[0],user[1],tuple.getScore());
        }).collect(Collectors.toList());
    }
    public List<RedisUser> getTopFollowed() {
        return getTopFollowed(DEFAULT_OUTPUT_TOP);
    }

    //__________________Til Test data______________________
    public boolean randomData(String movie) {

        for (int i = 0; i <= 6; i++) {
            String date = currentdate.minusDays(i).toString();
            String member = "test_" + i;
            var re = jedis.zincrby(date, 1, member);
        }
        return true;
    }

    public void fulshDB() {
        jedis.flushAll();
    }
}
