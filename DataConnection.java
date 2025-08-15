// application/DataConnection.java
package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConnection {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Prefer environment variables (for Gitpod/containers), fallback to local defaults
            String host = System.getenv().getOrDefault("DB_HOST", "127.0.0.1");
            String port = System.getenv().getOrDefault("DB_PORT", "3306");
            String dbName = System.getenv().getOrDefault("DB_NAME", "bankmanagement");
            String user = System.getenv().getOrDefault("DB_USER", "root");
            String password = System.getenv().getOrDefault("DB_PASS", "");

            String url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true",
                host, port, dbName
            );

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to MySQL: " + url);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error connecting to MySQL");
            e.printStackTrace();
        }
        return connection;
    }
}
