import java.util.*;
import java.io.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static final String ACC_FILE = "accounts.txt";
    static final String TRANS_FILE = "transactions.txt";

    static ArrayList<BankAccount> accounts = new ArrayList<>();
    static int nextAccNumber = 1001;

    public static void main(String[] args) {

        loadData();  // Load stored data at startup

        printHeader("WELCOME TO TRUSTBANK SYSTEM");

        while (true) {
            printBox("MAIN MENU");
            System.out.println("  1. Create New Account");
            System.out.println("  2. Login to Account");
            System.out.println("  3. Admin Login");
            System.out.println("  4. Exit");
            printLine();
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    createAccount();
                    break;

                case 2:
                    userLogin();
                    break;

                case 3:
                    adminLogin();
                    break;

                case 4:
                    saveData();
                    printFooter("THANK YOU FOR USING TRUSTBANK");
                    return;

                default:
                    printError("Invalid choice! Please try again.");
            }
        }
    }

    // --------------------------- STYLISH UI METHODS ---------------------------

    public static void printHeader(String title) {
        System.out.println("\n===============================================");
        System.out.println("         " + title);
        System.out.println("===============================================\n");
    }

    public static void printFooter(String msg) {
        System.out.println("\n===============================================");
        System.out.println("         " + msg);
        System.out.println("===============================================\n");
    }

    public static void printBox(String title) {
        System.out.println("\n+-------------------------------------------+");
        System.out.println("|               " + title + "               |");
        System.out.println("+-------------------------------------------+");
    }

    public static void printLine() {
        System.out.println("+-------------------------------------------+");
    }

    public static void printSuccess(String msg) {
        System.out.println("\n✔ " + msg + "\n");
    }

    public static void printError(String msg) {
        System.out.println("\n✖ " + msg + "\n");
    }

    // --------------------------- CREATE ACCOUNT ---------------------------

    public static void createAccount() {
        sc.nextLine();
        printBox("CREATE NEW ACCOUNT");

        System.out.print("Enter your full name: ");
        String name = sc.nextLine();

        System.out.print("Set a 4-digit PIN: ");
        int pin = sc.nextInt();

        BankAccount acc = new BankAccount(name, nextAccNumber, pin);
        accounts.add(acc);

        printSuccess("Account created successfully!");
        System.out.println("Your Account Number: " + nextAccNumber);

        nextAccNumber++;
        saveData();
    }

    // --------------------------- USER LOGIN ---------------------------

    public static void userLogin() {
        printBox("USER LOGIN");

        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();

        System.out.print("Enter PIN: ");
        int pin = sc.nextInt();

        for (BankAccount acc : accounts) {
            if (acc.getAccountNumber() == accNo && acc.validatePin(pin) && acc.isActive()) {
                printSuccess("Login Successful! Welcome, " + acc.getName());
                userMenu(acc);
                return;
            }
        }

        printError("Invalid account number or PIN, or account blocked.");
    }

    // --------------------------- USER MENU ---------------------------

    public static void userMenu(BankAccount acc) {
        while (true) {

            printBox("USER DASHBOARD");
            System.out.println("Name: " + acc.getName());
            System.out.println("Account No: " + acc.getAccountNumber());
            System.out.println("Balance: Rs. " + acc.getBalance());
            printLine();

            System.out.println("  1. Deposit Money");
            System.out.println("  2. Withdraw Money");
            System.out.println("  3. View Balance");
            System.out.println("  4. View Transactions");
            System.out.println("  5. Transfer Money");
            System.out.println("  6. Export Passbook");
            System.out.println("  7. Logout");
            printLine();

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter amount to deposit: Rs. ");
                    acc.deposit(sc.nextDouble());
                    saveData();
                    break;

                case 2:
                    System.out.print("Enter amount to withdraw: Rs. ");
                    acc.withdraw(sc.nextDouble());
                    saveData();
                    break;

                case 3:
                    printBox("ACCOUNT BALANCE");
                    System.out.println("Current Balance: Rs. " + acc.getBalance());
                    break;

                case 4:
                    printBox("TRANSACTION HISTORY");
                    acc.printTransactions();
                    break;

                case 5:
                    transferMoney(acc);
                    saveData();
                    break;

                case 6:
                    exportPassbook(acc);
                    break;

                case 7:
                    printSuccess("Logged out successfully.");
                    return;

                default:
                    printError("Invalid option. Try again!");
            }
        }
    }

    // --------------------------- MONEY TRANSFER ---------------------------

    public static void transferMoney(BankAccount sender) {
        printBox("MONEY TRANSFER");

        System.out.print("Enter receiver's account number: ");
        int receiverAccNo = sc.nextInt();

        BankAccount receiver = null;

        for (BankAccount acc : accounts) {
            if (acc.getAccountNumber() == receiverAccNo) {
                receiver = acc;
                break;
            }
        }

        if (receiver == null) {
            printError("Receiver account not found.");
            return;
        }

        System.out.print("Enter amount to transfer: Rs. ");
        double amount = sc.nextDouble();

        if (amount > sender.getBalance()) {
            printError("Insufficient balance!");
            return;
        }

        sender.withdraw(amount);
        receiver.deposit(amount);

        printSuccess("Transfer Successful!");
        System.out.println("Sent Rs. " + amount + " to " + receiver.getName());
    }

    // --------------------------- EXPORT PASSBOOK ---------------------------

    public static void exportPassbook(BankAccount acc) {
        try {
            String filename = "Passbook_" + acc.getAccountNumber() + ".txt";
            FileWriter writer = new FileWriter(filename);

            writer.write("======== TRUSTBANK PASSBOOK ========\n");
            writer.write("Account No: " + acc.getAccountNumber() + "\n");
            writer.write("Name: " + acc.getName() + "\n");
            writer.write("----------------------------------\n");

            for (String t : acc.getTransactions()) {
                writer.write(t + "\n");
            }

            writer.write("----------------------------------\n");
            writer.write("Current Balance: Rs. " + acc.getBalance() + "\n");
            writer.write("==================================");

            writer.close();

            printSuccess("Passbook saved as: " + filename);

        } catch (Exception e) {
            printError("Error exporting passbook.");
        }
    }

    // --------------------------- ADMIN LOGIN ---------------------------

    public static void adminLogin() {
        printBox("ADMIN LOGIN");

        System.out.print("Enter Admin Username: ");
        String user = sc.next();

        System.out.print("Enter Admin Password: ");
        String pass = sc.next();

        if (user.equals("admin") && pass.equals("1234")) {
            printSuccess("Admin login successful!");
            adminMenu();
        } else {
            printError("Incorrect admin credentials.");
        }
    }

    // --------------------------- ADMIN MENU ---------------------------

    public static void adminMenu() {
        while (true) {

            printBox("ADMIN PANEL");
            System.out.println("  1. View All Accounts");
            System.out.println("  2. Search Account");
            System.out.println("  3. Block an Account");
            System.out.println("  4. View Total Bank Balance");
            System.out.println("  5. Logout");
            printLine();

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    printBox("ALL ACCOUNTS");
                    for (BankAccount acc : accounts) {
                        System.out.println(acc.getAccountNumber() + " - " + acc.getName());
                    }
                    break;

                case 2:
                    System.out.print("Enter Account Number: ");
                    int accNo = sc.nextInt();
                    boolean found = false;

                    for (BankAccount acc : accounts) {
                        if (acc.getAccountNumber() == accNo) {
                            System.out.println("Account Found: " + acc.getName());
                            found = true;
                        }
                    }

                    if (!found) printError("Account not found.");
                    break;

                case 3:
                    System.out.print("Enter Account Number to Block: ");
                    int blockNo = sc.nextInt();
                    boolean blocked = false;

                    for (BankAccount acc : accounts) {
                        if (acc.getAccountNumber() == blockNo) {
                            acc.blockAccount();
                            saveData();
                            blocked = true;
                            printSuccess("Account Blocked!");
                        }
                    }

                    if (!blocked) printError("Account not found.");
                    break;

                case 4:
                    double total = 0;
                    for (BankAccount acc : accounts) total += acc.getBalance();

                    printBox("TOTAL BALANCE IN BANK");
                    System.out.println("Rs. " + total);
                    break;

                case 5:
                    printSuccess("Logged out of admin panel.");
                    return;

                default:
                    printError("Invalid option!");
            }
        }
    }

    // --------------------------- FILE STORAGE ---------------------------

    public static void saveData() {
        try {
            FileWriter accWriter = new FileWriter(ACC_FILE);
            for (BankAccount acc : accounts) {
                accWriter.write(acc.getAccountNumber() + "," +
                        acc.getName() + "," +
                        acc.getPin() + "," +
                        acc.getBalance() + "," +
                        acc.isActive() + "\n");
            }
            accWriter.close();

            FileWriter transWriter = new FileWriter(TRANS_FILE);
            for (BankAccount acc : accounts) {
                for (String t : acc.getTransactions()) {
                    transWriter.write(acc.getAccountNumber() + "," + t + "\n");
                }
            }
            transWriter.close();

        } catch (Exception e) {
            printError("Error saving data.");
        }
    }

    public static void loadData() {
        try {
            File accFile = new File(ACC_FILE);
            File transFile = new File(TRANS_FILE);

            if (!accFile.exists() || !transFile.exists()) return;

            BufferedReader accReader = new BufferedReader(new FileReader(accFile));

            String line;

            while ((line = accReader.readLine()) != null) {
                String[] p = line.split(",");

                int accNo = Integer.parseInt(p[0]);
                String name = p[1];
                int pin = Integer.parseInt(p[2]);
                double bal = Double.parseDouble(p[3]);
                boolean active = Boolean.parseBoolean(p[4]);

                BankAccount acc = new BankAccount(name, accNo, pin);
                acc.setBalance(bal);
                if (!active) acc.blockAccount();

                accounts.add(acc);

                if (accNo >= nextAccNumber)
                    nextAccNumber = accNo + 1;
            }
            accReader.close();

            BufferedReader transReader = new BufferedReader(new FileReader(transFile));

            while ((line = transReader.readLine()) != null) {
                String[] p = line.split(",", 2);

                int accNo = Integer.parseInt(p[0]);
                String transaction = p[1];

                for (BankAccount acc : accounts) {
                    if (acc.getAccountNumber() == accNo) {
                        acc.addTransaction(transaction);
                    }
                }
            }

            transReader.close();

        } catch (Exception e) {
            printError("Error loading data.");
        }
    }
}
