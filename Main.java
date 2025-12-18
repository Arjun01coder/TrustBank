import java.util.*;
import java.io.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static final String ACC_FILE = "accounts.txt";
    static final String TRANS_FILE = "transactions.txt";

    static ArrayList<BankAccount> accounts = new ArrayList<>();
    static int nextAccNumber = 1001;

    // ---------------- UI HELPERS ----------------
    static void header(String t) {
        System.out.println("\n===============================================");
        System.out.println("          " + t);
        System.out.println("===============================================");
    }

    static void line() {
        System.out.println("-----------------------------------------------");
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {

        loadData();
        header("WELCOME TO TRUSTBANK");

        while (true) {
            System.out.println("\n1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> createAccount();
                case 2 -> userLogin();
                case 3 -> adminLogin();
                case 4 -> {
                    saveData();
                    header("THANK YOU");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    // ---------------- CREATE ACCOUNT ----------------
    static void createAccount() {
        sc.nextLine();
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Set PIN: ");
        int pin = sc.nextInt();

        BankAccount acc = new BankAccount(name, nextAccNumber, pin);
        accounts.add(acc);

        System.out.println("Account Created. Account No: " + nextAccNumber);
        nextAccNumber++;
        saveData();
    }

    // ---------------- USER LOGIN ----------------
    static void userLogin() {
        System.out.print("Account No: ");
        int accNo = sc.nextInt();
        System.out.print("PIN: ");
        int pin = sc.nextInt();

        for (BankAccount acc : accounts) {
            if (acc.getAccountNumber() == accNo &&
                acc.validatePin(pin) &&
                acc.isActive()) {
                userMenu(acc);
                return;
            }
        }
        System.out.println("Login failed");
    }

    // ---------------- USER MENU ----------------
    static void userMenu(BankAccount acc) {

        while (true) {
            header("USER DASHBOARD");
            System.out.println("Name: " + acc.getName());
            System.out.println("Balance: Rs. " + acc.getBalance());
            line();
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Interest Calculator");
            System.out.println("5. Loan EMI Calculator");
            System.out.println("6. Transactions");
            System.out.println("7. Export Passbook");
            System.out.println("8. Logout");
            System.out.print("Choice: ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> {
                    System.out.print("Amount: ");
                    acc.deposit(sc.nextDouble());
                    saveData();
                }
                case 2 -> {
                    System.out.print("Amount: ");
                    acc.withdraw(sc.nextDouble());
                    saveData();
                }
                case 3 -> transferMoney(acc);
                case 4 -> interestFeature(acc);
                case 5 -> emiCalculator();
                case 6 -> acc.printTransactions();
                case 7 -> exportPassbook(acc);
                case 8 -> { return; }
                default -> System.out.println("Invalid option");
            }
        }
    }

    // ---------------- TRANSFER ----------------
    static void transferMoney(BankAccount sender) {
        System.out.print("Receiver Acc No: ");
        int rno = sc.nextInt();
        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        for (BankAccount r : accounts) {
            if (r.getAccountNumber() == rno && r.isActive()) {
                if (amt <= sender.getBalance()) {
                    sender.withdraw(amt);
                    r.deposit(amt);
                    sender.addTransaction("Sent Rs. " + amt + " to " + r.getName());
                    r.addTransaction("Received Rs. " + amt + " from " + sender.getName());
                    saveData();
                }
                return;
            }
        }
        System.out.println("Transfer failed");
    }

    // ---------------- INTEREST ----------------
    static void interestFeature(BankAccount acc) {
        System.out.print("Rate (%): ");
        double r = sc.nextDouble();
        System.out.print("Years: ");
        int y = sc.nextInt();

        double interest = acc.calculateInterest(r, y);
        System.out.println("Interest Earned: Rs. " + interest);
    }

    // ---------------- EMI ----------------
    static void emiCalculator() {
        System.out.print("Loan Amount: ");
        double p = sc.nextDouble();
        System.out.print("Rate (%): ");
        double r = sc.nextDouble() / (12 * 100);
        System.out.print("Months: ");
        int n = sc.nextInt();

        double emi = (p * r * Math.pow(1 + r, n)) /
                     (Math.pow(1 + r, n) - 1);

        System.out.println("Monthly EMI: Rs. " + String.format("%.2f", emi));
    }

    // ---------------- ADMIN ----------------
    static void adminLogin() {
        System.out.print("Admin User: ");
        String u = sc.next();
        System.out.print("Password: ");
        String p = sc.next();

        if (u.equals("admin") && p.equals("1234")) adminMenu();
        else System.out.println("Invalid admin login");
    }

    static void adminMenu() {
        while (true) {
            header("ADMIN PANEL");
            System.out.println("1. View Accounts");
            System.out.println("2. Unblock Account");
            System.out.println("3. Delete Account");
            System.out.println("4. Total Bank Balance");
            System.out.println("5. Logout");
            System.out.print("Choice: ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> accounts.forEach(a ->
                        System.out.println(a.getAccountNumber() + " | " + a.getName()));
                case 2 -> unblockAccount();
                case 3 -> deleteAccount();
                case 4 -> {
                    double sum = 0;
                    for (BankAccount a : accounts) sum += a.getBalance();
                    System.out.println("Total Balance: Rs. " + sum);
                }
                case 5 -> { return; }
            }
        }
    }

    static void unblockAccount() {
        System.out.print("Account No: ");
        int no = sc.nextInt();
        for (BankAccount a : accounts) {
            if (a.getAccountNumber() == no) {
                a.unblockAccount();
                saveData();
            }
        }
    }

    static void deleteAccount() {
        System.out.print("Account No: ");
        int no = sc.nextInt();
        accounts.removeIf(a -> a.getAccountNumber() == no);
        saveData();
    }

    // ---------------- PASSBOOK ----------------
    static void exportPassbook(BankAccount acc) {
        try (FileWriter w =
                     new FileWriter("Passbook_" + acc.getAccountNumber() + ".txt")) {

            w.write("TRUSTBANK PASSBOOK\n");
            w.write("Account: " + acc.getAccountNumber() + "\n");
            w.write("Name: " + acc.getName() + "\n\n");

            for (String t : acc.getTransactions())
                w.write(t + "\n");

            w.write("\nBalance: Rs. " + acc.getBalance());
        } catch (Exception e) {
            System.out.println("Error exporting passbook");
        }
    }

    // ---------------- FILE STORAGE ----------------
    static void saveData() {
        try (FileWriter a = new FileWriter(ACC_FILE);
             FileWriter t = new FileWriter(TRANS_FILE)) {

            for (BankAccount acc : accounts) {
                a.write(acc.getAccountNumber() + "," + acc.getName() + "," +
                        acc.getPin() + "," + acc.getBalance() + "," +
                        acc.isActive() + "\n");

                for (String tr : acc.getTransactions())
                    t.write(acc.getAccountNumber() + "," + tr + "\n");
            }
        } catch (Exception ignored) {}
    }

    static void loadData() {
        try {
            if (!new File(ACC_FILE).exists()) return;

            BufferedReader ar = new BufferedReader(new FileReader(ACC_FILE));
            String l;

            while ((l = ar.readLine()) != null) {
                String[] p = l.split(",");
                BankAccount acc = new BankAccount(p[1],
                        Integer.parseInt(p[0]),
                        Integer.parseInt(p[2]));
                acc.setBalance(Double.parseDouble(p[3]));
                if (!Boolean.parseBoolean(p[4])) acc.blockAccount();
                accounts.add(acc);

                nextAccNumber = Math.max(nextAccNumber,
                        acc.getAccountNumber() + 1);
            }
            ar.close();
        } catch (Exception ignored) {}
    }
}
