package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AccountIDScreen extends Application {

    private static int accountNumber = 0; // Initial account number, this will be fetched from the database

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Account Created");

        // Fetch the current highest UserID from the database
        accountNumber = getMaxUserIDFromDatabase() + 1; // Increment the highest ID by 1

        // Display the generated account number
        Label accountLabel = new Label("Your account number is: " + accountNumber);
        accountLabel.setStyle("-fx-font-size: 16px;");

        // Ask if the user wants to proceed with transactions
        Label askTransactionLabel = new Label("Do you want to proceed with transactions?");
        askTransactionLabel.setStyle("-fx-font-size: 14px;");

        // Button to proceed with transactions
        Button yesButton = new Button("Yes");
        yesButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        yesButton.setOnAction(e -> {
            TransactionScreen transactionScreen = new TransactionScreen(accountNumber);
            transactionScreen.start(primaryStage); // Redirect to TransactionScreen
        });

        // Button to decline and save user data
        Button noButton = new Button("No");
        noButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        noButton.setOnAction(e -> {
            // Get the generated userID
            int userID = getNextUserID();

            // Create an instance of the SignUp class and save the user data
            SignUp signUp = new SignUp();

            // Example form data (replace these with actual form inputs)
            String email = "user@example.com";  // Replace with actual email from form
            String password = " ";  // Actual password from form
            String firstName = " ";  // First Name
            String lastName = " ";  // Last Name
            String middleName = " ";  // Middle Name
            String prefix = " ";  // Prefix like Mr., Mrs.
            String phoneNumber = " ";  // Phone number
            LocalDate dob = LocalDate.of(1990, 1, 1);  // Date of birth
            String streetAddress = " ";  // Street Address
            String streetAddress2 = " ";  // Address Line 2
            String city = " ";  // City
            String state = " ";  // State
            String zip = " ";  // Zip Code
            String ssn = " ";  // SSN
            String country = " ";  // Country
            String maritalStatus = " ";  // Marital status
            String greenCardNumber = " ";  // Green card number
            String citizenship = " ";  // Citizenship

            // Call saveUserData to save all data along with userID
            signUp.saveUserData(userID, email, password, firstName, lastName, middleName, prefix, phoneNumber, dob,
                                streetAddress, streetAddress2, city, state, zip, ssn, country, maritalStatus, citizenship, greenCardNumber);

            // Show a thank-you message
            showThankYouMessage(primaryStage);
        });

        // Layout for the screen
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(accountLabel, askTransactionLabel, yesButton, noButton);

        // Set the scene and display the stage
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to query the database and find the maximum UserID
    private int getMaxUserIDFromDatabase() {
        int maxUserID = 0;

        String sql = "SELECT MAX(UserID) FROM users"; // Query to find the maximum UserID

        try (Connection connection = DataConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                maxUserID = resultSet.getInt(1); // Get the highest UserID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maxUserID;
    }

    // Static method to get the next user ID (account number)
    public static int getNextUserID() {
        return accountNumber;
    }

    // Method to show a thank-you message and close the stage
    private void showThankYouMessage(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thank You!");
        alert.setHeaderText(null);
        alert.setContentText("Thank you for using Java Bank. Have a great day!");
        alert.showAndWait();
        primaryStage.close(); // Close the window
    }

    public static void main(String[] args) {
        launch(args);
    }
}