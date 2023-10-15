package BankingSystemApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank {
    private ArrayList<User> users;
    private int bankid;
    private String name;

    public Bank(int bankid, String name) {
        this.bankid = bankid;
        this.name = name;
        this.users = new ArrayList<>();
    }

    public int getBankID() {
        return this.bankid;
    }

    public String getBankName() {
        return this.name;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    // return a copy of the list to prevent unintended modifications outside
    public List<User> getUsers2() {
        return Collections.unmodifiableList(users);
    }

    public void displayUserInfo() {
        for (User user : users) {
            System.out.println("\nUser ID: " + user.getUserID());
            System.out.println("UserName: " + user.getName());
            ArrayList<BankAccount> accounts = user.getAccounts();
            System.out.printf("%s Accounts: \n", user.getName());
            for (BankAccount account : accounts) {
                System.out.println("\nAccount Number: " + account.getAccountNumber());
                System.out.println("Account Type: " + account.getAccountType());
                System.out.printf("Balance: %.3f Euros \n", account.checkBalance());
            }
            System.out.println("\n-----------------------------");
        }
    }
}
