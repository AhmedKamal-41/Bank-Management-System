package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentScreen extends Application {

    private int userID; // The userID of the logged-in user
    private String accountNumber = "12345678"; // Simulate the user's account number

    public PaymentScreen(int userID) {
        this.userID = userID; // Initialize userID
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Transfers & Payments");

        // Label for instructions
        Label instructionLabel = new Label("Enter transfer/payment details:");
        instructionLabel.setStyle("-fx-font-size: 16px;");

        // TextField for recipient details
        TextField recipientField = new TextField();
        recipientField.setPromptText("Recipient's email or phone number");
        recipientField.setMaxWidth(250);

        // TextField for amount to transfer/pay
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setMaxWidth(250);

        // Confirm Button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> {
            // Logic to handle transfer or payment
            String recipient = recipientField.getText();
            String amountText = amountField.getText();
            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    showAlert("Invalid Amount", "Please enter a positive amount.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid number for the amount.");
                return;
            }

            // Perform the transfer
            performTransfer(recipient, amount);
        });

        // Back Button to return to transaction screen
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;");
        backButton.setOnAction(e -> {
            TransactionScreen transactionScreen = new TransactionScreen(userID); // Pass userID back
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
        layout.getChildren().addAll(instructionLabel, recipientField, amountField, confirmButton, backButton);

        // Set up the scene
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to perform the transfer between accounts
    private void performTransfer(String recipientDetail, double amount) {
        Connection conn = DataConnection.getConnection();
        if (conn == null) {
            showAlert("Connection Error", "Failed to connect to the database.");
            return;
        }

        try {
            conn.setAutoCommit(false); // Begin transaction

            // 1. Check if the recipient exists
            String recipientQuery = "SELECT UserID, Balance FROM users WHERE Email = ? OR PhoneNumber = ?";
            int recipientID = -1;
            double recipientBalance = 0.0;

            try (PreparedStatement recipientStmt = conn.prepareStatement(recipientQuery)) {
                recipientStmt.setString(1, recipientDetail);
                recipientStmt.setString(2, recipientDetail);
                ResultSet rs = recipientStmt.executeQuery();
                if (rs.next()) {
                    recipientID = rs.getInt("UserID");
                    recipientBalance = rs.getDouble("Balance");
                } else {
                    showAlert("Recipient Not Found", "No user found with the provided email or phone number.");
                    conn.rollback();
                    return;
                }
            }

            // 2. Check if the logged-in user has enough balance
            String userBalanceQuery = "SELECT Balance FROM users WHERE UserID = ?";
            double userBalance = 0.0;
            try (PreparedStatement userStmt = conn.prepareStatement(userBalanceQuery)) {
                userStmt.setInt(1, userID);
                ResultSet rs = userStmt.executeQuery();
                if (rs.next()) {
                    userBalance = rs.getDouble("Balance");
                    if (userBalance < amount) {
                        showAlert("Insufficient Funds", "You do not have enough balance to complete this transaction.");
                        conn.rollback();
                        return;
                    }
                } else {
                    showAlert("User Not Found", "The logged-in user was not found.");
                    conn.rollback();
                    return;
                }
            }

            // 3. Deduct amount from the logged-in user's balance
            String deductBalanceQuery = "UPDATE users SET Balance = Balance - ? WHERE UserID = ?";
            try (PreparedStatement deductStmt = conn.prepareStatement(deductBalanceQuery)) {
                deductStmt.setDouble(1, amount);
                deductStmt.setInt(2, userID);
                deductStmt.executeUpdate();
            }

            // 4. Add amount to the recipient's balance
            String addBalanceQuery = "UPDATE users SET Balance = Balance + ? WHERE UserID = ?";
            try (PreparedStatement addStmt = conn.prepareStatement(addBalanceQuery)) {
                addStmt.setDouble(1, amount);
                addStmt.setInt(2, recipientID);
                addStmt.executeUpdate();
            }

            // 5. Log the transaction for the logged-in user
            String transactionID = TransactionIDGenerator.generateTransactionID(accountNumber);
            String logTransactionSender = "INSERT INTO transactions (UserID, TransactionType, Amount, TransactionDate) " +
                    "VALUES (?, 'Transfer', ?, NOW())";
            try (PreparedStatement logSenderStmt = conn.prepareStatement(logTransactionSender)) {
                logSenderStmt.setInt(1, userID); // Log for the logged-in user
                logSenderStmt.setDouble(2, amount);
                logSenderStmt.executeUpdate();
            }

            // 6. Log the transaction for the recipient
            String logTransactionRecipient = "INSERT INTO transactions (UserID, TransactionType, Amount, TransactionDate) " +
                    "VALUES (?, 'Transfer Received', ?, NOW())";
            try (PreparedStatement logRecipientStmt = conn.prepareStatement(logTransactionRecipient)) {
                logRecipientStmt.setInt(1, recipientID); // Log for the recipient
                logRecipientStmt.setDouble(2, amount);
                logRecipientStmt.executeUpdate();
            }

            // Commit transaction
            conn.commit();

            showTransactionMessage(transactionID, recipientDetail, String.valueOf(amount));

        } catch (SQLException ex) {
            // Handle exceptions and rollback transaction if an error occurs
            try {
                conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            showAlert("Transaction Failed", "An error occurred during the transaction.");
            ex.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    // Function to show an alert with the transaction number
    private void showTransactionMessage(String transactionID, String recipient, String amount) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Transaction Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your transaction is successful!\nTransaction ID: " + transactionID +
                "\nRecipient: " + recipient + "\nAmount: $" + amount);
        alert.showAndWait();
    }

    // Function to show an error alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
