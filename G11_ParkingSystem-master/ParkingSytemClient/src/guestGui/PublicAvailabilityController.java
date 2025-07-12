package guestGui;

import bpark_common.ClientRequest;
import client.ClientController;
import entities.ParkingSpace;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import subscriberGui.SubscriberDashboardController;
import utils.SceneNavigator;
import java.util.List;

/**
 * Controller for the PublicAvailability.fxml.
 * Displays available parking spots to guest users.
 */
public class PublicAvailabilityController {

    private static PublicAvailabilityController currentInstance;

    @FXML
    private TableView<ParkingSpace> tableAvailability;

    @FXML
    private TableColumn<ParkingSpace, Integer> colSpotNumber;

    @FXML
    private TableColumn<ParkingSpace, String> colStatus;

    @FXML
    private Label lblAvailable;

    @FXML
    private Button btnBack;

    /**
     * Gets the current instance of this controller.
     *
     * @return the current {@code PublicAvailabilityController} instance
     */
    public static PublicAvailabilityController getCurrentInstance() {
        return currentInstance;
    }

    /**
     * Called after the FXML is loaded to initialize the controller.
     * Registers this controller as the current instance.
     */
    @FXML
    public void initialize() {
        currentInstance = this;

        colSpotNumber.setCellValueFactory(new PropertyValueFactory<>("parkingSpaceId"));
        colStatus.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().isAvailable() ? "Available" : "Occupied"));

        requestAvailableSpots();

        Timeline refreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(10), e -> requestAvailableSpots())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Requests the list of available parking spots from the server using ClientRequest.
     */
    private void requestAvailableSpots() {
        ClientController client = ClientController.getClient();
        if (client != null) {
            client.sendObjectToServer(new ClientRequest("get_available_spots", new Object[0]));
        }
    }

    /**
     * Updates the table with the list of available parking spaces.
     * Called by ClientController.
     *
     * @param spots List of available parking spaces.
     */
    public void updateTable(List<ParkingSpace> spots) {
        javafx.application.Platform.runLater(() -> {
            tableAvailability.getItems().setAll(spots);
            lblAvailable.setText("yes" + spots.size() + " spots available");
        });
    }

    /**
     * Handles back button click.
     * Returns to the main menu if not logged in,
     * or to the subscriber dashboard if logged in as subscriber.
     *
     * @param event The action event from the button.
     */
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        String role = ClientController.getClient().getUserRole();
        String fxmlPath;
        String title;

        if (role == null || role.isEmpty()) {
            fxmlPath = "/client/MainMenu.fxml";
            title = "BPARK - Main Menu";
        } else if ("subscriber".equalsIgnoreCase(role)) {
            fxmlPath = "/subscriberGui/SubscriberDashboard.fxml";
            title = "BPARK - Subscriber Dashboard";
        } else {
            fxmlPath = "/client/MainMenu.fxml";
            title = "BPARK - Main Menu";
        }

        if ("subscriber".equalsIgnoreCase(role)) {
            SubscriberDashboardController controller = SceneNavigator.navigateToAndGetController(
                event, fxmlPath, title
            );
            if (controller != null) {
                controller.setClient(ClientController.getClient());
            }
        } else {
            SceneNavigator.navigateToAndGetController(event, fxmlPath, title);
        }
    }
}
