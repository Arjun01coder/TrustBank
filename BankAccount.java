import java.util.ArrayList;

public class BankAccount {

    private String name;
    private int accountNumber;
    private int pin;
    private double balance;
    private boolean active;
    private ArrayList<String> transactions;

    // --------------------- CONSTRUCTOR ---------------------
    public BankAccount(String name, int accountNumber, int pin) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = 0.0;
        this.active = true;
        this.transactions = new ArrayList<>();
        transactions.add("Account created successfully.");
    }

    // --------------------- GETTERS ---------------------
    public String getName() {
        return name;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public int getPin() {
        return pin;
    }

    public boolean isActive() {
        return active;
    }

    public double getBalance() {
        return balance;
    }

    // Return a safe copy of transactions
    public ArrayList<String> getTransactions() {
        return new ArrayList<>(transactions);
    }

    // --------------------- SETTERS (needed for file loading) ---------------------
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addTransaction(String t) {
        this.transactions.add(t);
    }

    // --------------------- ACCOUNT STATUS ---------------------
    public void blockAccount() {
        this.active = false;
        transactions.add("Account blocked.");
    }

    // --------------------- BANK OPERATIONS ---------------------
    public void deposit(double amount) {
        if (!active) {
            System.out.println("Account is blocked. Cannot deposit.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
            return;
        }

        balance += amount;
        transactions.add("Deposited Rs. " + amount);
        System.out.println("Deposit successful! Rs. " + amount + " added.");
    }

    public void withdraw(double amount) {
        if (!active) {
            System.out.println("Account is blocked. Cannot withdraw.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
            return;
        }

        if (amount > balance) {
            System.out.println("Insufficient balance!");
            return;
        }

        balance -= amount;
        transactions.add("Withdrew Rs. " + amount);
        System.out.println("Withdrawal successful! Rs. " + amount + " deducted.");
    }

    public boolean validatePin(int pin) {
        return this.pin == pin;
    }

    // --------------------- PRINT TRANSACTIONS ---------------------
    public void printTransactions() {
        System.out.println("\n--- Transaction History ---");

        for (String t : transactions) {
            System.out.println(t);
        }

        System.out.println("---------------------------\n");
    }
}
