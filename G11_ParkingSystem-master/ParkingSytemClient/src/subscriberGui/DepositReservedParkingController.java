package subscriberGui;

import bpark_common.ClientRequest;
import client.ClientController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.SceneNavigator;

/**
 * Controller for handling the deposit of a reserved car using a confirmation code.
 */
public class DepositReservedParkingController {

    private static DepositReservedParkingController instance;
    private ClientController client;

    @FXML
    private TextField txtConfirmationCode;

    @FXML
    private Label lblMessage;

    @FXML
    private TextField txtCancelCode;

    /**
     * Constructor that registers this instance statically for later reference.
     */
    public DepositReservedParkingController() {
        instance = this;
    }

    /**
     * Returns the currently loaded controller instance.
     *
     * @return the active DepositReservedParkingController
     */
    public static DepositReservedParkingController getInstance() {
        return instance;
    }

    /**
     * Sets the active client reference for use in this controller.
     *
     * @param client the ClientController instance
     */
    public void setClient(ClientController client) {
        this.client = client;
    }

    /**
     * Called when the user clicks the "Deposit" button.
     * Sends the confirmation code to the server for validation and action.
     *
     * @param event the action event from the deposit button
     */
    @FXML
    private void handleDepositReserved(ActionEvent event) {
        String confirmationCode = txtConfirmationCode.getText().trim();

        if (confirmationCode.isEmpty()) {
            lblMessage.setText("Please enter the confirmation code.");
            return;
        }
        if (!confirmationCode.matches("\\d+"))
        {
        	lblMessage.setText("Please enter the confirmation code Only Numbers.");
            return;
        }
        if (client == null || client.getCurrentSubscriber() == null) {
            lblMessage.setText("Client or subscriber not available.");
            return;
        }

        // Send request to server
        ClientRequest request = new ClientRequest("CheckAndDepositReservedCar", new Object[]{confirmationCode});
        client.sendObjectToServer(request);
    }

    /**
     * Displays the result message received from the server.
     *
     * @param message response message from server
     */
    public void showServerResponse(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reservation Status");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();

            // Navigate back to subscriber dashboard
            SceneNavigator.navigateTo(null,
                "/subscriberGui/SubscriberDashboard.fxml",
                "BPARK - Subscriber Dashboard");
        });
    }

    /**
     * Called when the user clicks "Back" to return to the dashboard.
     *
     * @param event the action event from the back button
     */
    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.navigateTo(null,
            "/subscriberGui/SubscriberDashboard.fxml",
            "BPARK - Subscriber Dashboard");
    }
    
    /**
     * Handles the cancel reservation action when the user clicks "Cancel".
     * Validates the confirmation code and sends a cancellation request to the server.
     *
     * @param event the action event triggered by the cancel button
     */
    @FXML
    private void handleCancelReservation(ActionEvent event) {
        String code = txtCancelCode.getText().trim();

        if (code.isEmpty()) {
            lblMessage.setText("Please enter the confirmation code.");
            return;
        }
        if (!code.matches("\\d+"))
        {
        	lblMessage.setText("Please enter the confirmation code Only Numbers.");
            return;
        }
        if (client == null || client.getCurrentSubscriber() == null) {
            lblMessage.setText("Client or subscriber not available.");
            return;
        }

        client.sendObjectToServer(
            new ClientRequest("CancelReservationByCode", new Object[]{code})
        );
    }

}
