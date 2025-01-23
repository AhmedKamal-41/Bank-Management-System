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
            
            // Database URL, username, password
            String url = "jdbc:mysql://127.0.0.1:3306/bankmanagment"; // Replace with your database name
            String user = "root";
            String password = ""; // Add your password if any
            
            // Establish connection
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the MySQL database!");

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error connecting to MySQL");
            e.printStackTrace();
        }
        return connection;
    }
}

