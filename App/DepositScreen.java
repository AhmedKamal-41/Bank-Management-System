package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DepositScreen extends Application {

    private int userID; // Store the userID passed from login
    private String accountNumber = "12345678"; // Simulate the user's account number

    public DepositScreen(int userID) {
        this.userID = userID; // Initialize userID
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Deposit");

        // Create a label for instructions
        Label instructionLabel = new Label("Enter the amount you want to deposit:");
        instructionLabel.setStyle("-fx-font-size: 16px;");

        // Create a TextField for amount input
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setMaxWidth(200);

        // Confirm Button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> {
            String amountText = amountField.getText();
            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    showAlert("Invalid Amount", "Please enter a positive number for the amount.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid number.");
                return;
            }

            Connection conn = DataConnection.getConnection();
            if (conn == null) {
                showAlert("Connection Error", "Failed to connect to the database.");
                return;
            }

            try {
                conn.setAutoCommit(false); // Start a transaction

                String transactionID = TransactionIDGenerator.generateTransactionID(accountNumber); // Generate a transaction ID

                // 1. Update the user's balance by adding the deposit amount
                String updateBalanceSql = "UPDATE users SET Balance = Balance + ? WHERE UserID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSql)) {
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, userID); // Use the correct userID
                    int count = updateStmt.executeUpdate();
                    if (count == 0) {
                        throw new SQLException("Failed to update the balance.");
                    }
                }

                // 2. Insert a new transaction record into the transactions table
                String insertTransactionSql = "INSERT INTO transactions (UserID, TransactionType, Amount, TransactionDate) VALUES (?, 'Deposit', ?, NOW())";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertTransactionSql)) {
                    insertStmt.setInt(1, userID); // Use the correct userID
                    insertStmt.setDouble(2, amount);
                    insertStmt.executeUpdate();
                }

                // Commit transaction
                conn.commit();
                showTransactionMessage(transactionID, amountText);

                // Ask the user if they want to perform another transaction
                askForAnotherTransaction(primaryStage);

            } catch (SQLException ex) {
                // Handle exceptions and rollback in case of error
                try {
                    conn.rollback();
                } catch (SQLException se2) {
                    se2.printStackTrace();
                }
                showAlert("Transaction Failed", "An error occurred during the transaction: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        });

        // Back Button to return to transaction screen
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;");
        backButton.setOnAction(e -> {
            TransactionScreen transactionScreen = new TransactionScreen(userID); // Pass the userID back to TransactionScreen
            transactionScreen.start(primaryStage);
        });

        // Layout setup
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(instructionLabel, amountField, confirmButton, backButton);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Function to show an alert with the transaction number
    private void showTransactionMessage(String transactionID, String amount) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Transaction Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your deposit is successful!\nTransaction ID: " + transactionID +
                "\nAmount: $" + amount);
        alert.showAndWait();
    }

    // Function to show error alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Ask the user if they want to perform another transaction
    private void askForAnotherTransaction(Stage primaryStage) {
        // Create a confirmation alert
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Transaction Complete");
        confirmAlert.setHeaderText("Would you like to make another transaction?");
        confirmAlert.setContentText("Choose your option:");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        // Set the button types
        confirmAlert.getButtonTypes().setAll(yesButton, noButton);

        // Show the alert and wait for a response
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                // User chose 'Yes', go back to the transaction screen
                TransactionScreen transactionScreen = new TransactionScreen(userID); // Use correct userID
                transactionScreen.start(primaryStage);
            } else if (response == noButton) {
                // User chose 'No', close the application
                primaryStage.close();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

