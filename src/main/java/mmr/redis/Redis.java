package mmr.redis;

import mmr.dto.Movie;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class Redis {
    //Til docker
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

    public void getTopWeek(){
        var dates = new ArrayList<String>();
        for (int i = 0; i < 6 ; i++) {
           String date = LocalDate.now().minusDays(i).toString();
            dates.add(date);
        }
        String dstKey = dates.get(0);
    }


    //__________________Til Test data______________________

}
