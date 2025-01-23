package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Java Bank Login");

        // Info Pane (Left)
        VBox infoPane = new VBox(10);
        infoPane.setPadding(new Insets(20));
        infoPane.setAlignment(Pos.TOP_LEFT);
        infoPane.setStyle("-fx-background-color: #2c3e50;"); // Fancy Navy Background
        infoPane.setPrefWidth(500); // Set equal width

        Label title = new Label("Welcome to Java Bank!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Label welcomeText = new Label("Please login to your account.");
        welcomeText.setFont(Font.font("Arial", 14));
        welcomeText.setTextFill(Color.LIGHTGRAY);

        Label emailLabel = new Label("Login section");
        emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        emailLabel.setTextFill(Color.LIGHTSKYBLUE);

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setMaxWidth(280);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);

        HBox rememberBox = new HBox(10);
        CheckBox rememberMe = new CheckBox("Remember Me");
        rememberMe.setTextFill(Color.WHITE);
        Label forgotPassword = new Label("Forgot Password?");
        forgotPassword.setTextFill(Color.LIGHTSKYBLUE);
        rememberBox.getChildren().addAll(rememberMe, forgotPassword);
        rememberBox.setAlignment(Pos.CENTER_LEFT);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(120);
        loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            // Validate the user
            int userID = validateUser(email, password);
            if (userID != -1) {
                // Pass the UserID to the transaction screen
                TransactionScreen transactionScreen = new TransactionScreen(userID); // Pass userID
                transactionScreen.start(primaryStage);
            } else {
                showErrorMessage();
            }
        });

        
        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(120);
        signUpButton.setStyle("-fx-background-color: white; -fx-border-color: #2980b9; -fx-text-fill: #2980b9;");
        SignUpEmail signUpEmailPage = new SignUpEmail();
        signUpButton.setOnAction(e -> signUpEmailPage.showSignUpEmail(primaryStage));

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(loginButton, signUpButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Label orLoginWith = new Label("Or login with");
        orLoginWith.setFont(Font.font("Arial", 14));
        orLoginWith.setTextFill(Color.WHITE);

        HBox socialBox = new HBox(10);
        socialBox.setAlignment(Pos.CENTER_LEFT);
        Label facebook = new Label("Facebook");
        facebook.setTextFill(Color.LIGHTSKYBLUE);
        Label linkedIn = new Label("LinkedIn");
        linkedIn.setTextFill(Color.LIGHTSKYBLUE);
        Label google = new Label("Google");
        google.setTextFill(Color.LIGHTSKYBLUE);
        socialBox.getChildren().addAll(facebook, linkedIn, google);

        infoPane.getChildren().addAll(title, welcomeText, emailLabel, emailField, passwordField, rememberBox, buttonBox, orLoginWith, socialBox);

        // Picture Pane (Right)
        StackPane picturePane = new StackPane();
        picturePane.setStyle("-fx-background-color: #F7F7F7;");
        picturePane.setPrefWidth(500); // Set equal width

        // HBox for equal frames
        HBox mainLayout = new HBox();
        mainLayout.getChildren().addAll(infoPane, picturePane);

        Scene scene = new Scene(mainLayout, 1000, 500); // Wider screen size
        primaryStage.setScene(scene);
        primaryStage.show();
        
        forgotPassword.setOnMouseClicked(e -> {
            PasswordResetScreen forgotPasswordScreen = new PasswordResetScreen();
            forgotPasswordScreen.start(primaryStage);
        });
    }

    // Method to validate user credentials
    private int validateUser(String input, String password) {
        Connection connection = DataConnection.getConnection();
        if (connection != null) {
            String query;
            boolean isUserID = input.matches("\\d+"); // Check if input is numeric (UserID)
            if (isUserID) {
                query = "SELECT UserID FROM users WHERE UserID = ? AND Password = ?";
            } else {
                query = "SELECT UserID FROM users WHERE Email = ? AND Password = ?";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, input);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("UserID"); // Return UserID if valid
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1; // Return -1 if invalid credentials
    }

    // Method to show error message
    private void showErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText("Invalid email or password. Please try again.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

