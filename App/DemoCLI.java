import java.sql.*;
import java.util.Scanner;

public class DemoCLI {
    static final String URL  = System.getenv().getOrDefault("DB_URL",
        "jdbc:mysql://db:3306/bankmanagement?useSSL=false&allowPublicKeyRetrieval=true");
    static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    static final String PASS = System.getenv().getOrDefault("DB_PASS", "root");

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("✅ Connected to MySQL");
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("\n1) List users  2) Show balance  3) Deposit  4) Withdraw  0) Exit");
                System.out.print("Choice: ");
                String choice = sc.nextLine().trim();
                if (choice.equals("0")) break;
                switch (choice) {
                    case "1" -> listUsers(conn);
                    case "2" -> { System.out.print("Email: "); showBalance(conn, sc.nextLine().trim()); }
                    case "3" -> { System.out.print("Email: "); String e=sc.nextLine().trim();
                                  System.out.print("Amount: "); double a=Double.parseDouble(sc.nextLine().trim());
                                  deposit(conn,e,a); }
                    case "4" -> { System.out.print("Email: "); String e=sc.nextLine().trim();
                                  System.out.print("Amount: "); double a=Double.parseDouble(sc.nextLine().trim());
                                  withdraw(conn,e,a); }
                    default -> System.out.println("Invalid");
                }
            }
        }
    }

    static void listUsers(Connection c) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT user_id,email,first_name,last_name,balance FROM users");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.printf("%d | %s | %s %s | $%.2f%n",
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getDouble(5));
            }
        }
    }

    static void showBalance(Connection c, String email) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT balance FROM users WHERE email=?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) System.out.printf("Balance: $%.2f%n", rs.getDouble(1));
                else System.out.println("No such user.");
            }
        }
    }

    static void deposit(Connection c, String email, double amount) throws Exception {
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE users SET balance = balance + ? WHERE email=?")) {
                ps.setDouble(1, amount); ps.setString(2, email);
                if (ps.executeUpdate()==0) throw new SQLException("User not found.");
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO transactions(user_id, transaction_type, amount) " +
                    "SELECT user_id,'DEPOSIT',? FROM users WHERE email=?")) {
                ps.setDouble(1, amount); ps.setString(2, email); ps.executeUpdate();
            }
            c.commit();
            System.out.println("✅ Deposit complete.");
        } catch (Exception e) { c.rollback(); throw e; } finally { c.setAutoCommit(true); }
    }

    static void withdraw(Connection c, String email, double amount) throws Exception {
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE users SET balance = balance - ? WHERE email=? AND balance>=?")) {
                ps.setDouble(1, amount); ps.setString(2, email); ps.setDouble(3, amount);
                if (ps.executeUpdate()==0) throw new SQLException("Insufficient funds or user not found.");
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO transactions(user_id, transaction_type, amount) " +
                    "SELECT user_id,'WITHDRAWAL',? FROM users WHERE email=?")) {
                ps.setDouble(1, amount); ps.setString(2, email); ps.executeUpdate();
            }
            c.commit();
            System.out.println("✅ Withdrawal complete.");
        } catch (Exception e) { c.rollback(); throw e; } finally { c.setAutoCommit(true); }
    }
}
