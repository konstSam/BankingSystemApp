package BankingSystemApp;

import java.util.ArrayList;
import java.util.Scanner;

import BankingSystemApp.database.DatabaseBank;
import BankingSystemApp.database.DatabaseConnection;
import BankingSystemApp.database.DatabaseUser;
import BankingSystemApp.database.DatabaseBankAccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getConnection();
        Scanner scanner = new Scanner(System.in);
        try {
            // Create a Scanner for user input
            User currentUser = login(connection, scanner);;

            // Display user's accounts
            System.out.println("Welcome, " + currentUser.getName() + "!");
            boolean isRunning = true;
            // Provide options for transactions
            while (isRunning) {
                displayOptions();

                // int option = scanner.nextInt();
                int option = CustomExceptions.getIntCheckException(scanner);
                // remove newline character
                scanner.nextLine();

                switch (option) {
                    case 1: // DEPOSIT
                        deposits(connection, currentUser, scanner);
                        break;

                    case 2: // WITHDRAW
                        withdrawals(connection, currentUser, scanner);
                        break;


                    case 3: // CHECK BALANCE
                        checksBalance(connection, currentUser, scanner);
                        break;

                    case 4: // CONVERT CURRENCY
                        currencyConverter(connection, currentUser,scanner);
                        break;

                    case 5: // TRANSFER TO ANOTHER ACCOUNT
                        transferMoney(connection, currentUser, scanner);
                        break;

                    case 6: // CREATE NEW ACCOUNT
                        openNewAccount(connection, currentUser, scanner);
                        break;

                    case 7:
                        exit(connection, scanner);
                        return;

                    default:
                        System.out.println("Invalid option. Please choose a valid option.");
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            scanner.close(); // Close the scanner in the finally block
            DatabaseConnection.closeConnection(connection);
        }

    }

    private static User login(Connection connection, Scanner scanner) {
        User currentUser = null;
        boolean userExists = false;

        while (!userExists) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            currentUser = DatabaseUser.findUserByUsername(connection, username);
            if (currentUser != null) {
                userExists = true;
            } else {
                System.out.println("User not found. Try again.");
            }
        }
        return currentUser;
    }

    private static void exit(Connection connection, Scanner scanner) {
        System.out.println("Thank you for using our banking system. Goodbye!");
        scanner.close();
        DatabaseConnection.closeConnection(connection);
    }

    private static void displayOptions() {
        // Display the available options
        System.out.println("\nChoose an option:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Check Balance");
        System.out.println("4. Convert to another Currency");
        System.out.println("5. Tranfer to another Account");
        System.out.println("6. Open new account");
        System.out.println("7. Exit");
    }

    private static void deposits(Connection connection, User currentUser, Scanner scanner) {
        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection, "full");
        int depositAccountNumber = DatabaseBankAccount.selectAccount(
                DatabaseBankAccount.getListOfAccounts(currentUser, connection, "full"),
                "\nChoose the account to deposit into. Enter the number that represents it:",
                scanner);
        BankAccount depositAccount = DatabaseBankAccount.loadBankAccount(connection,
                depositAccountNumber);
        // Print information about the selected account
        assert depositAccount != null;
        depositAccount.getAccountDetails();

        System.out.print("\nEnter the amount to deposit: ");
        BigDecimal depositAmount = CustomExceptions.getDecimalCheckException(scanner);
        scanner.nextLine();
        boolean deposited = DatabaseBankAccount.depositAmount(connection, depositAccountNumber, depositAmount);
        if (deposited) {
            depositAccount = DatabaseBankAccount.loadBankAccount(connection, depositAccountNumber);
            assert depositAccount != null;
            System.out.println(
                    "New Balance: " + depositAccount.getBalance().setScale(2, RoundingMode.HALF_UP) + " " + depositAccount.getCurrencyType());
        } else {
            System.out.println("Deposit failed.");
        }
    }

    private static void withdrawals(Connection connection, User currentUser, Scanner scanner) {
        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection, "full");
        int withdrawAccountNumber = DatabaseBankAccount.selectAccount(
                DatabaseBankAccount.getListOfAccounts(currentUser, connection, "full"),
                "\nChoose the account to withraw. Enter the number that represents it:",
                scanner);
        BankAccount withdrawAccount = DatabaseBankAccount.loadBankAccount(connection,
                withdrawAccountNumber);
        // Print information about the selected account
        assert withdrawAccount != null;
        withdrawAccount.getAccountDetails();

        System.out.print("\nEnter the amount to withdraw: ");
        BigDecimal withdrawalAmount = CustomExceptions.getDecimalCheckException(scanner);
        scanner.nextLine();
        boolean withdrawn = DatabaseBankAccount.withdrawAmount(connection, withdrawAccount, withdrawAccountNumber, withdrawalAmount);
        if (withdrawn) {
            withdrawAccount = DatabaseBankAccount.loadBankAccount(connection, withdrawAccountNumber);
            assert withdrawAccount != null;
            System.out.println(
                    "New Balance: " + withdrawAccount.getBalance().setScale(2, RoundingMode.HALF_UP) + " " + withdrawAccount.getCurrencyType());
        } else {
            System.out.println("Withdrawal failed.");
        }
    }

    private static void checksBalance(Connection connection, User currentUser, Scanner scanner) {
        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection, "full");

        int checkAccountNumber = DatabaseBankAccount.selectAccount(
                DatabaseBankAccount.getListOfAccounts(currentUser, connection, "full"),
                "\nChoose the account to check balance. Enter the number that represents it:",
                scanner);
        BankAccount currentAccount = DatabaseBankAccount.loadBankAccount(connection,
                checkAccountNumber);
        assert currentAccount != null;
        currentAccount.getAccountDetails();
        System.out
                .println("Balance: " + currentAccount.checkBalance().setScale(2, RoundingMode.HALF_UP) + " " + currentAccount.getCurrencyType());
    }

    private static void currencyConverter(Connection connection, User currentUser, Scanner scanner) {
        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection, "full");
        int conversionAccountNumber = DatabaseBankAccount.selectAccount(
                DatabaseBankAccount.getListOfAccounts(currentUser, connection, "full"),
                "\nChoose the account to convert from. Enter the number that represents it:",
                scanner);
        scanner.nextLine();
        BankAccount conversionAccount = DatabaseBankAccount.loadBankAccount(connection,
                conversionAccountNumber);
        // Print information about the selected account
        assert conversionAccount != null;
        conversionAccount.getAccountDetails();

        String sourceCurrency = conversionAccount.getCurrencyType();
        System.out.printf("Your account is %s. Choose currency to convert (e.g. USD): ",
                sourceCurrency);

        String targetCurrency = Converter.validateCurrency(scanner);

        System.out.printf("Available Balance %s. Enter amount to convert: ",
                conversionAccount.checkBalance().setScale(2, RoundingMode.HALF_UP));
        BigDecimal amount = CustomExceptions.getDecimalCheckException(scanner);
        BigDecimal[] rates = Converter.getExchangeRates(amount, sourceCurrency, targetCurrency,
                scanner);
        System.out.printf("Exchange rate: %.3f. Converted amount: %.3f %s", rates[0], rates[1],
                targetCurrency);

        System.out.println("\nDo you want to proceed with the conversion?(Yes/No) ");
        scanner.nextLine();
        String response = scanner.nextLine();

        // list the available accounts having target currency
        if (response.equalsIgnoreCase("yes")) {

            if (conversionAccount.getBalance().compareTo(amount) <= 0) {
                System.out.println("Not enough money in your balance.");
                return;
            } else {
                System.out.println("Available " + targetCurrency + " accounts to transfer: ");
                assert targetCurrency != null;
                DatabaseBankAccount.displayAllUserAccounts(currentUser, connection, targetCurrency);
                boolean resultsExist = DatabaseBankAccount.hasResults(connection);

                if (!resultsExist) {
                    System.out.println("There is no " + targetCurrency
                            + " account on your name. Please open a new one first.");
                    return;
                }

                int convertedAccountNumber = DatabaseBankAccount.selectAccount(
                        DatabaseBankAccount.getListOfAccounts(currentUser, connection, targetCurrency),
                        "\nSelect account to transfer: ",
                        scanner);

                boolean conversion = DatabaseBankAccount.withdrawAmount(connection, conversionAccount, conversionAccountNumber, amount);
                if (conversion) {
                    System.out.println("Withdrawn " + amount.toPlainString() + " " + conversionAccount.getCurrencyType());
                    boolean converted = DatabaseBankAccount.depositAmount(connection, convertedAccountNumber, rates[1]);
                    System.out.println(converted ? "Transfer was successful!" : "Transfer failed.");
                    System.out.printf("Converted %.3f %s to %.3f %s with Exchange rate: %.3f ", amount, conversionAccount.getCurrencyType(), rates[1],
                            targetCurrency, rates[0]);
                }
            }
        }
    }

    private static void transferMoney(Connection connection, User currentUser, Scanner scanner) {
        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection, "full");
        int senderAccountNumber = DatabaseBankAccount.selectAccount(
                DatabaseBankAccount.getListOfAccounts(currentUser, connection, "full"),
                "\nChoose the account to transfer from. Enter the number that represents it:",
                scanner);
        BankAccount senderAccount = DatabaseBankAccount.loadBankAccount(connection,
                senderAccountNumber);
        // Print information about the selected account
        assert senderAccount != null;
        senderAccount.getAccountDetails();
        System.out.println("Available balance: " + senderAccount.getBalance() + " " + senderAccount.getCurrencyType());

        System.out.print("\nEnter the account number of the receiver: ");
        int receiverAccountNumber = CustomExceptions.getIntCheckException(scanner);
        scanner.nextLine();

        System.out.print("\nEnter the amount to transfer: ");
        BigDecimal transferAmount = CustomExceptions.getDecimalCheckException(scanner);
        scanner.nextLine();

        BankAccount receiverAccount = DatabaseBankAccount.loadBankAccount(connection,
                receiverAccountNumber);
        if (receiverAccount==null){
            System.out.println("Account not found!\n");
            return;
        }

        String receiverUsername = DatabaseUser.findUserByAccountNumber(connection, receiverAccountNumber);
        String receiverCurrency = receiverAccount.getCurrencyType();

        if (receiverAccount != null && receiverAccount != null && receiverCurrency.equalsIgnoreCase(senderAccount.getCurrencyType())) {
            System.out.println("Found account with Username: " + receiverUsername);

            if (senderAccount.getBalance().compareTo(transferAmount) <= 0) {
                System.out.println("Not enough money in your balance.");
                return;
            } else {
                boolean ok = DatabaseBankAccount.withdrawAmount(connection, senderAccount, senderAccountNumber, transferAmount);
                if (ok) {
                    senderAccount = DatabaseBankAccount.loadBankAccount(connection, senderAccountNumber);
                    assert senderAccount != null;
                    System.out.println(
                            "New Balance: " + senderAccount.getBalance().setScale(2, RoundingMode.HALF_UP) + " " + senderAccount.getCurrencyType());
                }
                boolean received = DatabaseBankAccount.depositAmount(connection, receiverAccountNumber, transferAmount);
                System.out.println(received ? "Transfer was successful!" : "Transfer failed.");
            }
        } else if (receiverAccount != null && !receiverCurrency.equalsIgnoreCase(senderAccount.getCurrencyType())) {
            System.out.println("Found account with Username: " + receiverUsername);
            System.out.println("Receiver Account currency is " + receiverAccount.getCurrencyType()+ ".");


            BigDecimal[] transferRate = Converter.getExchangeRates(transferAmount, senderAccount.getCurrencyType(), receiverAccount.getCurrencyType(),
                    scanner);
            System.out.printf("Exchange rate: %.3f. Converted amount: %.3f %s", transferRate[0], transferRate[1],
                    receiverAccount.getCurrencyType());

            System.out.println("\nDo you want to proceed with the conversion?(Yes/No) ");
            String userResponse = scanner.nextLine();

            if (userResponse.equalsIgnoreCase("yes")) {
                boolean ok = DatabaseBankAccount.withdrawAmount(connection, senderAccount, senderAccountNumber, transferAmount);

                if (ok) {
                    senderAccount = DatabaseBankAccount.loadBankAccount(connection, senderAccountNumber);
                    assert senderAccount != null;
                    System.out.println(
                            "New Balance: " + senderAccount.getBalance().setScale(2, RoundingMode.HALF_UP) + " " + senderAccount.getCurrencyType());
                }
                boolean received = DatabaseBankAccount.depositAmount(connection, receiverAccountNumber, transferAmount);
                System.out.println(received ? "Transfer was successful!" : "Transfer failed.");
            }
        } else {
            // Account with the specified accountNumber was not found
            System.out.println("Account not found.");
            return;
        }
    }

    private static void openNewAccount(Connection connection, User currentUser, Scanner scanner) {
        System.out.println("Enter the currency type for your account: ");
        String currencytype = Converter.validateCurrency(scanner);
        System.out.println("Enter the account type for your account: ");
        String accounttype = scanner.nextLine();

        Bank currentBank = DatabaseBank.findBankByUser(connection, currentUser);
        assert currentBank != null;
        boolean created = DatabaseBankAccount.createBankAccount(connection, currentUser, currencytype, accounttype, currentBank);
        System.out.println(created ? "New account created successfully!" : "Error creating new account.");
    }


    // method that the user inputs an int that represents an account and it searches
    // for that account if exists
    public static BankAccount chooseAccount(User user, ArrayList<BankAccount> accounts, String inputMessage,
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

}
