package adminGui;

import client.ClientController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.SceneNavigator;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import bpark_common.ClientRequest;
import common.ChatIF;
import entities.ParkingHistory;

/**
 * Controller for AdminOrders.fxml.
 * Displays all currently active parking sessions and allows filtering by subscriber ID or spot ID.
 */
public class AdminOrdersController implements ChatIF {
    @FXML private Label lblStatus;

    /**
     * Displays a message received from the server by updating the status label.
     *
     * @param message the message to display
     */
    @Override
    public void display(String message) {
        Platform.runLater(() -> lblStatus.setText(message));
    }

    private ClientController client;

    @FXML private TableView<ParkingHistory> tableActiveParking;
    @FXML private TableColumn<ParkingHistory, String> colActiveSubId;
    @FXML private TableColumn<ParkingHistory, Integer> colActiveSpotId;
    @FXML private TableColumn<ParkingHistory, String> colActiveEntryTime;
    @FXML private TableColumn<ParkingHistory, String> colActiveExpectedExitTime;
    @FXML private TableColumn<ParkingHistory, String> colActiveCode;

    @FXML private TextField txtSearchSubId;
    @FXML private TextField txtSearchSpot;

    @FXML private Button btnSearch;
    @FXML private Button btnClear;
    @FXML private Button btnRefresh;
    
    private ObservableList<ParkingHistory> allActiveSessions = FXCollections.observableArrayList();
    
    
    /**
     * Sets the client controller and triggers the initial loading of active parking sessions.
     *
     * @param client the client controller used to communicate with the server
     */
    public void setClient(ClientController client) {
        this.client = client;
        client.setAdminOrdersController(this);
        loadActiveSessions();
    }

    /**
     * Initializes the table columns, button event handlers, and UI behavior.
     * Called automatically after FXML is loaded.
     */
    @FXML
    public void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        colActiveSubId.setCellValueFactory(data -> 
        	new javafx.beans.property.SimpleStringProperty(data.getValue().getSubscriberCode()));
        colActiveSpotId.setCellValueFactory(new PropertyValueFactory<>("parkingSpaceId"));
        colActiveEntryTime.setCellValueFactory(data -> 
        	new javafx.beans.property.SimpleStringProperty(data.getValue().getEntryTime().format(formatter)));
        colActiveExpectedExitTime.setCellValueFactory(data -> 
        	new javafx.beans.property.SimpleStringProperty(data.getValue().getExitTime().format(formatter)));
        colActiveCode.setCellValueFactory(data -> 
        	new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getParkingSpaceId())));
        
        btnSearch.setOnAction(e -> handleSearch());
        btnClear.setOnAction(e -> handleClearSearch());
        btnRefresh.setOnAction(e -> loadActiveSessions());
    }
    
    /**
     * Sends a request to the server to retrieve all currently active parking sessions.
     * Clears the table and updates the status label.
     */
    private void loadActiveSessions() {
    	allActiveSessions.clear();
        tableActiveParking.getItems().clear();
        lblStatus.setText("Loading active sessions...");

        ClientRequest request = new ClientRequest("get_parking_history_all_active", new Object[0]);
        ClientController.getClient().sendObjectToServer(request);
    }
    
    /**
     * Populates the table with active parking sessions received from the server.
     *
     * @param sessions the list of active ParkingHistory objects
     */
    @FXML
    public void setActiveSessions(List<ParkingHistory> sessions) {
        javafx.application.Platform.runLater(() -> {
            allActiveSessions.setAll(sessions);
            tableActiveParking.setItems(allActiveSessions);
            lblStatus.setText(sessions.size() + " active sessions loaded.");
        });
    }
    
    /**
     * Filters the active parking table based on subscriber ID and/or spot ID.
     * Updates the table and status label with the filtered results.
     */
    private void handleSearch() {
    	String subId = txtSearchSubId.getText().trim();
        String spot = txtSearchSpot.getText().trim();

        List<ParkingHistory> filtered = allActiveSessions.stream()
            .filter(p -> (subId.isEmpty() || p.getSubscriberCode().contains(subId)) &&
                         (spot.isEmpty() || String.valueOf(p.getParkingSpaceId()).contains(spot)))
            .collect(Collectors.toList());

        tableActiveParking.setItems(FXCollections.observableArrayList(filtered));
        lblStatus.setText("Showing " + filtered.size() + " filtered results.");
    }
    
    /**
     * Clears the search fields and restores the full list of active parking sessions in the table.
     */
    private void handleClearSearch() {
    	txtSearchSubId.clear();
        txtSearchSpot.clear();
        tableActiveParking.setItems(allActiveSessions);
        lblStatus.setText("All sessions shown.");
    }
    
    /**
     * Handles the Back button click.
     * Navigates back to the Admin Main Menu screen.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        AdminMainMenuController controller = SceneNavigator.navigateToAndGetController(
            event, "/adminGui/AdminMainMenu.fxml", "Admin Dashboard"
        );
        if (controller != null) controller.setClient(client);
    }
}
