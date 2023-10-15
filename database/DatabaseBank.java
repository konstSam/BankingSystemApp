package BankingSystemApp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import BankingSystemApp.Bank;

public class DatabaseBank {
    public static Bank findBankByName(Connection connection, String name) {
        try {
            String query = "SELECT * FROM bank WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int bankid = resultSet.getInt("bankid");
                return new Bank(bankid, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
