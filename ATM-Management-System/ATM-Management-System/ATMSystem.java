
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingUtilities;

public class ATMSystem {

    static final Scanner sc = new Scanner(System.in);

    static final List<Account> accounts = new ArrayList<>();
    static final List<Transaction> transactions = new ArrayList<>();

    static int nextAccount = 100003;
    static int nextTxn = 1;

    // ------------------------------------------------
    // MAIN METHOD
    // ------------------------------------------------

    // Initialize demo accounts whenever the system is created.
    public ATMSystem() {
        if (accounts.isEmpty()) {
            accounts.add(new Account(
                    "100001",
                    "Rahul Sharma",
                    "1234",
                    "Savings",
                    "9876543210",
                    "rahul@example.com",
                    10000
            ));

            accounts.add(new Account(
                    "100002",
                    "Priya Verma",
                    "2222",
                    "Savings",
                    "9876501234",
                    "priya@example.com",
                    15000
            ));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ATMFrame().setVisible(true);
        });
    }

    // ------------------------------------------------
    // FIND ACCOUNT
    // ------------------------------------------------

    Account find(String number) {

        for (Account a : accounts) {
            if (a.number.equals(number) && !a.deleted) {
                return a;
            }
        }

        return null;
    }

    // ------------------------------------------------
    // RECORD TRANSACTION
    // ------------------------------------------------

    void record(Account account, String type,
                double amount, String description) {

        Transaction transaction = new Transaction(
                nextTxn++,
                account.number,
                type,
                amount,
                account.balance,
                description,
                LocalDateTime.now()
        );

        transactions.add(transaction);
    }

    // ------------------------------------------------
    // GUI: FIND ACCOUNT
    // ------------------------------------------------

    Account findAccountForGUI(String number) {

        Account a = find(number);

        if (a == null || a.blocked) {
            return null;
        }

        return a;
    }

    // ------------------------------------------------
    // GUI: VERIFY PIN
    // ------------------------------------------------

    boolean verifyPinForGUI(Account account, String pin) {

        if (account == null || account.blocked || account.deleted) {
            return false;
        }

        return account.checkPin(pin);
    }

    // ------------------------------------------------
    // GUI: BLOCK ACCOUNT
    // ------------------------------------------------

    void blockAccountForGUI(Account account) {

        if (account != null) {
            account.blocked = true;
        }
    }

    // ------------------------------------------------
    // GUI: DEPOSIT
    // ------------------------------------------------

    String depositForGUI(Account account, double amount) {

        if (account == null || account.deleted || account.blocked) {
            return "Invalid or blocked account.";
        }

        if (amount <= 0) {
            return "Amount must be greater than zero.";
        }

        account.deposit(amount);

        record(account, "DEPOSIT", amount, "Cash deposit");

        return "Deposit successful!\nNew Balance: ₹"
                + account.balance;
    }

    // ------------------------------------------------
    // GUI: WITHDRAW
    // ------------------------------------------------

    String withdrawForGUI(Account account, double amount) {

        if (account == null || account.deleted || account.blocked) {
            return "Invalid or blocked account.";
        }

        if (amount <= 0) {
            return "Amount must be greater than zero.";
        }

        if (amount > account.balance) {
            return "Insufficient balance.";
        }

        account.withdraw(amount);

        record(account, "WITHDRAWAL", amount, "Cash withdrawal");

        return "Withdrawal successful!\nRemaining Balance: ₹"
                + account.balance;
    }

    // ------------------------------------------------
    // GUI: TRANSFER
    // ------------------------------------------------

    String transferForGUI(
            Account sender,
            String receiverNumber,
            double amount) {

        if (sender == null || sender.deleted || sender.blocked) {
            return "Invalid sender account.";
        }

        Account receiver = find(receiverNumber);

        if (receiver == null || receiver.blocked) {
            return "Receiver account not found or blocked.";
        }

        if (sender.number.equals(receiver.number)) {
            return "Cannot transfer money to the same account.";
        }

        if (amount <= 0) {
            return "Amount must be greater than zero.";
        }

        if (amount > sender.balance) {
            return "Insufficient balance.";
        }

        sender.withdraw(amount);
        receiver.deposit(amount);

        record(sender, "TRANSFER SENT", amount,
                "Transferred to account " + receiver.number);

        record(receiver, "TRANSFER RECEIVED", amount,
                "Received from account " + sender.number);

        return "Transfer successful!\n"
                + "Amount: ₹" + amount
                + "\nReceiver: " + receiver.name
                + "\nRemaining Balance: ₹" + sender.balance;
    }

    // ------------------------------------------------
    // GUI: CHANGE PIN
    // ------------------------------------------------

    String changePinForGUI(
            Account account,
            String oldPin,
            String newPin,
            String confirmPin) {

        if (account == null || account.deleted || account.blocked) {
            return "Invalid account.";
        }

        if (!account.checkPin(oldPin)) {
            return "Incorrect current PIN.";
        }

        if (newPin == null || !newPin.matches("\\d{4}")) {
            return "New PIN must contain exactly four digits.";
        }

        if (!newPin.equals(confirmPin)) {
            return "New PIN and confirmation PIN do not match.";
        }

        if (oldPin.equals(newPin)) {
            return "New PIN must be different from old PIN.";
        }

        account.pin = newPin;

        record(account, "PIN CHANGE", 0,
                "Account PIN changed");

        return "PIN changed successfully.";
    }

    // ------------------------------------------------
    // GUI: CUSTOMER TRANSACTIONS
    // ------------------------------------------------

    List<Transaction> getTransactionsForGUI(Account account) {

        List<Transaction> result = new ArrayList<>();

        if (account == null) {
            return result;
        }

        for (Transaction t : transactions) {

            if (t.accountNumber.equals(account.number)) {
                result.add(t);
            }
        }

        return result;
    }

    // ------------------------------------------------
    // GUI: GET ALL ACCOUNTS
    // ------------------------------------------------

    List<Account> getAllAccounts() {

        List<Account> result = new ArrayList<>();

        for (Account a : accounts) {

            if (!a.deleted) {
                result.add(a);
            }
        }

        return result;
    }

    // ------------------------------------------------
    // GUI: GET ALL TRANSACTIONS
    // ------------------------------------------------

    List<Transaction> getAllTransactions() {

        return new ArrayList<>(transactions);
    }

    // ------------------------------------------------
    // GUI: CREATE ACCOUNT
    // ------------------------------------------------

    String createAccountForGUI(
            String name,
            String pin,
            String type,
            String mobile,
            String email,
            double openingBalance) {

        if (name == null || name.isBlank()) {
            return "Customer name is required.";
        }

        if (pin == null || !pin.matches("\\d{4}")) {
            return "PIN must contain exactly four digits.";
        }

        if (openingBalance < 0) {
            return "Opening balance cannot be negative.";
        }

        if (type == null ||
                (!type.equalsIgnoreCase("Savings")
                        && !type.equalsIgnoreCase("Current"))) {

            return "Invalid account type.";
        }

        if (mobile == null || !mobile.matches("\\d{10}")) {
            return "Mobile number must contain 10 digits.";
        }

        if (email == null || !email.contains("@")) {
            return "Please enter a valid email address.";
        }

        Account newAccount = new Account(
                String.valueOf(nextAccount++),
                name.trim(),
                pin,
                type,
                mobile.trim(),
                email.trim(),
                openingBalance
        );

        accounts.add(newAccount);

        return "Account created successfully!\n"
                + "Account Number: " + newAccount.number
                + "\nCustomer Name: " + newAccount.name
                + "\nOpening Balance: ₹" + newAccount.balance;
    }

    // ------------------------------------------------
    // GUI: DELETE ACCOUNT
    // ------------------------------------------------

    String deleteAccountForGUI(String number) {

        Account account = find(number);

        if (account == null) {
            return "Account not found.";
        }

        if (account.balance > 0) {
            return "Cannot delete an account with positive balance.";
        }

        account.deleted = true;

        return "Account deleted successfully.";
    }

    // ------------------------------------------------
    // ACCOUNT CLASS
    // ------------------------------------------------

    static class Account {

        private String number;
        private String name;
        private String pin;
        private String type;
        private String mobile;
        private String email;

        private double balance;

        private boolean blocked = false;
        private boolean deleted = false;

        Account(
                String number,
                String name,
                String pin,
                String type,
                String mobile,
                String email,
                double balance) {

            this.number = number;
            this.name = name;
            this.pin = pin;
            this.type = type;
            this.mobile = mobile;
            this.email = email;
            this.balance = balance;
        }

        // Getters

        public String getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getMobile() {
            return mobile;
        }

        public String getEmail() {
            return email;
        }

        public double getBalance() {
            return balance;
        }

        public boolean isBlocked() {
            return blocked;
        }

        // Check PIN

        boolean checkPin(String enteredPin) {
            return pin.equals(enteredPin);
        }

        // Deposit money

        void deposit(double amount) {
            balance += amount;
        }

        // Withdraw money

        boolean withdraw(double amount) {

            if (amount <= 0 || amount > balance) {
                return false;
            }

            balance -= amount;
            return true;
        }
    }

    // ------------------------------------------------
    // TRANSACTION CLASS
    // ------------------------------------------------

    static class Transaction {

        private int transactionId;
        private String accountNumber;
        private String type;
        private double amount;
        private double balanceAfter;
        private String description;
        private LocalDateTime dateTime;

        Transaction(
                int transactionId,
                String accountNumber,
                String type,
                double amount,
                double balanceAfter,
                String description,
                LocalDateTime dateTime) {

            this.transactionId = transactionId;
            this.accountNumber = accountNumber;
            this.type = type;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
            this.description = description;
            this.dateTime = dateTime;
        }

        public int getTransactionId() {
            return transactionId;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public double getBalanceAfter() {
            return balanceAfter;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        @Override
        public String toString() {

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            return "Transaction ID: " + transactionId
                    + "\nAccount Number: " + accountNumber
                    + "\nType: " + type
                    + "\nAmount: ₹" + amount
                    + "\nBalance After: ₹" + balanceAfter
                    + "\nDescription: " + description
                    + "\nDate: " + dateTime.format(formatter);
        }
    }
}