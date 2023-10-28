package BankingSystemApp;

import java.util.ArrayList;

public class User {
    private int userID;
    private String userName;
    private ArrayList<BankAccount> accounts;

    // Constructor
    public User(int userID, String userName) {
        this.userID = userID;
        this.userName = userName;
        this.accounts = new ArrayList<>();
    }

    // getter for the private variables
    public int getUserID() {
        return this.userID;
    }

    public String getName() {
        return this.userName;
    }

    public ArrayList<BankAccount> getAccounts() {
        return accounts;
    }

    // setter
    public void addAccount(BankAccount account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
        }
    }

    // method to find a user by username
    static User findByUsername(Bank bank, String userName) throws CustomExceptions.UserNotFoundException {
        for (User customer : bank.getUsers()) {
            if (customer.getName().equalsIgnoreCase(userName)) {
                return customer;
            }
        }
        throw new CustomExceptions.UserNotFoundException("User not found. Try again.");
    }

    static void displayUserAccountsInfo(User currentUser) {
        System.out.println("Your Accounts: ");
        int k = 1;
        for (BankAccount account : currentUser.getAccounts()) {
            System.out.println(
                    account.getAccountType() + " (Account Number: " + account.getAccountNumber() + ", "
                            + account.getCurrencyType() + ") (" + k++
                            + ")");
        }
    }

}