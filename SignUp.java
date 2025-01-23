package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SignUp {

    public void showLoginPage(Stage primaryStage, String email, String password) {
        primaryStage.setTitle("Account Information");

        // Create a GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.TOP_LEFT);

        // Go Back Button
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9; -fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            SignUpEmail signUpEmail = new SignUpEmail();
            signUpEmail.showSignUpEmail(primaryStage); // Go back to SignUpEmail page
        });
        gridPane.add(backButton, 0, 0, 2, 1); // Spanning across 2 columns

        // Account Type
        Label accountTypeLabel = new Label("Account Type");
        gridPane.add(accountTypeLabel, 0, 1);

        ComboBox<String> accountTypeComboBox = new ComboBox<>();
        accountTypeComboBox.getItems().addAll("Checking", "Savings", "Credit");
        accountTypeComboBox.setPromptText("Please Select");
        gridPane.add(accountTypeComboBox, 1, 1);

        // Personal Information Section
        Label personalInfoLabel = new Label("Personal Information");
        personalInfoLabel.setStyle("-fx-font-weight: bold;");
        gridPane.add(personalInfoLabel, 0, 2, 4, 1); // Spanning across 4 columns

        Label infoDescription = new Label("The information given in this section is considered as the information of the primary account owner.");
        gridPane.add(infoDescription, 0, 3, 4, 1); // Spanning across 4 columns

        // Name Fields
        Label nameLabel = new Label("Name");
        gridPane.add(nameLabel, 0, 4);

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        gridPane.add(firstNameField, 1, 4);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        gridPane.add(lastNameField, 2, 4);

        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name (Initial)");
        gridPane.add(middleNameField, 3, 4);

        TextField prefixField = new TextField();
        prefixField.setPromptText("Prefix (optional)");
        gridPane.add(prefixField, 4, 4);

        // Phone Number and Date of Birth
        Label phoneNumberLabel = new Label("Phone Number");
        gridPane.add(phoneNumberLabel, 0, 5);

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("(***) ***-****");
        gridPane.add(phoneNumberField, 1, 5);

        Label dobLabel = new Label("Date of Birth");
        gridPane.add(dobLabel, 2, 5);

        DatePicker dobPicker = new DatePicker();
        gridPane.add(dobPicker, 3, 5);

        // Residential Address
        Label addressLabel = new Label("Residential Address");
        gridPane.add(addressLabel, 0, 6);

        TextField streetAddressField = new TextField();
        streetAddressField.setPromptText("Street Address");
        gridPane.add(streetAddressField, 1, 6, 3, 1); // Spanning 3 columns

        TextField streetAddress2Field = new TextField();
        streetAddress2Field.setPromptText("Street Address Line 2");
        gridPane.add(streetAddress2Field, 1, 7, 3, 1); // Spanning 3 columns

        // City, State, Zip, Country
        TextField cityField = new TextField();
        cityField.setPromptText("City");
        gridPane.add(cityField, 1, 8);

        ComboBox<String> stateComboBox = new ComboBox<>();
        stateComboBox.getItems().addAll(
            "AK", "AL", "AR", "AS", "AZ", "CA", "CO", "CT", "DC", "DE", "FL", "GA", "GU", "HI", 
            "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MI", "MN", "MO", "MP",
            "MS", "MT", "NC", "ND", "NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA",
            "PR", "RI", "SC", "SD", "TN", "TT", "TX", "UT", "VA", "VI", "VT", "WA", "WI", "WV", "WY"
        );
        stateComboBox.setPromptText("State / Province");
        gridPane.add(stateComboBox, 2, 8);

        TextField zipField = new TextField();
        zipField.setPromptText("Postal / Zip Code");
        gridPane.add(zipField, 1, 9);

        Label SSNLabel = new Label("Social Security Number");
        gridPane.add(SSNLabel, 2, 9);

        TextField SSNField = new TextField();
        SSNField.setPromptText("***-**-****");
        gridPane.add(SSNField, 3, 9);

        // Country ComboBox
        ComboBox<String> countryComboBox = new ComboBox<>();
        countryComboBox.getItems().addAll("United States", "Other");
        countryComboBox.setPromptText("Please Select");
        gridPane.add(countryComboBox, 3, 8);

        // TextField for entering the country (initially hidden)
        TextField otherCountryField = new TextField();
        otherCountryField.setPromptText("Enter Country Name");
        otherCountryField.setVisible(false); // Initially hidden
        gridPane.add(otherCountryField, 2, 8); // Placed below the ComboBox

        // Event listener for ComboBox selection
        countryComboBox.setOnAction(e -> {
            String selectedCountry = countryComboBox.getValue();
            if ("Other".equals(selectedCountry)) {
                otherCountryField.setVisible(true);
                stateComboBox.setVisible(false);
                zipField.setVisible(false);
            } else {
                otherCountryField.setVisible(false);
                stateComboBox.setVisible(true);
                zipField.setVisible(true);
            }
        });

        // Citizenship
        Label citizenshipLabel = new Label("Citizenship");
        gridPane.add(citizenshipLabel, 0, 10);

        // Citizenship Section
        VBox citizenshipBox = new VBox(5);
        RadioButton usCitizen = new RadioButton("US Citizen");
        RadioButton otherCitizen = new RadioButton("Other");
        ToggleGroup citizenshipGroup = new ToggleGroup();
        usCitizen.setToggleGroup(citizenshipGroup);
        otherCitizen.setToggleGroup(citizenshipGroup);
        citizenshipBox.getChildren().addAll(usCitizen, otherCitizen);
        gridPane.add(citizenshipBox, 1, 10);

        // TextField for entering the country (initially hidden)
        TextField countryField = new TextField();
        countryField.setPromptText("Enter Country Name");
        countryField.setVisible(false); // Initially hidden
        gridPane.add(countryField, 1, 11); // Placed below the citizenship options

        // TextField for entering the green card number (initially hidden)
        TextField greenCardField = new TextField();
        greenCardField.setPromptText("Enter Green Card Number");
        greenCardField.setVisible(false); // Initially hidden
        gridPane.add(greenCardField, 1, 12); // Placed below the country field

        // Event listener for RadioButton selection
        citizenshipGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == otherCitizen) {
                countryField.setVisible(true);  // Show the country TextField when "Other" is selected
                greenCardField.setVisible(true);  // Show the green card TextField when "Other" is selected
            } else {
                countryField.setVisible(false);
                greenCardField.setVisible(false);
            }
        });

        // Marital Status
        Label maritalStatusLabel = new Label("Marital Status");
        gridPane.add(maritalStatusLabel, 2, 10);

        ComboBox<String> maritalStatusComboBox = new ComboBox<>();
        maritalStatusComboBox.getItems().addAll("Single", "Married", "Divorced", "Prefer Not to Say");
        maritalStatusComboBox.setPromptText("Please Select");
        gridPane.add(maritalStatusComboBox, 3, 10);

     // Create Save and Continue Button
        Button createSaveContinueButton = new Button("Create Save and Continue");
        createSaveContinueButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        createSaveContinueButton.setOnAction(e -> {
            // Get the generated userID from AccountIDScreen
            int userID = AccountIDScreen.getNextUserID();

            // Capture actual form field values
            String firstName = firstNameField.getText();    // Get the text from the firstName field
            String lastName = lastNameField.getText();      // Get the text from the lastName field
            String middleName = middleNameField.getText();  // Get the text from the middleName field
            String prefix = prefixField.getText();          // Get the text from the prefix field
            String phoneNumber = phoneNumberField.getText();// Get the text from the phoneNumber field
            LocalDate dob = dobPicker.getValue();           // Get the date from the DatePicker
            String streetAddress = streetAddressField.getText();  // Get the text from the streetAddress field
            String streetAddress2 = streetAddress2Field.getText(); // Get the text from the streetAddress2 field
            String city = cityField.getText();              // Get the text from the city field
            String state = stateComboBox.getValue();        // Get the selected value from the state ComboBox
            String zip = zipField.getText();                // Get the text from the zip field
            String ssn = SSNField.getText();                // Get the text from the SSN field

            // Check if "Other" was selected for the country
            String country;
            if ("Other".equals(countryComboBox.getValue())) {
                country = otherCountryField.getText();  // Use otherCountryField value
            } else {
                country = "United States";  // Default to United States
            }

            String maritalStatus = maritalStatusComboBox.getValue(); // Get the selected value from maritalStatus ComboBox
            String greenCardNumber = greenCardField.getText(); // Get green card number (if any)
            
            String citizenship;
            if (otherCitizen.isSelected()) {
                citizenship = otherCountryField.getText(); // Get the country from the otherCountryField
            } else {
                citizenship = "US Citizen"; // Default to "US Citizen" if selected
            }

            // Call saveUserData to save all data including the email and password
            saveUserData(userID, email, password, firstName, lastName, middleName, prefix, phoneNumber, dob,
                         streetAddress, streetAddress2, city, state, zip, ssn, country, maritalStatus, citizenship, greenCardNumber);
            
            // Show a confirmation message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Account Created");
            alert.setHeaderText(null);
            alert.setContentText("Your account has been successfully created");
            alert.showAndWait();

            // After showing the confirmation message, send the user back to the Main class (login screen)
            Main mainLoginScreen = new Main();
            mainLoginScreen.start(primaryStage); // Redirect to the Main login screen
        });


        HBox buttonBox = new HBox(createSaveContinueButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        gridPane.add(buttonBox, 4, 13, 2, 1); // Adjust column and row as per your layout

        // Set the scene
        Scene scene = new Scene(gridPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void saveUserData(int userID, String email, String password, String firstName, String lastName, String middleName, 
            String prefix, String phoneNumber, LocalDate dob, String streetAddress, String streetAddress2, 
            String city, String state, String zip, String ssn, String country, String maritalStatus, 
            String citizenship, String greenCardNumber) {

        // Call insertAccountInfo with all the attributes
        insertAccountInfo(userID, email, password, firstName, lastName, middleName, prefix, phoneNumber, dob, streetAddress, 
                          streetAddress2, city, state, zip, ssn, country, maritalStatus, citizenship, greenCardNumber);
    }

    public void insertAccountInfo(int userID, String email, String password, String firstName, String lastName, String middleName, 
            String prefix, String phoneNumber, LocalDate dob, String streetAddress, String streetAddress2, 
            String city, String state, String zip, String ssn, String country, String maritalStatus, 
            String citizenship, String greenCardNumber) {

        // SQL query to insert data into the 'users' table
        String sql = "INSERT INTO users (UserID, Email, Password, FirstName, LastName, MiddleInitial, Prefix, PhoneNumber, DateOfBirth, ResidentialAddress, " +
                     "ResidentialAddress2, City, State, ZipCode, SSN, Country, MaritalStatus, Citizenship, GreenCardNumber) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataConnection.getConnection(); 
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Mandatory Fields
            preparedStatement.setInt(1, userID);                      // user_id
            preparedStatement.setString(2, email);                    // Email (email) - Mandatory
            preparedStatement.setString(3, password);                 // Password (password) - Mandatory
            preparedStatement.setString(4, firstName);                // First Name (firstName) - Mandatory
            preparedStatement.setString(5, lastName);                 // Last Name (lastName) - Mandatory
            preparedStatement.setString(8, phoneNumber);              // Phone Number (phoneNumber) - Mandatory
            preparedStatement.setDate(9, java.sql.Date.valueOf(dob)); // Date of Birth (dob) - Mandatory
            preparedStatement.setString(10, streetAddress);           // Street Address (streetAddress) - Mandatory
            preparedStatement.setString(12, city);                    // City (city) - Mandatory
            preparedStatement.setString(15, ssn);                     // Social Security Number (SSN) (ssn) - Mandatory
            preparedStatement.setString(17, maritalStatus);           // Marital Status (maritalStatus) - Mandatory
            preparedStatement.setString(18, citizenship);             // Citizenship (citizenship) - Mandatory

            // Optional Fields
            if (middleName != null && !middleName.isEmpty()) {
                preparedStatement.setString(6, middleName);           // Middle Name (middleName) - Optional
            } else {
                preparedStatement.setNull(6, java.sql.Types.VARCHAR);
            }

            if (prefix != null && !prefix.isEmpty()) {
                preparedStatement.setString(7, prefix);               // Prefix (prefix) - Optional
            } else {
                preparedStatement.setNull(7, java.sql.Types.VARCHAR);
            }

            if (streetAddress2 != null && !streetAddress2.isEmpty()) {
                preparedStatement.setString(11, streetAddress2);       // Street Address Line 2 (streetAddress2) - Optional
            } else {
                preparedStatement.setNull(11, java.sql.Types.VARCHAR);
            }

            if (state != null && !state.isEmpty()) {
                preparedStatement.setString(13, state);               // State (state) - Optional (mandatory only for users living in the USA)
            } else {
                preparedStatement.setNull(13, java.sql.Types.VARCHAR);
            }

            if (zip != null && !zip.isEmpty()) {
                preparedStatement.setString(14, zip);                 // Postal/Zip Code (zip) - Optional (mandatory only for users living in the USA)
            } else {
                preparedStatement.setNull(14, java.sql.Types.VARCHAR);
            }

            if (country != null && !country.isEmpty()) {
                preparedStatement.setString(16, country);             // Country (country) - Optional (mandatory for users living outside the USA)
            } else {
                preparedStatement.setNull(16, java.sql.Types.VARCHAR);
            }

            if (greenCardNumber != null && !greenCardNumber.isEmpty()) {
                preparedStatement.setString(19, greenCardNumber);      // Green Card Number (greenCardNumber) - Optional (required only for non-US citizens who have a green card)
            } else {
                preparedStatement.setNull(19, java.sql.Types.VARCHAR);
            }

            // Execute the query
            preparedStatement.executeUpdate();
            System.out.println("User information saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

