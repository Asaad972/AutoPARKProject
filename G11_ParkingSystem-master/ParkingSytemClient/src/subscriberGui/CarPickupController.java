package subscriberGui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.SceneNavigator;
import bpark_common.ClientRequest;
import client.ClientController;
import entities.Subscriber;

/**
 * Controller for the CarPickup.fxml screen.
 * Handles the retrieval of a parked vehicle using a parking code.
 * Sends a pickup request to the server and displays the result to the user.
 */
public class CarPickupController {

    @FXML
    private TextField txtParkingCode;

    @FXML
    private Button btnRetrieve;

    @FXML
    private Label lblConfirmation;

    @FXML
    private Button btnLostCode;

    /** The client controller used for server communication. */
    private ClientController client;

    /**
     * Injects the active client controller.
     *
     * @param client The ClientController instance to be used for communication.
     */
    public void setClient(ClientController client) {
        this.client = client;
    }

    /**
     * Initializes the controller.
     * Sets up initial UI state and event handlers.
     */
    @FXML
    public void initialize() {
        lblConfirmation.setVisible(false);
        btnRetrieve.setOnAction(this::onRetrieveClicked);
    }

    /**
     * Handles the action of retrieving the car.
     * Validates the input and sends a car pickup request to the server.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    
    @FXML
    private void onRetrieveClicked(ActionEvent event) {
    	String spotId = txtParkingCode.getText(); // what the user types (parking spot)
    	String subCode = client.getCurrentSubscriber().getSubscriberCode(); // from logged-in user
    	if (spotId == null || spotId.isEmpty()) {
    	    lblConfirmation.setText("Parking code cannot be empty.");
    	    lblConfirmation.setStyle("-fx-text-fill: red;");
    	    lblConfirmation.setVisible(true);
    	    return;
    	} else if (!spotId.matches("\\d+")) {
    	    lblConfirmation.setText("Parking code must contain numbers only.");
    	    lblConfirmation.setStyle("-fx-text-fill: red;");
    	    lblConfirmation.setVisible(true);
    	    return;
    	}
        Object[] params = { subCode, spotId };
        ClientRequest request = new ClientRequest("car_pickup", params);
        if (client != null) {
            client.sendObjectToServer(request);
        }
    }

    /**
     * Handles the response from the server after attempting to pick up the car.
     * Shows a success or error popup message to the user.
     * If successful, returns the user to the dashboard after the message is closed.
     *
     * @param success True if the pickup was successful, false otherwise.
     * @param message The message to display to the user.
     */
    public void handlePickupResponse(boolean success, String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                success ? javafx.scene.control.Alert.AlertType.INFORMATION : javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle(success ? "Pickup Successful" : "Pickup Failed");
            alert.setHeaderText(null);
            alert.setContentText((success ? "yes" : "no") + message);
            alert.showAndWait();

            if (success) {
                // Go back to dashboard
                utils.SceneNavigator.navigateTo(
                    null,
                    "/subscriberGui/SubscriberDashboard.fxml",
                    "BPARK - Subscriber Dashboard"
                );
            }
        });
    }

    /**
     *
     * @param event The action event triggered by clicking the hyperlink.
     */
    @FXML
    private void onLostCodeClicked(ActionEvent event) {
        subscriberGui.ForgotCodeController controller = SceneNavigator.navigateToAndGetController(
            event,
            "/subscriberGui/ForgotCode.fxml",
            "BPARK - Forgot Code"
        );

        if (controller != null) {
            ClientController.getClient().setForgotCodeController(controller);

            Subscriber current = ClientController.getClient().getCurrentSubscriber();
            if (current != null) {
                ClientRequest req = new ClientRequest("get_subscriber_contact", new Object[]{current.getSubscriberCode()});
                ClientController.getClient().sendObjectToServer(req);
            }
        } else {
            System.err.println("Failed to get ForgotCodeController");
        }
    }






    /**
     * Handles the "Back" button action to return to the Subscriber Dashboard.
     *
     * @param event The ActionEvent triggered by the user's interaction.
     */
    @FXML
    private void goBack(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/subscriberGui/SubscriberDashboard.fxml", "BPARK - Subscriber Dashboard");
    }
}
