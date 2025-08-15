import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConnection {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Read from environment for online demo; fallback to your local defaults
            String url = System.getenv().getOrDefault("BMS_DB_URL", "jdbc:mysql://127.0.0.1:3306/bankmanagment");
            String user = System.getenv().getOrDefault("BMS_DB_USER", "root");
            String password = System.getenv().getOrDefault("BMS_DB_PASS", "");

            // Establish connection
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the MySQL database!");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add mysql-connector-j to your dependencies.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database. Check URL/credentials and DB availability.");
            e.printStackTrace();
        }
        return connection;
    }
}
