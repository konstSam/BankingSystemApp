package BankingSystemApp.database;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import BankingSystemApp.User;
import BankingSystemApp.Bank;
import BankingSystemApp.BankAccount;
import BankingSystemApp.CustomExceptions;

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

    public static void depositAmount(Connection connection, int accountNumber, BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("\nInvalid deposit amount");
                return; // Exit the method if the deposit amount is invalid
            }

            String query = "UPDATE bankaccount SET balance = balance + ? WHERE accountNumber = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBigDecimal(1, amount);
            statement.setInt(2, accountNumber);
            statement.executeUpdate();
            String query2 = "SELECT balance FROM bankaccount WHERE accountNumber = ?";
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setInt(1, accountNumber);
            ResultSet resultSet = statement2.executeQuery();
            if (resultSet.next()) {
                BigDecimal balance = resultSet.getBigDecimal("balance");
                System.out.println(
                        "New Balance: " + balance.setScale(2, RoundingMode.HALF_UP));
            }
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

    // Get and return a list of all accounts of a user
    public static ArrayList<Integer> getListOfAccounts(User user, Connection connection) {
        ArrayList<Integer> accountNumbers = new ArrayList<>();
        try {
            String query = "SELECT accountNumber FROM bankaccount WHERE userid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getUserID());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int accountNumber = resultSet.getInt("accountNumber");
                accountNumbers.add(accountNumber);
            }

            return accountNumbers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // method that the user inputs an int that represents an account and it searches
    // for that account if exists
    public static Integer selectAccount(User user, ArrayList<Integer> accounts, String inputMessage,
            Scanner scanner) {
        int accountChoice = 0;
        boolean validChoice = false;

        while (!validChoice) {
            System.out.print(inputMessage);
            accountChoice = CustomExceptions.getIntCheckException(scanner);
            if (accountChoice >= 1 && accountChoice <= accounts.size()) {
                validChoice = true;
            } else {
                System.out.println("Invalid choice. Please select a valid account.");
            }
        }

        return accounts.get(accountChoice - 1);
    }

    // Get all account of a specific user
    public static void displayAllUserAccounts(User user, Connection connection) {
        // Display user's accounts
        System.out.println("Your Accounts: ");
        int i = 1;
        try {

            String query = "SELECT * FROM bankaccount WHERE userid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getUserID());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int accountnumber = resultSet.getInt("accountNumber");
                String currencyType = resultSet.getString("currencyType");
                String accountType = resultSet.getString("accountType");

                System.out.println(
                        accountType + " (Account Number: " + accountnumber
                                + ", "
                                + currencyType + ") (" + i++
                                + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}