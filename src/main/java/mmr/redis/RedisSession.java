package mmr.redis;

import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;

public class RedisSession {
    private Jedis jedis;
    private long sessionTimeInSecounds = 120;

    public RedisSession(Jedis jedis) {
        this.jedis = jedis;

    }

    public String startSession(Map<String, String> data) {
        UUID uuid = UUID.randomUUID();
        String sessionID = uuid.toString();
        String userKey = "user:" + sessionID;
        try (var tran = jedis.multi()) {
            tran.hset(userKey, data);
            tran.expire(userKey,sessionTimeInSecounds);
            tran.exec();
        }
        return sessionID;
    }

    public boolean isSessionValid(String sessionID){
        return jedis.exists("user:" + sessionID);
    }

    public Map<String, String> getSessionData(String sessionID) {
        var check = isSessionValid(sessionID);
        if (check) {
            return jedis.hgetAll("user:" + sessionID);
        }
        return null;
    }

    public Map<String, String> updateSessionData(String sessionID,Map<String, String> data){
        var check = isSessionValid(sessionID);
        if (check) {
            String userKey = "user:" + sessionID;
            jedis.hset(userKey, data);
            return jedis.hgetAll(userKey);
        }
        return null;
    }
}

