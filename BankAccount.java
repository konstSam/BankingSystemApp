package BankingSystemApp;

import java.math.BigDecimal;
import java.util.ArrayList;

public class BankAccount {
    private int accountNumber;
    private String currencyType;
    private String accountType;
    private BigDecimal balance;

    // Constructor
    public BankAccount(int accountNumber, String currencyType, String accountType, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.currencyType = currencyType;
        this.accountType = accountType;
        this.balance = balance;
    }

    // Getter & setter
    public int getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCurrencyType() {
        return this.currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

    public void depositMoney(User user, BankAccount userAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("\nInvalid deposit amount");
            return; // Exit the method if the deposit amount is invalid
        }
        // Check if the userAccount is associated with the specified user
        if (user.getAccounts().contains(userAccount)) {
            BigDecimal currentBalance = userAccount.getBalance();
            BigDecimal newBalance = currentBalance.add(amount);
            userAccount.setBalance(newBalance);
            System.out.println(
                    "Deposit successful. Deposited: " + amount.toPlainString() + " " + userAccount.getCurrencyType());
        } else {
            System.out.println("Account not found for the specified user.");
        }
    }

    public void withdrawMoney(User user, BankAccount userAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("\nInvalid withdraw amount");
            return; // Exit the method if the deposit amount is invalid
        }
        // Check if the userAccount is associated with the specified user
        if (user.getAccounts().contains(userAccount)) {
            BigDecimal currentBalance = userAccount.getBalance();
            if (currentBalance.compareTo(amount) >= 0) {
                BigDecimal newBalance = currentBalance.subtract(amount);
                userAccount.setBalance(newBalance);
                System.out.println("Withdraw successful. Withdrawn: " + amount.toPlainString() + " "
                        + userAccount.getCurrencyType());
            } else {
                System.out.println("Not enough money in your balance.\n");
            }
        } else {
            System.out.println("Account not found for the specified user.");
        }
    }

    public BigDecimal checkBalance() {
        return balance;
    }

    public void getAccountDetails() {
        // Print information about the selected account
        System.out.println("Selected Account: " + accountType + ", Currency: "
                + currencyType);
        System.out.println("Account Number: " + accountNumber);
    }

    public AccountAndUserName findAccountByAccountNumber(ArrayList<User> users, int accountNumberToFind) {
        for (User user : users) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getAccountNumber() == accountNumberToFind) {
                    return new AccountAndUserName(user, account, user.getName()); // Found the account with the
                                                                                  // specified accountNumber
                }
            }
        }
        return null; // Account with the specified accountNumber not found
    }

    // Class to extract the user, bankaccount & username from the json
    public class AccountAndUserName {
        private User user;
        private BankAccount account;
        private String userName;

        public AccountAndUserName(User user, BankAccount account, String userName) {
            this.user = user;
            this.account = account;
            this.userName = userName;
        }

        public User getUser() {
            return user;
        }

        public BankAccount getAccount() {
            return account;
        }

        public String getUserName() {
            return userName;
        }
    }
}
