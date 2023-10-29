package BankingSystemApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import BankingSystemApp.BankAccount.AccountAndUserName;
import BankingSystemApp.database.DatabaseConnection;
import BankingSystemApp.database.DatabaseUser;
import BankingSystemApp.database.DatabaseBankAccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Bank eurobank = new Bank(1, "eurobank");

        Scanner scanner = new Scanner(System.in);
        try {
            // Create a Scanner for user input
            User currentUser = null;
            Connection connection = DatabaseConnection.getConnection();
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
            // Display user's accounts
            System.out.println("Welcome, " + currentUser.getName() + "!");
            DatabaseBankAccount.displayAllUserAccounts(currentUser, connection);
            boolean isRunning = true;
            // Provide options for transactions
            while (isRunning) {
                System.out.println("\nChoose an option:");
                System.out.println("1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Check Balance");
                System.out.println("4. Convert to another Currency");
                System.out.println("5. Tranfer to another Account");
                System.out.println("6. Open new account");
                System.out.println("7. Exit");

                // int option = scanner.nextInt();
                int option = CustomExceptions.getIntCheckException(scanner);
                // remove newline character
                scanner.nextLine();

                switch (option) {
                    case 1: // DEPOSIT
                        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection);
                        int depositAccountNumber = DatabaseBankAccount.selectAccount(currentUser,
                                DatabaseBankAccount.getListOfAccounts(currentUser, connection),
                                "\nChoose the account to deposit into. Enter the number that represents it:",
                                scanner);
                        BankAccount depositAccount = DatabaseBankAccount.loadBankAccount(connection, currentUser,
                                depositAccountNumber);
                        // Print information about the selected account
                        depositAccount.getAccountDetails();

                        System.out.print("\nEnter the amount to deposit: ");
                        BigDecimal depositAmount = CustomExceptions.getDecimalCheckException(scanner);
                        scanner.nextLine();
                        DatabaseBankAccount.depositAmount(connection, depositAccount, depositAccountNumber, depositAmount);
                        break;

                    case 2: // WITHDRAW
                        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection);
                        int withdrawAccountNumber = DatabaseBankAccount.selectAccount(currentUser,
                                DatabaseBankAccount.getListOfAccounts(currentUser, connection),
                                "\nChoose the account to withraw. Enter the number that represents it:",
                                scanner);
                        BankAccount withdrawAccount = DatabaseBankAccount.loadBankAccount(connection, currentUser,
                                withdrawAccountNumber);
                        // Print information about the selected account
                        withdrawAccount.getAccountDetails();

                        System.out.print("\nEnter the amount to withdraw: ");
                        BigDecimal withdrawalAmount = CustomExceptions.getDecimalCheckException(scanner);
                        scanner.nextLine();
                        DatabaseBankAccount.withdrawAmount(connection, withdrawAccount, withdrawAccountNumber, withdrawalAmount);
                        break;

                    case 3: // CHECK BALANCE
                        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection);

                        int checkAccountNumber = DatabaseBankAccount.selectAccount(currentUser,
                                DatabaseBankAccount.getListOfAccounts(currentUser, connection),
                                "\nChoose the account to check balance. Enter the number that represents it:",
                                scanner);
                        BankAccount currentAccount = DatabaseBankAccount.loadBankAccount(connection, currentUser,
                                checkAccountNumber);
                        currentAccount.getAccountDetails();
                        System.out
                                .println("Balance: " + currentAccount.checkBalance().setScale(2, RoundingMode.HALF_UP) + " " + currentAccount.getCurrencyType());
                        break;

                    case 4:
                        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection);
                        BankAccount conversionAccount = chooseAccount(currentUser, currentUser.getAccounts(),
                                "\nChoose the account to convert from. Enter the number that represents it: ",
                                scanner);
                        scanner.nextLine();
                        // Print information about the selected account
                        conversionAccount.getAccountDetails();
                        String sourceCurrency = conversionAccount.getCurrencyType();
                        System.out.printf("Your account is %s. Choose currency to convert (e.g. USD): ",
                                sourceCurrency);

                        String targetCurrency = Converter.validateCurrency(scanner);

                        System.out.printf("Available Balance %s. Enter amount to convert: ",
                                conversionAccount.checkBalance().setScale(2, RoundingMode.HALF_UP));
                        BigDecimal amount = CustomExceptions.getDecimalCheckException(scanner);
                        BigDecimal rates[] = Converter.getExchangeRates(amount, sourceCurrency, targetCurrency,
                                scanner);
                        System.out.printf("Exchange rate: %.3f. Converted amount: %.3f %s", rates[0], rates[1],
                                targetCurrency);

                        System.out.println("\nDo you want to proceed with the conversion?(Yes/No) ");
                        scanner.nextLine();
                        String response = scanner.nextLine();

                        // list the available accounts having targetcurrency
                        if (response.equalsIgnoreCase("yes")) {

                            if (conversionAccount.getBalance().compareTo(amount) <= 0) {
                                System.out.println("Not enough money in your balance.");
                                break;
                            } else {
                                System.out.println("Available " + targetCurrency + " accounts to transfer: ");
                                int j = 1;
                                int lackOfAccount = 0;

                                HashMap<Integer, BankAccount> curAccountsList = new HashMap<Integer, BankAccount>();

                                for (BankAccount account : currentUser.getAccounts()) {
                                    if (account.getCurrencyType().equals(targetCurrency)) {
                                        System.out.println(
                                                account.getAccountType() + " (Account Number: "
                                                        + account.getAccountNumber()
                                                        + ", "
                                                        + account.getCurrencyType() + ") (" + j
                                                        + ")");
                                        // Add the matching account to the ArrayList
                                        curAccountsList.put(j, account);
                                        j++;
                                        lackOfAccount++;
                                    }
                                }

                                if (lackOfAccount == 0) {
                                    System.out.println("There is no " + targetCurrency
                                            + " account on your name. Please open a new one first.");
                                    break;
                                }
                                System.out.println("\nSelect account to transfer(e.g. 2): ");
                                int ans = CustomExceptions.getIntCheckException(scanner);
                                scanner.nextLine();

                                BankAccount depAccount = curAccountsList.get(ans);
                                conversionAccount.withdrawMoney(currentUser, conversionAccount, amount);
                                depAccount.depositMoney(currentUser, depAccount, rates[1]);

                                System.out.printf("\nAccount %s, New Balance: %.2f",
                                        conversionAccount.getAccountNumber(),
                                        conversionAccount.checkBalance().setScale(2, RoundingMode.HALF_UP));
                                System.out.printf("\nAccount %s, New Balance: %.2f\n", depAccount.getAccountNumber(),
                                        depAccount.checkBalance().setScale(2, RoundingMode.HALF_UP));
                            }
                        }
                        break;

                    case 5: // TRANSFER TO ANOTHER ACCOUNT
                        DatabaseBankAccount.displayAllUserAccounts(currentUser, connection);
                        int senderAccountNumber = DatabaseBankAccount.selectAccount(currentUser,
                                DatabaseBankAccount.getListOfAccounts(currentUser, connection),
                                "\nChoose the account to transfer from. Enter the number that represents it:",
                                scanner);
                        BankAccount senderAccount = DatabaseBankAccount.loadBankAccount(connection, currentUser,
                                senderAccountNumber);
                        // Print information about the selected account
                        senderAccount.getAccountDetails();
                        System.out.println(senderAccount.getBalance());

                        System.out.print("\nEnter the amount to transfer: ");
                        BigDecimal transferAmount = CustomExceptions.getDecimalCheckException(scanner);
                        scanner.nextLine();

                        System.out.print("\nEnter the account number of the receiver: ");
                        int receiverAccountNumber = CustomExceptions.getIntCheckException(scanner);
                        scanner.nextLine();

                        AccountAndUserName receiverDetails = senderAccount
                                .findAccountByAccountNumber(eurobank.getUsers(), receiverAccountNumber);
                        if (receiverDetails != null) {
                            User receiverUser = receiverDetails.getUser();
                            BankAccount receiverAccount = receiverDetails.getAccount();
                            String receiverName = receiverDetails.getUserName();
                            System.out.println("Found account with Username: " + receiverName);

                            if (senderAccount.getBalance().compareTo(transferAmount) <= 0) {
                                System.out.println("Not enough money in your balance.");
                                break;
                            } else {

                                senderAccount.withdrawMoney(currentUser, senderAccount, transferAmount);
                                receiverAccount.depositMoney(receiverUser, receiverAccount, transferAmount);

                                System.out.printf("\nAccount %s, New Balance: %.2f",
                                        senderAccount.getAccountNumber(),
                                        senderAccount.checkBalance().setScale(2, RoundingMode.HALF_UP));
                                System.out.printf("\nAccount %s received the amount successfully.",
                                        receiverAccount.getAccountNumber());
                            }
                        } else {
                            // Account with the specified accountNumber was not found
                            System.out.println("Account not found.");
                            break;
                        }

                        break;

                    case 6:
                        Random random = new Random();
                        System.out.println("Enter the currency type for your account: ");
                        String currencytype = Converter.validateCurrency(scanner);
                        System.out.println("Enter the account type for your account: ");
                        String accounttype = scanner.nextLine();
                        BankAccount account1 = new BankAccount(random.nextInt(100000), currencytype, accounttype,
                                new BigDecimal("0"));
                        currentUser.addAccount(account1);
                        System.out.println("Account created successfully");
                        break;

                    case 7:
                        System.out.println("Thank you for using our banking system. Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid option. Please choose a valid option.");
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            scanner.close();
            return;
        } finally {
            scanner.close(); // Close the scanner in the finally block
        }

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
