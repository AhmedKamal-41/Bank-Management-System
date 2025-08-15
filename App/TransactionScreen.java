package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TransactionScreen extends Application 
{	
    private int userID;

    public TransactionScreen(int userID) 
    {
        this.userID = userID; // Store userID
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Available Transactions");


        /*       
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9; -fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            SignUp signup = new SignUp();
            signup.showLoginPage(primaryStage);
        });*/

        // Create buttons for transactions
        Button withdrawButton = new Button("Withdrawal");
        withdrawButton.setPrefWidth(200);
        withdrawButton.setStyle("-fx-background-color: #3D9AF5; -fx-text-fill: white; -fx-font-size: 14px;");

                withdrawButton.setOnAction(e -> {
            WithdrawalScreen withdrawalScreen = new WithdrawalScreen(userID); // Pass userID
            withdrawalScreen.start(primaryStage);
        });
        
        Button depositButton = new Button("Deposit");
        depositButton.setPrefWidth(200);
        depositButton.setStyle("-fx-background-color: #3A3A3A; -fx-text-fill: white; -fx-font-size: 14px;");

        Button balanceInquiryButton = new Button("Balance Inquiry");
        balanceInquiryButton.setPrefWidth(200);
        balanceInquiryButton.setStyle("-fx-background-color: #3A3A3A; -fx-text-fill: white; -fx-font-size: 14px;");

        Button transfersPaymentsButton = new Button("Transfers & Payments");
        transfersPaymentsButton.setPrefWidth(200);
        transfersPaymentsButton.setStyle("-fx-background-color: #3A3A3A; -fx-text-fill: white; -fx-font-size: 14px;");

        // Create a VBox to arrange buttons
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #EFEFEF;");

        // Add buttons to layout
        layout.getChildren().addAll( withdrawButton, depositButton, balanceInquiryButton, transfersPaymentsButton);

        // Set up the scene
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        withdrawButton.setOnAction(e -> {
            // Open a withdrawal screen
            WithdrawalScreen withdrawalScreen = new WithdrawalScreen(userID);
            withdrawalScreen.start(primaryStage);
        });
        
        depositButton.setOnAction(e -> {
            // Open a deposit screen
            DepositScreen depositScreen = new DepositScreen(userID);
            depositScreen.start(primaryStage);
        });
        
        balanceInquiryButton.setOnAction(e -> {
            // Open a balance inquiry screen
            BalanceInquiryScreen balanceInquiryScreen = new BalanceInquiryScreen(userID);
            balanceInquiryScreen.start(primaryStage);
        });

        transfersPaymentsButton.setOnAction(e -> {
            // Open a transfer and payments screen
            PaymentScreen transfersPaymentsScreen = new PaymentScreen(userID);
            transfersPaymentsScreen.start(primaryStage);
        });
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
