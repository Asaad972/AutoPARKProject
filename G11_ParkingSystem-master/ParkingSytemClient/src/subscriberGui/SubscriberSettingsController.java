package subscriberGui;

import client.ClientController;
import entities.Subscriber;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import utils.SceneNavigator;
import javafx.event.ActionEvent;

/**
 * Controller for SubscriberSettings.fxml.
 * Displays the currently logged-in subscriber's details using labels.
 */
public class SubscriberSettingsController {

    @FXML
    private Label lblFullName;

    @FXML
    private Label lblSubscriberId;

    @FXML
    private Label lblSubscriberCode;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblCurrentEmail;

    @FXML
    private Label lblCurrentPhone;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnEdit;

    /**
     * Called automatically after the FXML is loaded.
     * Safely updates the UI once the subscriber is available.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Subscriber subscriber = ClientController.getClient().getCurrentSubscriber();
            if (subscriber == null) {
                System.out.println("No subscriber in client");
                return;
            }
            System.out.println("Populating fields for: " + subscriber.getFullName());

            lblFullName.setText(subscriber.getFullName());
            lblSubscriberId.setText(String.valueOf(subscriber.getId()));
            lblSubscriberCode.setText(subscriber.getSubscriberCode());
            lblUsername.setText(subscriber.getUsername());
            lblCurrentEmail.setText(subscriber.getEmail());
            lblCurrentPhone.setText(subscriber.getPhone());
        });
    }

    /**
     * Handles the Back button click.
     * Loads the SubscriberDashboard.fxml scene and refreshes data.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleBackButton(ActionEvent event) {
    	SceneNavigator.navigateTo(event, "/subscriberGui/SubscriberDashboard.fxml", "BPARK - Subscriber Dashboard");
    }

    /**
     * Handles the Edit button click.
     * Loads the EditSubscriberDetails.fxml screen.
     */
    @FXML
    private void handleEditButton(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/subscriberGui/EditSubscriberDetails.fxml", "BPARK - Edit Subscriber Details");
    }

}
