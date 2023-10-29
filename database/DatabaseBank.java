package BankingSystemApp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import BankingSystemApp.User;
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

    public static Bank findBankByAccount(Connection connection, int accountNumber){
        try {
            String query = "SELECT b.bankid, b.name FROM bank b" +
                           "JOIN bankaccount a on a.bankid=b.bankid " +
                           "WHERE a.accountNumber = ? ;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, accountNumber);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int bankid = resultSet.getInt("bankid");
                String bankName = resultSet.getString("name");
                return new Bank(bankid, bankName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bank findBankByUser(Connection connection, User currentUser){
        try {
            String query = "SELECT b.bankid, b.name FROM bank b " +
                        "JOIN bankaccount a on a.bankid=b.bankid " +
                        "JOIN user u on u.userid=a.userid " +
                        "WHERE u.userid = ? ;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, currentUser.getUserID());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int bankid = resultSet.getInt("bankid");
                String bankName = resultSet.getString("name");
                return new Bank(bankid, bankName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
