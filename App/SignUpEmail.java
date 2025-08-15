package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpEmail {

    public void showSignUpEmail(Stage primaryStage) {
        primaryStage.setTitle("Sign Up - Email and Password");

        // Create the Back button
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;");
        backButton.setOnAction(e -> {
            Main main = new Main();
            main.start(primaryStage);
        });

        // Create a top-right box for the back button
        HBox topRightBox = new HBox(backButton);
        topRightBox.setAlignment(Pos.TOP_LEFT);
        topRightBox.setPadding(new Insets(10));

        // Create the layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50;"); // Fancy Navy Background

        Label instructionLabel = new Label("Enter your email and create a password");
        instructionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create Password");
        passwordField.setMaxWidth(300);

        Label errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red;");
        errorMessage.setVisible(false); // Initially hidden

        Button continueButton = new Button("Continue");
        continueButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        continueButton.setOnAction(e -> {
            if (validateFields(emailField, passwordField, errorMessage)) {
                if (isEmailExists(emailField.getText())) {
                    errorMessage.setText("Email already exists. Please use a different email.");
                    errorMessage.setVisible(true);
                } else if (isEmailExistsInSignup(emailField.getText())) {
                    // If email exists in the signup table, move to the next screen without saving
                    SignUp signUpPage = new SignUp();
                    signUpPage.showLoginPage(primaryStage, emailField.getText(), passwordField.getText()); // Pass email and password
                } else {
                    saveUserToDatabase(emailField.getText(), passwordField.getText());
                    // After saving, move to the next screen
                    SignUp signUpPage = new SignUp();
                    signUpPage.showLoginPage(primaryStage, emailField.getText(), passwordField.getText()); // Pass email and password
                }
            }
        });


        // Align the button to the bottom left corner
        HBox buttonBox = new HBox(continueButton);
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 20));

        // Add everything to the layout
        layout.getChildren().addAll(topRightBox, instructionLabel, emailField, passwordField, errorMessage, buttonBox);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private boolean validateFields(TextField emailField, PasswordField passwordField, Label errorMessage) {
        boolean valid = true;
        StringBuilder errorText = new StringBuilder();

        // Email validation
        String email = emailField.getText();
        if (email.isEmpty()) {
            valid = false;
            errorText.append("Email is required. ");
        } else if (!email.contains("@") || !email.contains(".")) {
            valid = false;
            errorText.append("Invalid email format. ");
        }

        // Password validation
        String password = passwordField.getText();
        if (password.isEmpty()) {
            valid = false;
            errorText.append("Password is required. ");
        } else if (password.length() < 8) {
            valid = false;
            errorText.append("Password must be at least 8 characters long. ");
        }

        if (!valid) {
            errorMessage.setText(errorText.toString());
            errorMessage.setVisible(true);
        } else {
            errorMessage.setVisible(false);
        }

        return valid;
    }

    // Method to check if the email already exists in the 'users' table
    private boolean isEmailExists(String email) {
        Connection connection = DataConnection.getConnection();
        if (connection != null) {
            String query = "SELECT 1 FROM users WHERE Email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next(); // Return true if email exists, false otherwise
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Method to check if the email already exists in the 'signup' table
    private boolean isEmailExistsInSignup(String email) {
        Connection connection = DataConnection.getConnection();
        if (connection != null) {
            String query = "SELECT 1 FROM signup WHERE Email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next(); // Return true if email exists in signup table
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Method to save user information into the 'signup' table
    private void saveUserToDatabase(String email, String password) 
    {
        Connection connection = DataConnection.getConnection();
        if (connection != null) {
            String query = "INSERT INTO signup (Email, Password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate(); // Save user to the database
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}




