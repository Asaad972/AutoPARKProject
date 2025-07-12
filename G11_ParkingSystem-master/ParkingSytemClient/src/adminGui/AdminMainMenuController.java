package adminGui;


import client.ClientController;
import client.MainMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import utils.SceneNavigator;

/**
 * Controller for the Admin Main Menu screen.
 * Handles navigation to system logs, active parking orders, and subscriber management.
 */
public class AdminMainMenuController {

    private ClientController client;

    /**
     * Sets the client controller and adjusts UI visibility based on user role.
     *
     * @param client the client controller used to communicate with the server
     */
    public void setClient(ClientController client) {
        this.client = client;
        if ("supervisor".equalsIgnoreCase(client.getUserRole())) {
            if (btnLogs != null) {
                btnLogs.setVisible(false);
            }
            if (btnReports != null) {
                btnReports.setVisible(false);
            }
        }
    }

    @FXML private Button btnLogs;
    @FXML private Button btnReports;

    /**
     * Handles the exit button by sending a disconnect request to the server and closing the app.
     */
    @FXML
    private void handleExit(ActionEvent event) {
        MainMenuController controller = SceneNavigator.navigateToAndGetController(
            event, "/client/MainMenu.fxml", "Main Menu"
        );
        if (controller != null) controller.setClient(client);
    }

    /**
     * Handles the "View System Logs" button click.
     * Loads AdminLogs.fxml and transfers the ClientController via setClient().
     */
    @FXML
    private void handleViewLogs(ActionEvent event) {
        AdminLogsController controller = SceneNavigator.navigateToAndGetController(
            event, "/adminGui/AdminLogs.fxml", "System Logs"
        );
        if (controller != null) controller.setClient(client);
    }

    /**
     * Handles the "View Active Parking Orders" button click.
     * Sends a typed object request to the server, and loads the AdminOrders screen.
     */
    @FXML
    private void handleViewActiveParking(ActionEvent event) {
        AdminOrdersController controller = SceneNavigator.navigateToAndGetController(
            event, "/adminGui/AdminOrders.fxml", "Active Parking Details"
        );
        if (controller != null) controller.setClient(client);
    }

    /**
     * Handles the "Manage Subscribers" button click.
     * Loads AdminSubscriberManagement.fxml and passes the client to the next controller.
     */
    @FXML
    private void handleManageSubscribers(ActionEvent event) {
        AdminSubscribersController controller = SceneNavigator.navigateToAndGetController(
            event, "/adminGui/AdminSubscriberManagement.fxml", "Manage Subscribers"
        );
        if (controller != null) controller.setClient(client);
    }
    
    /**
     * Handles the "View Reports" button click.
     * Loads AdminReports.fxml and transfers the client controller.
     */
    @FXML
    private void handleViewReports(ActionEvent event) {
        AdminReportsController controller = SceneNavigator.navigateToAndGetController(
            event, "/adminGui/AdminReports.fxml", "View Reports"
        );
        if (controller != null) controller.setClient(client);
    }
}
