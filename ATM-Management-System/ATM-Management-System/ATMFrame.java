import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ATMFrame extends JFrame {

    private ATMSystem atm;
    private ATMSystem.Account currentAccount;

    private int failedAttempts = 0;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private final Color navy = new Color(16, 29, 53);
    private final Color blue = new Color(55, 115, 220);
    private final Color light = new Color(242, 246, 252);
    private final Color white = Color.WHITE;

    // Customer login fields
    private JTextField accountField;
    private JPasswordField pinField;

    // Admin login fields
    private JTextField adminIdField;
    private JPasswordField adminPasswordField;

    // Dashboard balance label
    private JLabel balanceLabel;

    public ATMFrame() {
        atm = new ATMSystem();

        setTitle("ATM Simulation System");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createRolePanel(), "ROLE");
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createAdminLoginPanel(), "ADMIN_LOGIN");
        mainPanel.add(createDashboardPanel(), "DASHBOARD");
        mainPanel.add(createAdminDashboard(), "ADMIN_DASHBOARD");

        add(mainPanel);
        cardLayout.show(mainPanel, "ROLE");
    }

    // ------------------------------------------------
    // ROLE SELECTION SCREEN
    // ------------------------------------------------

    private JPanel createRolePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(navy);

        JPanel content = new JPanel();
        content.setBackground(navy);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("ATM SIMULATION SYSTEM");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(white);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Select your login type");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton customerButton = createButton("CUSTOMER LOGIN");
        JButton adminButton = createButton("ADMIN LOGIN");

        customerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        customerButton.addActionListener(e ->
                cardLayout.show(mainPanel, "LOGIN"));

        adminButton.addActionListener(e ->
                cardLayout.show(mainPanel, "ADMIN_LOGIN"));

        content.add(title);
        content.add(Box.createVerticalStrut(15));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(45));
        content.add(customerButton);
        content.add(Box.createVerticalStrut(20));
        content.add(adminButton);

        panel.add(content);
        return panel;
    }

    // ------------------------------------------------
    // CUSTOMER LOGIN SCREEN
    // ------------------------------------------------

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(light);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(white);
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        form.setPreferredSize(new Dimension(350, 350));

        JLabel title = new JLabel("CUSTOMER LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(navy);

        JLabel accountLabel = new JLabel("Account Number");
        accountField = new JTextField();
        styleField(accountField);

        JLabel pinLabel = new JLabel("PIN");
        pinField = new JPasswordField();
        styleField(pinField);

        JButton loginButton = createButton("LOGIN");
        JButton backButton = createButton("BACK");

        loginButton.addActionListener(e -> login());

        backButton.addActionListener(e -> {
            accountField.setText("");
            pinField.setText("");
            cardLayout.show(mainPanel, "ROLE");
        });

        form.add(title);
        form.add(accountLabel);
        form.add(accountField);
        form.add(pinLabel);
        form.add(pinField);
        form.add(loginButton);
        form.add(backButton);

        panel.add(form);
        return panel;
    }

    private void login() {
        String number = accountField.getText().trim();
        String pin = new String(pinField.getPassword());

        if (number.isEmpty() || pin.isEmpty()) {
            showMessage("Please enter account number and PIN.");
            return;
        }

        ATMSystem.Account account = atm.findAccountForGUI(number);

        if (account == null) {
            showMessage("Account not found or blocked.");
            return;
        }

        if (atm.verifyPinForGUI(account, pin)) {
            currentAccount = account;
            failedAttempts = 0;

            updateBalance();
            cardLayout.show(mainPanel, "DASHBOARD");
        } else {
            failedAttempts++;

            if (failedAttempts >= 3) {
                atm.blockAccountForGUI(account);
                showMessage("Three incorrect attempts. Account blocked.");
                failedAttempts = 0;
            } else {
                showMessage("Incorrect PIN. Attempts remaining: "
                        + (3 - failedAttempts));
            }
        }
    }

    // ------------------------------------------------
    // CUSTOMER DASHBOARD
    // ------------------------------------------------

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(light);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("CUSTOMER DASHBOARD");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(navy);

        balanceLabel = new JLabel("Welcome");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        balanceLabel.setForeground(blue);

        JPanel heading = new JPanel(new GridLayout(2, 1));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(balanceLabel);

        JButton logoutButton = createButton("LOG OUT");
        logoutButton.addActionListener(e -> logout());

        header.add(heading, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        JPanel actions = new JPanel(new GridLayout(3, 2, 15, 15));
        actions.setOpaque(false);

        addAction(actions, "CHECK BALANCE", this::showBalance);
        addAction(actions, "DEPOSIT MONEY", this::deposit);
        addAction(actions, "WITHDRAW MONEY", this::withdraw);
        addAction(actions, "TRANSFER MONEY", this::transfer);
        addAction(actions, "CHANGE PIN", this::changePin);
        addAction(actions, "TRANSACTION HISTORY", this::showTransactions);

        JButton detailsButton = createButton("ACCOUNT DETAILS");
        detailsButton.addActionListener(e -> showDetails());

        panel.add(header, BorderLayout.NORTH);
        panel.add(actions, BorderLayout.CENTER);
        panel.add(detailsButton, BorderLayout.SOUTH);

        return panel;
    }

    private void updateBalance() {
        if (currentAccount != null && balanceLabel != null) {
            balanceLabel.setText(
                    "Welcome, " + currentAccount.getName()
                            + " | Balance: ₹"
                            + currentAccount.getBalance()
            );
        }
    }

    private void showBalance() {
        updateBalance();

        showMessage("Available Balance: ₹"
                + currentAccount.getBalance());
    }

    // ------------------------------------------------
    // DEPOSIT
    // ------------------------------------------------

    private void deposit() {
        Double amount = askAmount("Enter amount to deposit:");

        if (amount == null) {
            return;
        }

        if (amount <= 0) {
            showMessage("Amount must be greater than zero.");
            return;
        }

        String result = atm.depositForGUI(currentAccount, amount);

        showMessage(result);
        updateBalance();
    }

    // ------------------------------------------------
    // WITHDRAW
    // ------------------------------------------------

    private void withdraw() {
        Double amount = askAmount("Enter amount to withdraw:");

        if (amount == null) {
            return;
        }

        if (amount <= 0) {
            showMessage("Amount must be greater than zero.");
            return;
        }

        String result = atm.withdrawForGUI(currentAccount, amount);

        showMessage(result);
        updateBalance();
    }

    // ------------------------------------------------
    // TRANSFER
    // ------------------------------------------------

    private void transfer() {
        String receiver = JOptionPane.showInputDialog(
                this, "Enter receiver's account number:");

        if (receiver == null || receiver.trim().isEmpty()) {
            return;
        }

        Double amount = askAmount("Enter amount to transfer:");

        if (amount == null) {
            return;
        }

        if (amount <= 0) {
            showMessage("Amount must be greater than zero.");
            return;
        }

        String result = atm.transferForGUI(
                currentAccount, receiver.trim(), amount);

        showMessage(result);
        updateBalance();
    }

    // ------------------------------------------------
    // CHANGE PIN
    // ------------------------------------------------

    private void changePin() {
        JPasswordField oldPinField = new JPasswordField();
        JPasswordField newPinField = new JPasswordField();
        JPasswordField confirmPinField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));

        panel.add(new JLabel("Current PIN:"));
        panel.add(oldPinField);

        panel.add(new JLabel("New PIN:"));
        panel.add(newPinField);

        panel.add(new JLabel("Confirm New PIN:"));
        panel.add(confirmPinField);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change PIN",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String oldPin = new String(oldPinField.getPassword());
        String newPin = new String(newPinField.getPassword());
        String confirmPin = new String(confirmPinField.getPassword());

        String result = atm.changePinForGUI(
                currentAccount, oldPin, newPin, confirmPin);

        showMessage(result);
    }

    // ------------------------------------------------
    // TRANSACTION HISTORY
    // ------------------------------------------------

    private void showTransactions() {
        List<ATMSystem.Transaction> list =
                atm.getTransactionsForGUI(currentAccount);

        if (list == null || list.isEmpty()) {
            showMessage("No transactions found.");
            return;
        }

        StringBuilder output = new StringBuilder();

        for (ATMSystem.Transaction transaction : list) {
            output.append(transaction.toString()).append("\n\n");
        }

        JTextArea textArea = new JTextArea(output.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 350));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Transaction History",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ------------------------------------------------
    // ACCOUNT DETAILS
    // ------------------------------------------------

    private void showDetails() {
        String details =
                "Account Number: " + currentAccount.getNumber()
                        + "\nName: " + currentAccount.getName()
                        + "\nAccount Type: " + currentAccount.getType()
                        + "\nMobile: " + currentAccount.getMobile()
                        + "\nEmail: " + currentAccount.getEmail()
                        + "\nBalance: ₹" + currentAccount.getBalance();

        showMessage(details);
    }

    // ------------------------------------------------
    // ADMIN LOGIN SCREEN
    // ------------------------------------------------

    private JPanel createAdminLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(light);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(white);
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        form.setPreferredSize(new Dimension(350, 300));

        JLabel title = new JLabel("ADMIN LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(navy);

        JLabel idLabel = new JLabel("Admin ID");
        adminIdField = new JTextField();
        styleField(adminIdField);

        JLabel passwordLabel = new JLabel("Password");
        adminPasswordField = new JPasswordField();
        styleField(adminPasswordField);

        JButton loginButton = createButton("LOGIN");
        JButton backButton = createButton("BACK");

        loginButton.addActionListener(e -> adminLogin());

        backButton.addActionListener(e -> {
            adminIdField.setText("");
            adminPasswordField.setText("");
            cardLayout.show(mainPanel, "ROLE");
        });

        form.add(title);
        form.add(idLabel);
        form.add(adminIdField);
        form.add(passwordLabel);
        form.add(adminPasswordField);
        form.add(loginButton);
        form.add(backButton);

        panel.add(form);
        return panel;
    }

    private void adminLogin() {
        String id = adminIdField.getText().trim();
        String password = new String(adminPasswordField.getPassword());

        if (id.equals("admin") && password.equals("admin123")) {
            adminIdField.setText("");
            adminPasswordField.setText("");
            cardLayout.show(mainPanel, "ADMIN_DASHBOARD");
        } else {
            showMessage("Invalid admin ID or password.");
        }
    }

    // ------------------------------------------------
    // ADMIN DASHBOARD
    // ------------------------------------------------

    private JPanel createAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(light);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(navy);

        JButton logout = createButton("LOG OUT");

        logout.addActionListener(e -> {
            adminIdField.setText("");
            adminPasswordField.setText("");
            cardLayout.show(mainPanel, "ROLE");
        });

        header.add(title, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        JPanel actions = new JPanel(new GridLayout(3, 2, 15, 15));
        actions.setOpaque(false);

        addAction(actions, "VIEW ALL ACCOUNTS", this::showAllAccounts);
        addAction(actions, "SEARCH ACCOUNT", this::searchAccount);
        addAction(actions, "CREATE ACCOUNT", this::createAccountGUI);
        addAction(actions, "DELETE ACCOUNT", this::deleteAccountGUI);
        addAction(actions, "VIEW ALL TRANSACTIONS", this::showAllTransactions);

        panel.add(header, BorderLayout.NORTH);
        panel.add(actions, BorderLayout.CENTER);

        return panel;
    }

    // ------------------------------------------------
    // ADMIN: VIEW ALL ACCOUNTS
    // ------------------------------------------------

    private void showAllAccounts() {
        List<ATMSystem.Account> accounts = atm.getAllAccounts();

        if (accounts == null || accounts.isEmpty()) {
            showMessage("No accounts available.");
            return;
        }

        StringBuilder output = new StringBuilder();

        for (ATMSystem.Account account : accounts) {
            output.append("Account Number: ")
                    .append(account.getNumber())
                    .append("\nName: ")
                    .append(account.getName())
                    .append("\nType: ")
                    .append(account.getType())
                    .append("\nMobile: ")
                    .append(account.getMobile())
                    .append("\nEmail: ")
                    .append(account.getEmail())
                    .append("\nBalance: ₹")
                    .append(account.getBalance())
                    .append("\n------------------------------\n");
        }

        showTextWindow("All Accounts", output.toString());
    }

    // ------------------------------------------------
    // ADMIN: SEARCH ACCOUNT
    // ------------------------------------------------

    private void searchAccount() {
        String number = JOptionPane.showInputDialog(
                this, "Enter account number to search:");

        if (number == null || number.trim().isEmpty()) {
            return;
        }

        ATMSystem.Account account =
                atm.findAccountForGUI(number.trim());

        if (account == null) {
            showMessage("Account not found or blocked.");
            return;
        }

        String details =
                "Account Number: " + account.getNumber()
                        + "\nName: " + account.getName()
                        + "\nAccount Type: " + account.getType()
                        + "\nMobile: " + account.getMobile()
                        + "\nEmail: " + account.getEmail()
                        + "\nBalance: ₹" + account.getBalance();

        showMessage(details);
    }

    // ------------------------------------------------
    // ADMIN: CREATE ACCOUNT
    // ------------------------------------------------

    private void createAccountGUI() {
        JTextField nameField = new JTextField();
        JTextField mobileField = new JTextField();
        JTextField emailField = new JTextField();

        String[] accountTypes = {"Savings", "Current"};
        JComboBox<String> typeBox = new JComboBox<>(accountTypes);

        JPasswordField newPinField = new JPasswordField();
        JTextField balanceField = new JTextField();

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));

        form.add(new JLabel("Customer Name:"));
        form.add(nameField);

        form.add(new JLabel("Mobile Number:"));
        form.add(mobileField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Account Type:"));
        form.add(typeBox);

        form.add(new JLabel("4-Digit PIN:"));
        form.add(newPinField);

        form.add(new JLabel("Opening Balance:"));
        form.add(balanceField);

        int option = JOptionPane.showConfirmDialog(
                this,
                form,
                "Create New Account",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String mobile = mobileField.getText().trim();
        String email = emailField.getText().trim();
        String type = (String) typeBox.getSelectedItem();
        String pin = new String(newPinField.getPassword());

        if (balanceField.getText().trim().isEmpty()) {
            showMessage("Please enter an opening balance.");
            return;
        }

        double balance;

        try {
            balance = Double.parseDouble(balanceField.getText().trim());
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid opening balance.");
            return;
        }

        if (name.isEmpty()) {
            showMessage("Customer name is required.");
            return;
        }

        if (!pin.matches("\\d{4}")) {
            showMessage("PIN must contain exactly four digits.");
            return;
        }

        if (balance < 0) {
            showMessage("Opening balance cannot be negative.");
            return;
        }

        String result = atm.createAccountForGUI(
                name, pin, type, mobile, email, balance);

        showMessage(result);
    }

    // ------------------------------------------------
    // ADMIN: DELETE ACCOUNT
    // ------------------------------------------------

    private void deleteAccountGUI() {
        String number = JOptionPane.showInputDialog(
                this, "Enter account number to delete:");

        if (number == null || number.trim().isEmpty()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete account "
                        + number.trim() + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String result = atm.deleteAccountForGUI(number.trim());

        showMessage(result);
    }

    // ------------------------------------------------
    // ADMIN: VIEW ALL TRANSACTIONS
    // ------------------------------------------------

    private void showAllTransactions() {
        List<ATMSystem.Transaction> transactions =
                atm.getAllTransactions();

        if (transactions == null || transactions.isEmpty()) {
            showMessage("No transactions found.");
            return;
        }

        StringBuilder output = new StringBuilder();

        for (ATMSystem.Transaction transaction : transactions) {
            output.append(transaction.toString())
                    .append("\n\n");
        }

        showTextWindow("All Transactions", output.toString());
    }

    // ------------------------------------------------
    // COMMON HELPER METHODS
    // ------------------------------------------------

    private Double askAmount(String message) {
        String input = JOptionPane.showInputDialog(this, message);

        if (input == null) {
            return null;
        }

        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid amount.");
            return null;
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "ATM System",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showTextWindow(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void logout() {
        currentAccount = null;
        accountField.setText("");
        pinField.setText("");
        failedAttempts = 0;

        cardLayout.show(mainPanel, "ROLE");
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(blue);
        button.setForeground(white);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void addAction(
            JPanel panel, String text, Runnable action) {

        JButton button = createButton(text);
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    // ------------------------------------------------
    // MAIN METHOD
    // ------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ATMFrame().setVisible(true);
        });
    }
}