package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the initial connection screen of BPARK.
 * Allows user to specify server IP and port before launching main menu.
 */
public class ConnectViewController {

    @FXML
    private TextField txtServerIp;

    @FXML
    private TextField txtServerPort;

    @FXML
    private Button connectButton;

    @FXML
    private Label statusLabel;

    /**
     * Initializes default server values and label on startup.
     */
    @FXML
    public void initialize() {
        txtServerIp.setText("localhost");
        txtServerPort.setText("5555");
        statusLabel.setText("Enter server details to connect.");
    }

    /**
     * Handles the connection attempt when "Connect" button is clicked.
     * Connects to server, creates client controller, and loads the Main Menu UI.
     *
     * @param event ActionEvent triggered by clicking the connect button.
     */
    @FXML
    void handleConnect(ActionEvent event) {
        String ip = txtServerIp.getText().trim();
        String portText = txtServerPort.getText().trim();

        if (ip.isEmpty() || portText.isEmpty()) {
            statusLabel.setText("Please enter server IP and port.");
            return;
        }

        try {
            int port = Integer.parseInt(portText);

            // Load MainMenu.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/MainMenu.fxml"));
            Parent root = loader.load();

            // Get MainMenuController and pass references
            MainMenuController mainMenuController = loader.getController();

            // Create and assign client
            ClientController client = new ClientController(ip, port, mainMenuController);
            ClientController.setClient(client);
            mainMenuController.setClient(client);

            // Get the current stage and assign it
            Stage stage = (Stage) connectButton.getScene().getWindow();
            mainMenuController.setStage(stage);                   // Pass stage to controller
            ClientController.setPrimaryStage(stage);             // Save globally for reuse

            // Show main menu
            stage.setScene(new Scene(root));
            stage.setTitle("BPARK - Main Menu");
            stage.show();

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid port number.");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Could not connect to server.");
        }
    }
}
