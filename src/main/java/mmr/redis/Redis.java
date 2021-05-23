package mmr.redis;

import mmr.dto.Movie;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Redis {
    private final String host = "localhost";
    private final int port = 6379;
    private Jedis jedis;

    public Redis() {
        jedis = new Jedis(host, port);
    }

    public boolean incDay(String movie) {
        LocalDate currentdate = LocalDate.now();

        try (var tran = jedis.multi()) {
            tran.zincrby(currentdate.toString(), 1, movie);

            tran.exec();
        }
        return true;
    }

    public Set<Tuple> getTopWeek(){
        String[] dates = new String[7];
        for (int i = 0; i < 6 ; i++) {
           String date = LocalDate.now().minusDays(i).toString();
            dates[i] =date;
        }
        String dstKey = dates[0]+">"+dates[6];
        Set<Tuple> response;
        try (var tran = jedis.multi()) {
            tran.zunionstore(dstKey,dates);
            response = (Set<Tuple>) tran.zrangeWithScores(dstKey,0,9);
            tran.exec();
        }
        return response;
    }


    //__________________Til Test data______________________


}
