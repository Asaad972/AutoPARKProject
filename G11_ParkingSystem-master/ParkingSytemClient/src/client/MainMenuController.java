package client;

import adminGui.AdminMainMenuController;
import bpark_common.ClientRequest;
import common.ChatIF;
import entities.LoginRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import subscriberGui.SubscriberDashboardController;

import java.io.IOException;
import java.net.URL;

/**
 * Controller for the main client menu.
 * Handles login actions and access to public parking availability.
 */
public class MainMenuController implements ChatIF {

    private ClientController client;
    private Stage stage; // The window used to change scenes

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnCheckAvailability;

    @FXML
    private Label statusLabel;
    
    /** Toggle button for choosing access from home */
    @FXML
    private ToggleButton toggleHome;

    /** Toggle button for choosing access from shop */
    @FXML
    private ToggleButton toggleShop;
    
    /** TextField for entering an alternative ID used in scan tag functionality. */
    @FXML
    private TextField txtAltId;

    private boolean isScanTagLogin = false;



    /**
     * Allows external controllers (like logout) to pass the current stage.
     *
     * @param stage The JavaFX window used for scene changes.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Updates the status label with a given message from server responses or client events.
     *
     * @param message the message to be shown on the status label
     */
    @Override
    public void display(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    /**
     * Injects the client controller instance.
     *
     * @param client The client controller.
     */
    public void setClient(ClientController client) {
        this.client = client;
    }

    /**
     * Called automatically after FXML is loaded. Sets up button actions.
     */
    @FXML
    private void initialize() {
        btnLogin.setOnAction(e -> handleLogin());
        btnCheckAvailability.setOnAction(this::checkAvailability);
    }

    /**
     * Sends a login request to the server with the entered username, password, and selected access mode.
     * Validates input and prevents login if access mode is not selected or fields are empty.
     */
    private void handleLogin() {
        String mode = getAccessMode();
        if ("none".equals(mode)) {
            showAlert("Please select an access mode before logging in.");
            return;
        }

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please enter both username and password.");
            return;
        }
        else if (!username.matches("[a-zA-Z0-9]+"))
        {
            showAlert("Username must contain only letters and numbers");
            return;
        }
        // Create a login request with access mode included
        LoginRequest loginRequest = new LoginRequest(username, password, mode);

        try {
            ClientController.getClient().sendToServer(loginRequest);
        } catch (IOException e) {
            showAlert("Failed to send login request.");
            e.printStackTrace();
        }
    }



    /**
     * Displays an error alert with the given message.
     *
     * @param msg The error message to display.
     */
    public void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Called by the ClientController after receiving a login response.
     *
     * @param success  Indicates if the login was successful.
     * @param message  The login message from the server.
     */
    public void handleLoginResponse(boolean success, String message) {
        Platform.runLater(() -> {
            if (success) {
                String prefix = "Login successful: ";
                if (message.startsWith(prefix)) {
                    String role = message.substring(prefix.length()).trim();

                    //Only show this alert if the login was from scan tag
                    if (isScanTagLogin && ("admin".equalsIgnoreCase(role) || "supervisor".equalsIgnoreCase(role))) {
                        showAlert("Scan tag login is not supported for admin or supervisor roles.\nPlease log in manually.");
                        isScanTagLogin = false; // Reset the flag
                        return;
                    }

                    redirectBasedOnRole(role);
                }
            } else {
                showAlert("Fail" + message);
            }

            // Always reset the flag after handling login
            isScanTagLogin = false;
        });
    }


    /**
     * Gets the selected access mode.
     * @return "home" if Home is selected, "shop" if Shop is selected, "none" if neither is selected.
     */
    private String getAccessMode() {
        if (toggleHome.isSelected()) {
            return "home";
        } else if (toggleShop.isSelected()) {
            return "shop";
        } else {
            return "none";
        }
    }

