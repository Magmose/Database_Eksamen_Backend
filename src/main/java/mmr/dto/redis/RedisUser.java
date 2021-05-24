package mmr.dto.redis;

public class RedisUser {
    private String username;
    private String id;
    private double followed;

    public RedisUser() {
    }

    public RedisUser(String id, String username, double followed) {
        this.username = username;
        this.id = id;
        this.followed = followed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getFollowed() {
        return followed;
    }

    public void setFollowed(double followed) {
        this.followed = followed;
    }

    @Override
    public String toString() {
        return "RedisUser{" +
                "username='" + username + '\'' +
                ", id='" + id + '\'' +
                ", followed=" + followed +
                '}';
    }
}
