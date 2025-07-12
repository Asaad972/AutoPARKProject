package subscriberGui;

import bpark_common.ClientRequest;
import client.ClientController;
import entities.Subscriber;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.SceneNavigator;

/**
 * Controller for EditSubscriberDetails.fxml.
 * Loads the current subscriber's information into editable fields and handles user actions.
 */
public class EditSubscriberDetailsController {

    @FXML
    private TextField txtNewEmail;

    @FXML
    private TextField txtConfirmEmail;

    @FXML
    private TextField txtNewPhone;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnBack;

    @FXML
    private Label lblStatus;

    /** Holds the current subscriber loaded from ClientController */
    private Subscriber currentSubscriber;
    
    

    /**
     * Initializes the controller by loading subscriber data into fields.
     * Registers this controller with the ClientController for callback support.
     */
    @FXML
    public void initialize() {
        currentSubscriber = ClientController.getClient().getCurrentSubscriber();
        if (currentSubscriber != null) {
            System.out.println("Populating fields for: " + currentSubscriber.getFullName());
            txtNewEmail.setText(currentSubscriber.getEmail());
            txtConfirmEmail.setText(currentSubscriber.getEmail());
            txtNewPhone.setText(currentSubscriber.getPhone().replaceAll("-", ""));
        } else {
            System.out.println("currentSubscriber is null - cannot populate fields");
        }

        // Register this controller instance with ClientController so it can callback on update
        ClientController.getClient().setEditSubscriberDetailsController(this);
    }

    /**
     * Handles the Back button click event.
     * Returns to the SubscriberSettings screen.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/subscriberGui/SubscriberSettings.fxml", "Subscriber Settings");
    }

    /**
     * Handles the Cancel button click event.
     * Discards changes and returns to the settings screen.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/subscriberGui/SubscriberSettings.fxml", "Subscriber Settings");
    }

    /**
     * Handles the Save button click event.
     * Validates input and sends updated subscriber info to the server as a ClientRequest.
     * Disables Save button until response is received.
     */
    @FXML
    private void handleSave() {
    	String email = txtNewEmail.getText().trim();
    	String confirmEmail = txtConfirmEmail.getText().trim();
    	String phone = txtNewPhone.getText().trim();

    	if (email.isEmpty() || confirmEmail.isEmpty() || phone.isEmpty()) {
    	    lblStatus.setText("All fields are required.");
    	    return;
    	}

    	if (!email.matches("^(?![.])[a-zA-Z0-9._%+-]+(?<![.])@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
    	    lblStatus.setText("Invalid email format.");
    	    return;
    	}


    	if (!email.equals(confirmEmail)) {
    	    lblStatus.setText("Email confirmation does not match.");
    	    return;
    	}

    	if (!phone.matches("\\d{10}")) {
    	    lblStatus.setText("Phone number must be exactly 10 digits.");
    	    return;
    	}
    	
    	String formattedPhone = formatPhoneNumber(phone);


        btnSave.setDisable(true);
        lblStatus.setText("Sending update...");

        Subscriber updatedSubscriber = new Subscriber(
                currentSubscriber.getId(),
                currentSubscriber.getFullName(),
                currentSubscriber.getUsername(),
                email,
                formattedPhone,
                currentSubscriber.getSubscriberCode()
        );

        // Send update as a ClientRequest to the server
        ClientController.getClient().sendObjectToServer(
                new ClientRequest("update_subscriber", new Object[]{updatedSubscriber})
        );
    }

    /**
     * Called by ClientController when an update response is received from the server.
     *
     * @param success Whether the update was successful.
     * @param message Response message from the server.
     */
    public void onUpdateResponse(boolean success, String message) {
        Platform.runLater(() -> {
            lblStatus.setText(message);
            btnSave.setDisable(false);

            if (success) {
            	
            	String formattedPhone = formatPhoneNumber(txtNewPhone.getText().trim());

            	//Immediately updates the local in-memory Subscriber object
            	currentSubscriber = new Subscriber(
            	        currentSubscriber.getId(),
            	        currentSubscriber.getFullName(),
            	        currentSubscriber.getUsername(),
            	        txtNewEmail.getText().trim(),
            	        formattedPhone, 
            	        currentSubscriber.getSubscriberCode()
            	);

                ClientController.getClient().setCurrentSubscriber(currentSubscriber);

                // Show success popup
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Update Successful");
                alert.setHeaderText(null);
                alert.setContentText("Successfully updated subscriber details.");
                alert.showAndWait();
        
                // Switch to settings screen after popup closes
                SceneNavigator.navigateTo(new javafx.event.ActionEvent(), "/subscriberGui/SubscriberSettings.fxml", "Subscriber Settings");
            }
        });
    }
    
    /**
     * Formats a 10-digit phone number to XXX-XXX-XXXX format.
     *
     * @param rawPhone The phone number as a 10-digit string
     * @return Formatted phone number with dashes
     */
    private String formatPhoneNumber(String rawPhone) {
        return rawPhone.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
    }

}
