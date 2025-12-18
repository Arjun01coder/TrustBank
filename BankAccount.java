import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankAccount {

    private String name;
    private int accountNumber;
    private int pin;
    private double balance;
    private boolean active;
    private ArrayList<String> transactions;

    private static final DateTimeFormatter dtf =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ---------------- CONSTRUCTOR ----------------
    public BankAccount(String name, int accountNumber, int pin) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = 0.0;
        this.active = true;
        this.transactions = new ArrayList<>();
        addTransaction("Account created successfully");
    }

    // ---------------- BASIC GETTERS ----------------
    public String getName() {
        return name;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public int getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public ArrayList<String> getTransactions() {
        return new ArrayList<>(transactions);
    }

    // ---------------- SETTERS (for file load) ----------------
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addTransaction(String msg) {
        String time = LocalDateTime.now().format(dtf);
        transactions.add("[" + time + "] " + msg);
    }

    // ---------------- ACCOUNT STATUS ----------------
    public void blockAccount() {
        active = false;
        addTransaction("Account blocked by admin");
    }

    public void unblockAccount() {
        active = true;
        addTransaction("Account unblocked by admin");
    }

    // ---------------- BANK OPERATIONS ----------------
    public void deposit(double amount) {
        if (!active || amount <= 0) return;

        balance += amount;
        addTransaction("Deposited Rs. " + amount);
    }

    public void withdraw(double amount) {
        if (!active || amount <= 0 || amount > balance) return;

        balance -= amount;
        addTransaction("Withdrew Rs. " + amount);
    }

    public boolean validatePin(int pin) {
        return this.pin == pin;
    }

    // ---------------- INTEREST CALCULATION ----------------
    public double calculateInterest(double rate, int years) {
        return (balance * rate * years) / 100;
    }

    // ---------------- PRINT TRANSACTIONS ----------------
    public void printTransactions() {
        System.out.println("\n--- Transaction History ---");
        for (String t : transactions) {
            System.out.println(t);
        }
        System.out.println("----------------------------");
    }
}
