package mmr.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.time.LocalDate;
import java.util.Set;

public class Redis {


    // Hive alle ud
    // Fiks build data

    private Jedis jedis;

    public Redis(String host, int port) {
        jedis = new Jedis(host, port);
    }

    public boolean incDay(String movie) {
        LocalDate currentdate = LocalDate.now();
        jedis.zincrby(currentdate.toString(), 1, movie);
        return true;
    }

    public Set<Tuple> getTopWeek() {
        String[] dates = new String[7];
        for (int i = 0; i <= 6; i++) {
            String date = LocalDate.now().minusDays(i).toString();
            dates[i] = date;
        }
        String dstKey = dates[0] + ">" + dates[6];


        jedis.zunionstore(dstKey, dates);
        Set<Tuple> response = jedis.zrangeWithScores(dstKey, -3, -1);
        return response;
    }

    public Set<Tuple> getTopToday(){
        LocalDate currentdate = LocalDate.now();
        return jedis.zrangeWithScores(currentdate.toString(), -3, -1);
    }

    public long addTopFollowed(String id, String username,int followed){
       return jedis.zadd("top:users",followed,username+":"+id);
    }

    public Set<Tuple> getTopFollowed(){
    return jedis.zrangeWithScores("top:users", -3, -1);
    }


    //__________________Til Test data______________________
    public boolean randomData(String movie) {

        for (int i = 0; i <= 6; i++) {
            String date = LocalDate.now().minusDays(i).toString();
            String member = "test_"+i;

            var re = jedis.zincrby(date, 1, member);
           // System.out.println("Works: " + re+"     key."+date);


        }
        return true;
    }

    public void fulshDB(){
      jedis.flushAll();
    }
}