    /**
     * Redirects the user to their respective dashboard screen based on their role.
     * Reuses the current login stage instead of opening a new one.
     *
     * @param role The role returned from the server (e.g., "subscriber", "admin").
     */
    public void redirectBasedOnRole(String role) {
        try {
            FXMLLoader loader;
            Parent root;

            switch (role) {
                case "admin":
                	loader = new FXMLLoader(getClass().getResource("/adminGui/AdminMainMenu.fxml"));
                	break;
                case "supervisor":
                    loader = new FXMLLoader(getClass().getResource("/adminGui/AdminMainMenu.fxml"));
                    break;
                case "subscriber":
                    loader = new FXMLLoader(getClass().getResource("/subscriberGui/SubscriberDashboard.fxml"));
                    break;
                default:
                    showAlert("Unknown role: " + role);
                    return;
            }

            root = loader.load();
            Object controller = loader.getController();

            // Pass client to the correct controller
            if (controller instanceof AdminMainMenuController adminController) {
                adminController.setClient(client);
            } else if (controller instanceof SubscriberDashboardController subscriberController) {
                subscriberController.setClient(client);
            }

            // Reuse the current login stage instead of opening a new window
            Stage currentStage = this.stage;
            if (currentStage == null && txtUsername != null && txtUsername.getScene() != null) {
                currentStage = (Stage) txtUsername.getScene().getWindow();
            }
            if (currentStage == null) {
                currentStage = ClientController.getPrimaryStage(); // fallback
            }

            if (currentStage != null) {
                //Set primary stage for future event-less navigation
                ClientController.setPrimaryStage(currentStage);

                currentStage.setScene(new Scene(root));
                currentStage.setTitle("BPARK - " + role);
                currentStage.show();
            } else {
                showAlert("Could not find a valid window to load the scene.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load " + role + " dashboard: " + e.getMessage());
        }
    }


    /**
     * Navigates to the Public Availability screen (for guests).
     *
     * @param event The action event from button click.
     */
    @FXML
    private void checkAvailability(ActionEvent event) {
        try {
            URL fxmlLocation = getClass().getResource("/guestGui/PublicAvailability.fxml");

            if (fxmlLocation == null) {
                throw new IOException("FXML file not found: /guestGui/PublicAvailability.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Parking Availability");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load Public Availability screen.");
        }
    }
    
    /**
     * Handles login using a scanned tag (ID input). Sends a request to the server to retrieve login credentials.
     * If successful, triggers auto-login with those credentials.
     *
     * @param event the action event from clicking the Scan Tag button
     */
    @FXML
    private void handleScanTag(ActionEvent event) {
        String id = txtAltId.getText();

        if (id == null || id.trim().isEmpty() || !id.matches("\\d+")) {
            statusLabel.setText("Please enter a valid ID to scan. (only numbers)");
            return;
        }

        statusLabel.setText("Scanning tag... please wait!");

        //Mark that the next login is from a scan tag
        isScanTagLogin = true;

        
        // Create a new thread to simulate scanning
        new Thread(() -> {
            try {
                Thread.sleep(2000); // simulate 2 seconds scanning

                // Prepare the request to send to the server
                ClientRequest request = new ClientRequest("scan_tag_login", new Object[]{id});

                try {
                    ClientController.getClient().sendToServer(request);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Update the status label on the JavaFX Application Thread
                    Platform.runLater(() -> statusLabel.setText("Failed to send request to the server."));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                Platform.runLater(() -> statusLabel.setText("Scanning failed."));
            }
        }).start();
    }

    
    /**
     * Automatically fills in login credentials and performs a login, typically used after a successful scan-tag login.
     *
     * @param username the username to auto-fill
     * @param password the password to auto-fill
     */
    public void autoLogin(String username, String password) {
        // Set the username and password fields
        txtUsername.setText(username);
        txtPassword.setText(password);
        
        // Ensure "Shop" access mode is selected by default
        toggleShop.setSelected(true);
        
        // Trigger the login process
        handleLogin();
    }




}
