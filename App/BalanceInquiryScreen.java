package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceInquiryScreen extends Application {

    private int userID; // Store the userID passed from login
    private double balance = 0.0; // User's balance

    public BalanceInquiryScreen(int userID) {
        this.userID = userID; // Initialize userID
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Balance Inquiry");

        // Fetch the balance from the database for the logged-in user
        fetchBalance();

        // Label to display the balance
        Label balanceLabel = new Label("Your current balance is: $" + String.format("%.2f", balance));
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");

        // Back Button to return to transaction screen
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;");
        backButton.setOnAction(e -> {
            TransactionScreen transactionScreen = new TransactionScreen(userID); // Pass userID back to TransactionScreen
            transactionScreen.start(primaryStage);
        });

        // Create a top-right box for the back button
        HBox topRightBox = new HBox(backButton);
        topRightBox.setAlignment(Pos.TOP_LEFT);
        topRightBox.setPadding(new Insets(10));

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(balanceLabel, backButton);

        // Set up the scene
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to fetch the user's balance from the database
    private void fetchBalance() {
        Connection conn = DataConnection.getConnection();
        if (conn == null) {
            System.out.println("Error: Unable to connect to the database.");
            return;
        }

        String query = "SELECT Balance FROM users WHERE UserID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userID); // Use the userID passed to the class
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("Balance"); // Fetch the balance
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
