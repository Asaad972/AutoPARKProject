package subscriberGui;

import bpark_common.ClientRequest;
import client.ClientController;
import entities.ParkingHistory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import utils.SceneNavigator;

import java.time.LocalDateTime;

/**
 * Controller for the Car Deposit confirmation screen.
 * Displays the assigned parking spot and handles confirmation.
 *
 * This screen is shown when a subscriber chooses to deposit their car.
 */
public class CarDepositController {

    private ClientController client;

    @FXML
    private Label labelSpotNumber;

    private int selectedSpotId = -1;

    /** Flag to track if the deposit was rejected by the server. */
    private boolean depositRejected = false;

    /**
     * Called manually after the client is injected.
     * Sends a ClientRequest to the server for a random available parking spot.
     */
    public void onLoaded() {
        if (client != null) {
            client.sendObjectToServer(
                new ClientRequest("get_random_spot", new Object[0])
            );
        } else {
            System.out.println("Client not set — cannot request parking spot.");
        }
    }

    /**
     * Injects the active ClientController instance.
     *
     * @param client the active ClientController
     */
    public void setClient(ClientController client) {
        this.client = client;
    }

    /**
     * Sets the rejection flag for the current deposit attempt.
     *
     * @param rejected true if the deposit was rejected by the server
     */
    public void setDepositRejected(boolean rejected) {
        this.depositRejected = rejected;
    }

    /**
     * Sets the assigned parking spot number to display on screen and stores it for saving.
     *
     * @param spot the assigned parking spot (e.g., "6")
     */
    public void setSpot(String spot) {
        labelSpotNumber.setText(spot);
        try {
            selectedSpotId = Integer.parseInt(spot); // store the ID
        } catch (NumberFormatException e) {
            System.err.println("Invalid spot format: " + spot);
            selectedSpotId = -1;
        }
    }

    /**
     * Called when the user clicks "Finish" to confirm the deposit.
     * Sends a deposit ClientRequest to the server. Waits briefly for any
     * rejection response before showing a success alert and returning to dashboard.
     *
     * The entry and exit times are rounded to the nearest quarter hour.
     *
     * @param event The button click event
     */
    @FXML
    private void confirmDeposit(ActionEvent event) {
        if (client != null && selectedSpotId != -1) {
            depositRejected = false; // reset before sending

            String subscriberCode = client.getCurrentSubscriber().getSubscriberCode();

            // Get current time and round to nearest quarter hour
            LocalDateTime now = LocalDateTime.now();

         // Build the ParkingHistory for deposit (does not need history_id)
            ParkingHistory deposit = new ParkingHistory(
                    0,
                    subscriberCode,
                    selectedSpotId,
                    now,
                    now.plusHours(4),
                    false,
                    0,          // extendedHours is 0 for initial deposit
                    false,
                    false
                );


            // Send deposit as a ClientRequest
            client.sendObjectToServer(
                new ClientRequest("car_deposit", new Object[]{deposit})
            );

            // Wait briefly to allow server to respond with error if needed
            new Thread(() -> {
                try {
                    Thread.sleep(300); // brief delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    if (!depositRejected) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Deposit Confirmed");
                        alert.setHeaderText(null);
                        alert.setContentText("Your vehicle was successfully deposited.");
                        alert.showAndWait();

                        SceneNavigator.navigateTo(null,
                            "/subscriberGui/SubscriberDashboard.fxml",
                            "BPARK - Subscriber Dashboard");
                    }
                });
            }).start();
        }
    }
}
