package mmr.dto.request;

public class RequestBodyLogin {
    private String username,passwod;

    public RequestBodyLogin() {
    }

    public RequestBodyLogin(String username, String passwod) {
        this.username = username;
        this.passwod = passwod;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswod() {
        return passwod;
    }

    public void setPasswod(String passwod) {
        this.passwod = passwod;
    }
}
