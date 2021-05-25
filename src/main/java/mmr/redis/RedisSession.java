package mmr.redis;

import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class RedisSession {
    private Jedis jedis;


    public RedisSession(String host, int port) {
        jedis = new Jedis(host, port);
    }

    public String startSession(Map<String, String> data) {
        UUID uuid = UUID.randomUUID();
        String sessionKey = uuid.toString();
        String userKey = "user:" + sessionKey;
        try (var tran = jedis.multi()) {
            tran.sadd("active:session", sessionKey);
            tran.hset(userKey, data);
            tran.expire(userKey, 120L);
            tran.exec();
        }
        return sessionKey;
    }

    public Map<String, String> getSessionData(String sessionKey) {
        var check = jedis.sismember("active:session", sessionKey);
        if (check) {
            return jedis.hgetAll("user:" + sessionKey);
        }
        return null;
    }

    public Map<String, String> updateSessionData(String sessionKey,Map<String, String> data){
        var check = jedis.sismember("active:session", sessionKey);
        if (check) {
            String userKey = "user:" + sessionKey;
            jedis.hset(userKey, data);
            return jedis.hgetAll(userKey);
        }
        return null;
    }
}

