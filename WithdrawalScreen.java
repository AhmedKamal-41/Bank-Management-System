package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WithdrawalScreen extends Application {

    private int userID; // Store userID
    private String accountNumber = "12345678"; // Simulate the user's account number

    public WithdrawalScreen(int userID) {
        this.userID = userID; // Initialize userID
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Withdrawal");

        // Create a label for instructions
        Label instructionLabel = new Label("Enter the amount you want to withdraw:");
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

                // 1. Retrieve current balance of the user
                String balanceQuery = "SELECT Balance FROM users WHERE UserID = ?";
                double currentBalance = 0;
                try (PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery)) {
                    balanceStmt.setInt(1, userID); // Use userID passed from login
                    ResultSet rs = balanceStmt.executeQuery();
                    if (rs.next()) {
                        currentBalance = rs.getDouble("Balance");
                        if (currentBalance < amount) {
                            showAlert("Insufficient Funds", "You do not have enough funds to complete this transaction.");
                            conn.rollback();
                            return;
                        }
                    } else {
                        showAlert("User Not Found", "The user account was not found.");
                        conn.rollback();
                        return;
                    }
                }

                // 2. Update the balance
                String updateBalanceSql = "UPDATE users SET Balance = Balance - ? WHERE UserID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSql)) {
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, userID); // Use userID
                    updateStmt.executeUpdate();
                }

                // 3. Insert a new transaction record into the transactions table
                String insertTransactionSql = "INSERT INTO transactions (UserID, TransactionType, Amount, TransactionDate) VALUES (?, 'Withdrawal', ?, NOW())";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertTransactionSql)) {
                    insertStmt.setInt(1, userID); // Use userID
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
            TransactionScreen transactionScreen = new TransactionScreen(userID); // Pass userID back to TransactionScreen
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your withdrawal is successful!\nTransaction ID: " + transactionID +
                "\nAmount: $" + amount);
        alert.showAndWait();
    }

    // Function to ask if the user wants another transaction
    private void askForAnotherTransaction(Stage primaryStage) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Transaction Complete");
        confirmAlert.setHeaderText("Would you like to make another transaction?");
        confirmAlert.setContentText("Choose your option:");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        confirmAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                TransactionScreen transactionScreen = new TransactionScreen(userID); // Perform another transaction
                transactionScreen.start(primaryStage);
            } else if (response == noButton) {
                primaryStage.close(); // Exit the application
            }
        });
    }

    // Function to show error alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
