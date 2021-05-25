package mmr.postgres;

import mmr.dto.User;

import java.nio.file.Files;
import java.nio.file.Path;
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


    public User getUser(int userId) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "select * from full_user_data WHERE id = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
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
                    return user;
                }
                return null;
            }
        }
    }
}
