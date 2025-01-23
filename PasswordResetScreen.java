package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PasswordResetScreen extends Application {

    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Button resetPasswordButton;
    private Label errorLabel;  // To show errors

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Forgot Password");

        // Instruction Label
        Label instructionLabel = new Label("Enter your email and last 4 digits of SSN to reset your password:");
        instructionLabel.setStyle("-fx-font-size: 16px;");

        // TextField for Email Input
        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setMaxWidth(250);

        // TextField for SSN Input (last 4 digits)
        TextField ssnField = new TextField();
        ssnField.setPromptText("Last 4 digits of SSN");
        ssnField.setMaxWidth(250);

        // Password Field for New Password (initially hidden)
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setMaxWidth(250);
        newPasswordField.setVisible(false);  // Initially hidden

        // Password Field for Confirm Password (initially hidden)
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");
        confirmPasswordField.setMaxWidth(250);
        confirmPasswordField.setVisible(false);  // Initially hidden

        // Error Label for password mismatch
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");  // Set error label text color to red
        errorLabel.setVisible(false);  // Initially hidden

        // Reset Button (Initially hidden)
        resetPasswordButton = new Button("Reset Password");
        resetPasswordButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        resetPasswordButton.setVisible(false);  // Initially hidden
        resetPasswordButton.setOnAction(e -> {
            String email = emailField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Password length validation and match validation
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter the new password in both fields.");
            } else if (newPassword.length() < 8) {
                showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 8 characters long.");
            } else if (!newPassword.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match!");
                errorLabel.setVisible(true);  // Show the error label
            } else {
                // If passwords match, reset the password in the database
                updatePassword(primaryStage, email, newPassword);
            }
        });

        // Check Data Button (to verify email and SSN)
        Button checkDataButton = new Button("Check Data");
        checkDataButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        checkDataButton.setOnAction(e -> {
            String email = emailField.getText();
            String ssn = ssnField.getText();
            if (email.isEmpty() || ssn.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in both email and SSN.");
            } else if (ssn.length() != 4 || !ssn.matches("\\d{4}")) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter the last 4 digits of your SSN.");
            } else {
                // Check if the email and SSN match a record in the database
                checkEmailAndSSN(primaryStage, email, ssn);
            }
        });

        // Back Button to return to login screen
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;");
        backButton.setOnAction(e -> {
            Main loginPage = new Main();
            loginPage.start(primaryStage);
        });

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(instructionLabel, emailField, ssnField, checkDataButton, newPasswordField, confirmPasswordField, errorLabel, resetPasswordButton, backButton);

        // Set up the scene
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Function to show an alert
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to check if the email and SSN are correct
    private void checkEmailAndSSN(Stage primaryStage, String email, String ssn) {
        String query = "SELECT * FROM users WHERE Email = ? AND SSN LIKE ?";
        try (Connection connection = DataConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Prepare the query with the provided email and last 4 digits of SSN
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, "%" + ssn);  // Use LIKE to match the last 4 digits of SSN

            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                // User found, show the password fields and reset button
                newPasswordField.setVisible(true);
                confirmPasswordField.setVisible(true);
                resetPasswordButton.setVisible(true);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Your details are correct. Please enter a new password.");
            } else {
                // User not found or incorrect SSN
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid email or SSN.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while verifying your details.");
        }
    }

    // Method to update the password in the database
    private void updatePassword(Stage primaryStage, String email, String newPassword) {
        String updateQuery = "UPDATE users SET Password = ? WHERE Email = ?";
        try (Connection connection = DataConnection.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            // Update the password
            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, email);
            updateStmt.executeUpdate();

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your password has been reset successfully.");
            Main loginPage = new Main();
            loginPage.start(primaryStage);  // Redirect to login page
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while resetting your password.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

