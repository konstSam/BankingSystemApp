package BankingSystemApp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import BankingSystemApp.User;

public class DatabaseUser {

    public static void createUser(Connection connection, User user) {
        try {
            String query = "INSERT INTO users (userName) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User findUserByUsername(Connection connection, String username) {
        try {
            String query = "SELECT * FROM user WHERE userName = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int userid = resultSet.getInt("userid");
                return new User(userid, username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}