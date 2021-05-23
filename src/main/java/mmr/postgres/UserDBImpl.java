package mmr.postgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class UserDBImpl {

    private String connectionString;
    private String username, password;

    public UserDBImpl(String connectionString, String username, String password) {
        this.connectionString = connectionString;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        return DriverManager.getConnection(connectionString, props);
    }

    public void createUser(String username, String sirname, String lastname, String email, String password, int birthyear) throws SQLException {
        String sql = "INSERT INTO data_user (username, sirname, lastname, email, password, birthyear) values (?,?,?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, sirname);
            stmt.setString(3, lastname);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setInt(6, birthyear);


            stmt.executeUpdate();


        }

    }


    public String getAllUsers() throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "select * from data_user;";
            PreparedStatement statement = conn.prepareStatement(sql);
            try (ResultSet result = statement.executeQuery()) {

                ArrayList<String> users = new ArrayList<>();

                while (result.next()) {
                    int id = result.getInt("id");
                    String username = result.getString("username");
                    String sirname = result.getString("sirname");
                    String lastname = result.getString("lastname");
                    String email = result.getString("email");
                    String password = result.getString("password");
                    int birthyear = result.getInt("birthyear");

                    users.add(username + sirname + lastname + email + password + birthyear);

                }
                return users.toString();
            }
        }
    }


}
