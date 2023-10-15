package BankingSystemApp.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import BankingSystemApp.User;
import BankingSystemApp.Bank;
import BankingSystemApp.BankAccount;

public class DatabaseBankAccount {
    public static void createBankAccount(Connection connection, User user, String currencyType, String accountType,
            Bank bank) {
        try {
            Random random = new Random();
            String query = "INSERT INTO bankaccount (accountNumber, currencyType, accountType, balance, userid, bankid) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, random.nextInt(100000));
            statement.setString(2, currencyType);
            statement.setString(3, accountType);
            statement.setBigDecimal(4, new BigDecimal("0"));
            statement.setInt(5, user.getUserID());
            statement.setInt(6, bank.getBankID());
            statement.executeUpdate();
            System.out.println("New account created.");
        } catch (SQLException e) {
            System.out.println("Error creating new account.");
            e.printStackTrace();
        }
    }

    public static void updateBalance(Connection connection, int accountNumber, BigDecimal balance) {
        try {
            String query = "UPDATE bankaccount SET balance = ? WHERE accountNumber = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBigDecimal(1, balance);
            statement.setInt(2, accountNumber);
            statement.executeUpdate();
            System.out.println("New balance: " + balance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load a bank account from the database based on the account number
    public static BankAccount loadBankAccount(Connection connection, User user, int accountNumber) {
        try {
            String query = "SELECT * FROM bankaccount WHERE userid = ? AND accountNumber = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getUserID());
            statement.setInt(2, accountNumber);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String currencyType = resultSet.getString("currencyType");
                String accountType = resultSet.getString("accountType");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                return new BankAccount(accountNumber, currencyType, accountType, balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String username = "kostas";
        Connection connection = DatabaseConnection.getConnection();
        User myuser = DatabaseUser.findUserByUsername(connection, username);
        BankAccount currentAccount = loadBankAccount(connection, myuser, 79199);
        if (currentAccount != null) {
        } else {
            System.out.println(" not found");
        }
    }
}
