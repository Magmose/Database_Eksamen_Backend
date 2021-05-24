package mmr.redis;

import mmr.dto.User;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

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


    public Set<Tuple> getTopToday(int outputNumber) {
        return jedis.zrangeWithScores(currentdate.toString(), -outputNumber, -1);
    }

    public Set<Tuple> getTopToday() {
        return getTopToday(DEFAULT_OUTPUT_TOP);
    }

    public Set<Tuple> getTopWeek(int outputNumber) {
        String[] dates = new String[7];
        for (int i = 0; i <= 6; i++) {
            String date = currentdate.minusDays(i).toString();
            dates[i] = date;
        }
        String dstKey = dates[0] + ">" + dates[6];
        jedis.zunionstore(dstKey, dates);
        return jedis.zrangeWithScores(dstKey, -outputNumber, -1);
    }

    public Set<Tuple> getTopWeek() {
        return getTopWeek(DEFAULT_OUTPUT_TOP);
    }

    public Set<Tuple> getTopMonth(int outputNumber) {
        YearMonth yearMonth = YearMonth.of(currentdate.getYear(), currentdate.getMonth());
        int daysInMonth = yearMonth.lengthOfMonth();
        String[] dates = new String[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = LocalDate.of(currentdate.getYear(), currentdate.getMonthValue(), i + 1);
            dates[i] = date.toString();
        }
        String dstKey = dates[0] + ">" + dates[daysInMonth - 1];
        jedis.zunionstore(dstKey, dates);
        return jedis.zrangeWithScores(dstKey, -outputNumber, -1);
    }

    public Set<Tuple> getTopMonth() {
        return getTopMonth(DEFAULT_OUTPUT_TOP);
    }

    public Set<Tuple> getTopOverall(int outputNumber) {
        var response = jedis.smembers("top:total:movie").toArray(new String[0]);
        String dstKey = "top:total:movie:score";
        jedis.zunionstore(dstKey, response);
        return jedis.zrangeWithScores(dstKey, -outputNumber, -1);
    }

    public Set<Tuple> getTopOverall() {
        return getTopOverall(DEFAULT_OUTPUT_TOP);
    }

    public void addTopFollowed(User user) {
        jedis.zadd("top:users", user.getFollowed(), user.getUsername() + ":" + user.getId());
    }

    public void addListTopFollowed(List<User> users) {
        try (var tran = jedis.multi()) {
            for (User user : users) {
                tran.zadd("top:users", user.getFollowed(), user.getUsername() + ":" + user.getId());
            }
            tran.exec();
        }
    }

    public Set<Tuple> getTopFollowed() {
        return jedis.zrangeWithScores("top:users", -3, -1);
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
