package subscriberGui;

import bpark_common.ClientRequest;
import client.ClientController;
import entities.Subscriber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import utils.SceneNavigator;

/**
 * Controller for the Extend Parking screen.
 * Handles logic for extending parking time by 4 hours.
 */
public class ExtendParkingController {

    /**
     * Handles the "Back" button action to return to the Subscriber Dashboard.
     * Uses the SceneNavigator to ensure consistent navigation.
     *
     * @param event The ActionEvent triggered by the user's interaction.
     */
    @FXML
    private void goBack(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/subscriberGui/SubscriberDashboard.fxml", "BPARK - Subscriber Dashboard");
    }

    /**
     * Handles the "Finish" button click to extend parking time.
     * Sends a ClientRequest with the "extend_parking" command to the server.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    private void handleFinishExtendTime(ActionEvent event) {
        Subscriber currentSubscriber = ClientController.getClient().getCurrentSubscriber();

        if (currentSubscriber == null) {
            System.out.println("No subscriber is currently logged in.");
            return;
        }

        // Send as a ClientRequest for protocol consistency
        ClientRequest request = new ClientRequest("extend_parking", new Object[]{currentSubscriber.getSubscriberCode()});
        ClientController.getClient().sendObjectToServer(request);
    }

    /**
     * Initializes the controller.
     * Registers this controller with the ClientController to receive update responses.
     */
    @FXML
    public void initialize() {
        ClientController.getClient().setExtendParkingController(this);
    }

    /**
     * Called by ClientController when an extend parking response is received.
     *
     * @param success true if the extension was successful, false otherwise.
     * @param message the server's response message
     */
    public void onUpdateResponse(boolean success, String message) {
        System.out.println("onUpdateResponse called: " + message);

        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(success ? "Extension Successful!" : "Extension Failed");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Closes the confirmation window when the Finish button is pressed.
     *
     * @param event the action event from the button
     */
    @FXML
    private void handleClose(ActionEvent event) {
        // Close the window containing this button
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
