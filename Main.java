package BankingSystemApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import BankingSystemApp.BankAccount.AccountAndUserName;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {
    public static void main(String[] args) {
        // Create a bank
        Bank eurobank = new Bank();

        // Create User 1 and their accounts
        User user1 = new User(1, "Kostas");
        BankAccount account1_1 = new BankAccount(12345, "EUR", "Savings", new BigDecimal("1000"));
        BankAccount account1_2 = new BankAccount(12243, "EUR", "Checking", new BigDecimal("200.44"));
        BankAccount account1_3 = new BankAccount(12246, "USD", "Checking", new BigDecimal("0.44"));
        BankAccount account1_4 = new BankAccount(12247, "USD", "Savings", new BigDecimal("400"));
        user1.addAccount(account1_1);
        user1.addAccount(account1_2);
        user1.addAccount(account1_3);
        user1.addAccount(account1_4);

        // Create User 2 and their accounts
        User user2 = new User(2, "Emma");
        BankAccount account2_1 = new BankAccount(1215567, "EUR", "Savings", new BigDecimal("41000.64"));
        BankAccount account2_2 = new BankAccount(12243542, "USD", "Checking", new BigDecimal("843.67"));
        user2.addAccount(account2_1);
        user2.addAccount(account2_2);

        // Create User 3 and their accounts
        User user3 = new User(3, "Maria");
        BankAccount account3_1 = new BankAccount(1215324, "EUR", "Savings", new BigDecimal("2100.11"));
        BankAccount account3_2 = new BankAccount(1223422, "EUR", "Checking", new BigDecimal("1343.22"));
        user3.addAccount(account3_1);
        user3.addAccount(account3_2);

        // Add customers to the bank
        eurobank.addUser(user1);
        eurobank.addUser(user2);
        eurobank.addUser(user3);

        Scanner scanner = new Scanner(System.in);
        try {
            // Create a Scanner for user input
            User currentUser = null;
            boolean userExists = false;
            while (!userExists) {
                try {
                    System.out.print("Enter your username: ");
                    String username = scanner.nextLine();
                    currentUser = User.findByUsername(eurobank, username);
                    userExists = true;
                } catch (CustomExceptions.UserNotFoundException e) {
                    System.out.println(e.getMessage());

                }
            }
            // Display user's accounts
            System.out.println("Welcome, " + currentUser.getName() + "!");
            System.out.println("Your Accounts: ");
            int i = 1;
            for (BankAccount account : currentUser.getAccounts()) {
                System.out.println(
                        account.getAccountType() + " (Account Number: " + account.getAccountNumber() + ", "
                                + account.getCurrencyType() + ") (" + i++
                                + ")");
            }
            boolean isRunning = true;
            // Provide options for transactions
            while (isRunning) {
                System.out.println("\nChoose an option:");
                System.out.println("1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Check Balance");
                System.out.println("4. Convert to another Currency");
                System.out.println("5. Tranfer to another Account");
                System.out.println("6. Exit");

                // int option = scanner.nextInt();
                int option = CustomExceptions.getIntCheckException(scanner);
                // remove newline character
                scanner.nextLine();

                switch (option) {
                    case 1:
                        BankAccount depositAccount = chooseAccount(currentUser, currentUser.getAccounts(),
                                "\nChoose the account to deposit into. Enter the number that represents it: ", scanner);
                        // Print information about the selected account
                        depositAccount.getAccountDetails();

                        System.out.print("\nEnter the amount to deposit: ");
                        BigDecimal depositAmount = CustomExceptions.getDecimalCheckException(scanner);
                        scanner.nextLine();
                        depositAccount.depositMoney(currentUser, depositAccount, depositAmount);
                        System.out.println(
                                "New Balance: " + depositAccount.checkBalance().setScale(2, RoundingMode.HALF_UP));
                        break;

                    case 2:
                        BankAccount withdrawAccount = chooseAccount(currentUser, currentUser.getAccounts(),
                                "\nChoose the account to withraw. Enter the number that represents it: ", scanner);

                        // Print information about the selected account
                        withdrawAccount.getAccountDetails();

                        System.out.print("\nEnter the amount to withdraw: ");
                        BigDecimal withdrawalAmount = CustomExceptions.getDecimalCheckException(scanner);
                        scanner.nextLine();
                        withdrawAccount.withdrawMoney(currentUser, withdrawAccount, withdrawalAmount);
                        System.out.println(
                                "New Balance: " + withdrawAccount.checkBalance().setScale(2, RoundingMode.HALF_UP));
                        break;

                    case 3:
                        BankAccount checkAccount = chooseAccount(currentUser, currentUser.getAccounts(),
                                "\nChoose the account to check its balance. Enter the number that represents it: ",
                                scanner);
                        // Print information about the selected account
                        checkAccount.getAccountDetails();
                        System.out.println("Balance: " + checkAccount.checkBalance().setScale(2, RoundingMode.HALF_UP));
                        break;

                    case 4:
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

                    case 5:
                        BankAccount senderAccount = chooseAccount(currentUser, currentUser.getAccounts(),
                                "\nChoose the account to transfer from. Enter the number that represents it: ",
                                scanner);

                        // Print information about the selected account
                        senderAccount.getAccountDetails();
                        senderAccount.getBalance();
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
