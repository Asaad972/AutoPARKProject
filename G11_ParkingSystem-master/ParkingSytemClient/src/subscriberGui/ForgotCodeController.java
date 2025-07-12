package subscriberGui;

import bpark_common.ClientRequest;
import bpark_common.ServerResponse;
import client.ClientController;
import entities.Subscriber;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import utils.SceneNavigator;

/**
 * Controller for the ForgotCode screen.
 * Allows the user to request their parking code via email or SMS.
 */
public class ForgotCodeController {

    @FXML
    private RadioButton radioEmail;

    @FXML
    private RadioButton radioPhone;

    @FXML
    private ToggleGroup toggleMethod;

    @FXML
    private Button btnSendCode;

    @FXML
    private Button btnBack;

    @FXML
    private Label lblResult;

    private String actualEmail;
    private String actualPhone;

    /**
     * Initializes the controller, sets actions and fetches latest contact info.
     */
    @FXML
    public void initialize() {
        btnSendCode.setOnAction(event -> handleSendCode());
        btnBack.setOnAction(event -> handleBack());
    }

    /**
     * Handles the Send Code action based on selected method.
     */
    private void handleSendCode() {
        if (radioPhone.isSelected()) {
            if (actualPhone == null) {
                setResult("Phone number not available.", "red");
                return;
            }

            showAlertAndBack("SMS Sent", "A code was sent to your phone.", Alert.AlertType.INFORMATION);
        } else if (radioEmail.isSelected()) {
            if (actualEmail == null) {
                setResult("Email address not available.", "red");
                return;
            }

            Subscriber sub = ClientController.getClient().getCurrentSubscriber();
            if (sub == null) {
                setResult("Subscriber not found.", "red");
                return;
            }

            ClientRequest req = new ClientRequest("send_code_email", new Object[]{sub.getSubscriberCode()});
            ClientController.getClient().sendObjectToServer(req);

            setResult("Sending code to your email...", "blue");
        } else {
            setResult("Please select a method.", "orange");
        }
    }

    /**
     * Displays result text with given color.
     */
    private void setResult(String message, String color) {
        lblResult.setText(message);
        lblResult.setStyle("-fx-text-fill: " + color + ";");
    }

    /**
     * Navigates to CarPickup screen after alert and injects the client.
     */
    private void showAlertAndBack(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.setOnHidden(event -> {
            CarPickupController controller = SceneNavigator.navigateToAndGetController(
                null,
                "/subscriberGui/CarPickup.fxml",
                "BPARK - Retrieve Your Vehicle"
            );
            if (controller != null) {
                controller.setClient(ClientController.getClient());
            }
        });

        alert.show();
    }

    /**
     * Navigates back to the CarPickup screen.
     */
    private void handleBack() {
        CarPickupController controller = SceneNavigator.navigateToAndGetController(
            null,
            "/subscriberGui/CarPickup.fxml",
            "BPARK - Retrieve Your Vehicle"
        );
        if (controller != null) {
            controller.setClient(ClientController.getClient());
        }
    }

    /**
     * Handles the server response after attempting to send an email.
     *
     * @param response the server response indicating success or failure
     */
    public void handleEmailResponse(ServerResponse response) {
        Platform.runLater(() -> {
            if (response.isSuccess()) {
                showAlertAndBack("Email Sent", "A code was sent to your email.", Alert.AlertType.INFORMATION);
            } else {
                setResult(response.getMessage(), "red");
            }
        });
    }

    /**
     * Updates and masks the email address for display.
     *
     * @param email the full email address to be masked
     */
    public void updateEmailDisplay(String email) {
        if (email == null || !email.contains("@")) return;

        this.actualEmail = email;

        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        String maskedName = name.length() <= 2 ? name : name.substring(0, 2) + "****";
        String maskedEmail = maskedName + "@" + domain;

        Platform.runLater(() -> {
            if (radioEmail != null) {
                radioEmail.setText("Send to Email: " + maskedEmail);
            }
        });
    }

    /**
     * Updates and masks the phone number for display.
     *
     * @param phone the full phone number to be masked
     */
    public void updatePhoneDisplay(String phone) {
        if (phone == null || phone.length() < 7) return;

        this.actualPhone = phone;

        String masked = phone.substring(0, 2) + "*-***-" + phone.substring(phone.length() - 4);

        Platform.runLater(() -> {
            if (radioPhone != null) {
                radioPhone.setText("Send to Phone: " + masked);
            }
        });
    }

    /**
     * Returns the actual email address (unmasked) of the subscriber.
     *
     * @return the full email address
     */
    public String getActualEmail() {
        return actualEmail;
    }

    /**
     * Returns the actual phone number (unmasked) of the subscriber.
     *
     * @return the full phone number
     */
    public String getActualPhone() {
        return actualPhone;
    }
}
