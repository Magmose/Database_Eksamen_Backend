package mmr.postgres;

import mmr.dto.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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


    public void setupData() throws Exception {
        String sqlSource = "src/main/resources/postgres.sql";
        String sqlString = Files.readString(Path.of(sqlSource));

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sqlString);
            stmt.executeUpdate();

        }

    }

    public void createPremiumUser(String username,
                                  String sirname,
                                  String lastname,
                                  String email,
                                  String password,
                                  int birthyear,
                                  String roleType,
                                  long premiumEndDate,
                                  String subscriptionTier,
                                  long cardNumber) throws SQLException {
        String sql = "CALL create_user (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, sirname);
            stmt.setString(3, lastname);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setInt(6, birthyear);
            stmt.setString(7, roleType);

            stmt.setLong(8, premiumEndDate);
            stmt.setString(9, subscriptionTier);
            stmt.setLong(10, cardNumber);


            stmt.executeUpdate();


        }

    }


    public void createUser(String username,
                           String sirname,
                           String lastname,
                           String email,
                           String password,
                           int birthyear,
                           String roleType) throws SQLException {
        String sql = "CALL create_user (?,?,?,?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, sirname);
            stmt.setString(3, lastname);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setInt(6, birthyear);
            stmt.setString(7, roleType);


            stmt.executeUpdate();


        }

    }


    public ArrayList<User> getAllUsers() throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "select * from full_user_data;";
            PreparedStatement statement = conn.prepareStatement(sql);
            try (ResultSet result = statement.executeQuery()) {

                ArrayList<User> users = new ArrayList<>();

                while (result.next()) {
                    int id = result.getInt("id");
                    String username = result.getString("username");
                    String sirname = result.getString("sirname");
                    String lastname = result.getString("lastname");
                    String email = result.getString("email");
                    String password = result.getString("password");
                    int birthyear = result.getInt("birthyear");
                    String roleType = result.getString("role_type");

                    long premiumEndDate = result.getLong("premium_end_date");
                    String subscriptionTier = result.getString("subsciption_tier");
                    long cardNumber = result.getLong("card_number");
                    User user = new User(birthyear, username, sirname, lastname, email, password, roleType, subscriptionTier, premiumEndDate, cardNumber);
                    user.setId(id);
                    users.add(user);

                }
                return users;
            }
        }
    }


    public Map<String,String> getUser(String username) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "select * from full_user_data WHERE username = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                Map<String,String> response = new HashMap<>();
                if (result.next()) {
                    response.put("id",String.valueOf(result.getInt("id")));
                    response.put("username",result.getString("username"));
                    response.put("sirname",result.getString("sirname"));
                    response.put("lastname",result.getString("lastname"));
                    response.put("email",result.getString("email"));
                    response.put("password",result.getString("password"));
                    response.put("birthyear",String.valueOf(result.getInt("birthyear")));
                    response.put("role_type",result.getString("role_type"));
                    response.put("premium_end_date",String.valueOf(result.getLong("premium_end_date")));
                    response.put("subsciption_tier",result.getString("subsciption_tier"));
                    response.put("card_number",String.valueOf(result.getLong("card_number")));


                    return response;
                }
                return null;
            }
        }
    }

    public void updateUserRole(int userId, String roleType) throws SQLException {
        String sql = "UPDATE user_data " +
                "SET role_type = ? " +
                "WHERE id = ?;";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, roleType);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        }
    }


    public ArrayList<String> getUserRoleLog() throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "select * from user_role_log;";
            PreparedStatement statement = conn.prepareStatement(sql);
            try (ResultSet result = statement.executeQuery()) {

                ArrayList<String> logs = new ArrayList<>();

                while (result.next()) {
                    int user_id = result.getInt("user_id");
                    String newRoleType = result.getString("old_role_type");
                    String oldRoleType = result.getString("new_role_type");
                    String timestamp = result.getTimestamp("timestamp").toString();

                    String log = "USER ID: " + user_id + " OLD ROLE: " + oldRoleType + " NEW ROLE: " + newRoleType + " TIMESTAMP: " + timestamp;

                    logs.add(log);
                }
                return logs;
            }
        }
    }

}
