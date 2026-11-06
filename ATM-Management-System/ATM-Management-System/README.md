# ATM Management System

A Java Swing desktop ATM simulation with separate customer and administrator interfaces.

## Requirements
- JDK 11 or later
- Visual Studio Code
- Extension Pack for Java (recommended)

## Project files
- `ATMFrame.java` — Swing graphical user interface
- `ATMSystem.java` — account, PIN, deposit, withdrawal, transfer and transaction logic

## Run in VS Code
1. Open this folder in VS Code.
2. Open the integrated terminal (**Terminal → New Terminal**).
3. Compile both Java files:

   ```bash
   javac ATMSystem.java ATMFrame.java
   ```

4. Run the application:

   ```bash
   java ATMSystem
   ```

You can also run `ATMFrame` as the entry point after compilation:

```bash
java ATMFrame
```

## Demo customer accounts

| Account number | PIN | Customer |
|---|---|---|
| 100001 | 1234 | Rahul Sharma |
| 100002 | 2222 | Priya Verma |

## Demo administrator login
- Admin ID: `admin`
- Password: `admin123`

## Important notes
- This is an educational simulation. It is not connected to a real bank or payment network.
- Account and transaction data are stored in memory, so changes are lost when the application closes.
- The demo credentials are hard-coded and must not be used for a real financial application.
