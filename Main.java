package BankingSystemApp;

import java.util.Scanner;
import BankingSystemApp.database.DatabaseConnection;
import BankingSystemApp.database.Methods;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getConnection();
        Scanner scanner = new Scanner(System.in);
        try {
            // Create a Scanner for user input
            User currentUser = Methods.login(connection, scanner);;

            // Display user's accounts
            System.out.println("Welcome, " + currentUser.getName() + "!");
            boolean isRunning = true;
            // Provide options for transactions
            while (isRunning) {
                Methods.displayOptions();
                int option = CustomExceptions.getIntCheckException(scanner);
                // remove newline character
                scanner.nextLine();

                switch (option) {
                    case 1: // DEPOSIT
                        Methods.deposits(connection, currentUser, scanner);
                        break;
                    case 2: // WITHDRAW
                        Methods.withdrawals(connection, currentUser, scanner);
                        break;
                    case 3: // CHECK BALANCE
                        Methods.checksBalance(connection, currentUser, scanner);
                        break;
                    case 4: // CONVERT CURRENCY
                        Methods.currencyConverter(connection, currentUser,scanner);
                        break;
                    case 5: // TRANSFER TO ANOTHER ACCOUNT
                        Methods.transferMoney(connection, currentUser, scanner);
                        break;
                    case 6: // CREATE NEW ACCOUNT
                        Methods.openNewAccount(connection, currentUser, scanner);
                        break;
                    case 7:
                        Methods.exit(connection, scanner);
                        return;
                    default:
                        System.out.println("Invalid option. Please choose a valid option.");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            scanner.close(); // Close the scanner in the "finally" block
            DatabaseConnection.closeConnection(connection);
        }
    }
}
