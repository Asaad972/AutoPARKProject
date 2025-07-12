package subscriberGui;

import bpark_common.ClientRequest;
import client.ClientController;
import client.MainMenuController;
import entities.ParkingHistory;
import entities.Subscriber;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import utils.SceneNavigator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the subscriber dashboard.
 * Handles display of parking history and navigation between screens.
 */
public class SubscriberDashboardController {

    private ClientController client;

    @FXML private TableView<ParkingHistory> tableHistory;
    @FXML private TableColumn<ParkingHistory, String> colEntryTime;
    @FXML private TableColumn<ParkingHistory, String> colExitTime;
    @FXML private TableColumn<ParkingHistory, String> colHistorySpot;
    @FXML private TableColumn<ParkingHistory, String> colWasExtended;
    @FXML private TableColumn<ParkingHistory, String> colWasLate;
    @FXML private Label labelSpot;
    @FXML private Label labelEntryTime;
    @FXML private Label labelTimeRemaining;
    @FXML private javafx.scene.control.Button btnExtend;
    @FXML private javafx.scene.control.Button btnReserve;
    @FXML private javafx.scene.control.Button btnPickup;
    @FXML private javafx.scene.control.Button btnDeposit;


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ActionEvent lastEvent;

    /**
     * Injects the active client controller into this controller.
     *
     * @param client The active ClientController instance.
     */
    public void setClient(ClientController client) {
        this.client = client;
        if (client != null) {
            client.setSubscriberDashboardController(this);
        }
    }

    @FXML
    private void openDetails(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/subscriberGui/SubscriberSettings.fxml", "BPARK - Subscriber Settings");
    }

    @FXML
    private void openExtendParking(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/subscriberGui/ExtendParking.fxml", "BPARK - Extend Parking");
    }

    /**
     * Opens the reservation window after the server confirms availability.
     * Called by ClientController when the server response is positive.
     *
     * @param event The ActionEvent that originally triggered the reservation attempt.
     */
    public void openReservationWindow(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/subscriberGui/ReservationRequest.fxml", "BPARK - Reserve Parking");
    }



    @FXML
    private void openCarPickup(ActionEvent event) {
        subscriberGui.CarPickupController controller = SceneNavigator.navigateToAndGetController(
            event, "/subscriberGui/CarPickup.fxml", "BPARK - Car Pickup"
        );
        if (controller != null) {
            controller.setClient(ClientController.getClient()); // set the client controller!
            ClientController.getClient().setCarPickupController(controller); // for response routing
        }
    }

    @FXML
    private void handleOpenPublicAvailability(ActionEvent event) {
        SceneNavigator.navigateTo(event, "/guestGui/PublicAvailability.fxml", "Public Availability");
    }

    /**
     * Requests the server to verify whether the user has an active deposit.
     * Uses ClientRequest instead of a raw string.
     *
     * @param event The action event triggering the request.
     */
    @FXML
    private void openCarDeposit(ActionEvent event) {
        this.setLastEvent(event);

        String code = client.getCurrentSubscriber().getSubscriberCode();
        // Send check_active as a ClientRequest
        client.sendObjectToServer(
            new ClientRequest("check_active", new Object[]{code})
        );
    }

    /**
     * Handles logout: clears session data and loads main menu screen.
     *
     * @param event The logout button action event.
     */
    @FXML
    private void logout(ActionEvent event) {
        ClientController client = ClientController.getClient();
        if (client != null) {
            client.setUserRole(null);
            client.setCurrentSubscriber(null);
        }

        MainMenuController controller = SceneNavigator.navigateToAndGetController(event,
                "/client/MainMenu.fxml", "BPARK - Main Menu");

        if (controller != null) {
            Stage stage = ClientController.getPrimaryStage();
            controller.setClient(client);
            controller.setStage(stage);
        }
    }

    /**
     * Populates the parking history TableView and updates the current parking session labels.
     *
     * @param history the list of ParkingHistory objects to display
     */
    public void setParkingHistoryData(ObservableList<ParkingHistory> history) {
        // Configure table columns
        colEntryTime.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getEntryTime().format(formatter))
        );
        colExitTime.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getExitTime().format(formatter))
        );
        colHistorySpot.setCellValueFactory(cell ->
            new SimpleStringProperty(String.valueOf(cell.getValue().getParkingSpaceId()))
        );
        colWasExtended.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().isExtended() ? "Yes" : "No"))
        ;
        colWasLate.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().isWasLate() ? "Yes" : "No"))
        ;

        // Highlight late sessions
        tableHistory.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ParkingHistory item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.isWasLate()) {
                    setStyle("-fx-background-color: #FFCDD2;");
                } else {
                    setStyle("");
                }
            }
        });

        // Bind data
        tableHistory.setItems(history);

        // Detect and display active session
        LocalDateTime now = LocalDateTime.now();
        history.stream()
            .filter(h -> !h.getEntryTime().isAfter(now) && h.getExitTime().isAfter(now))
            .findFirst()
            .ifPresentOrElse(active -> {
                labelSpot.setText(String.valueOf(active.getParkingSpaceId()));
                labelEntryTime.setText(active.getEntryTime().format(formatter));
                long minsLeft = Duration.between(now, active.getExitTime()).toMinutes();
                labelTimeRemaining.setText(minsLeft + " min");
            }, () -> {
                labelSpot.setText("---");
                labelEntryTime.setText("---");
                labelTimeRemaining.setText("---");
            });

        centerColumn(colEntryTime);
        centerColumn(colExitTime);
        centerColumn(colHistorySpot);
        centerColumn(colWasExtended);
        centerColumn(colWasLate);


    }

    /**
     * Initializes the dashboard controller by loading the subscriber's parking history.
     * Also refreshes when the window regains focus.
     */
    @FXML
    public void initialize() {
        refreshParkingHistory();

        tableHistory.sceneProperty().addListener((sceneObs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((winObs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.focusedProperty().addListener((focusObs, wasFocused, isNowFocused) -> {
                            if (isNowFocused) {
                                refreshParkingHistory();
                            }
                        });
                    }
                });
            }
        });
        
     //Access mode login using your exact button names
        String accessMode = ClientController.getClient().accessMode;
        if ("home".equals(accessMode)) {
            // Hide deposit and pickup buttons
            if (btnDeposit != null) btnDeposit.setVisible(false);
            if (btnPickup != null) btnPickup.setVisible(false);
        } 
    }

    /**
     * Requests the latest parking history from the server for the current subscriber using ClientRequest.
     */
    public void refreshParkingHistory() {
        Platform.runLater(() -> {
            Subscriber subscriber = ClientController.getClient().getCurrentSubscriber();
            if (subscriber != null) {
                String code = subscriber.getSubscriberCode();
                // Now using ClientRequest instead of ParkingHistoryRequest for protocol consistency
                ClientController.getClient().sendObjectToServer(
                    new ClientRequest("get_parking_history", new Object[]{code})
                );
            } else {
                System.out.println("No subscriber logged in.");
            }
        });
    }

    /**
     * Returns the last ActionEvent that triggered a reservation or deposit request.
     *
     * @return the last ActionEvent
     */
	public ActionEvent getLastEvent() {
		return lastEvent;
	}

	/**
	 * Stores the ActionEvent that triggered a reservation or deposit request.
	 *
	 * @param lastEvent the triggering ActionEvent
	 */
	public void setLastEvent(ActionEvent lastEvent) {
		this.lastEvent = lastEvent;
	}
	
	
	/**
	 * Opens the screen to deposit a reserved car using a confirmation code.
	 *
	 * @param event the ActionEvent triggered by clicking the deposit reserved button
	 */
	@FXML
	private void handleDepositReservedCar(ActionEvent event) {
	    subscriberGui.DepositReservedParkingController controller =
	        SceneNavigator.navigateToAndGetController(event, "/subscriberGui/CarDepositReserved.fxml", "Car Deposit Reserved");

	    if (controller != null) {
	        controller.setClient(ClientController.getClient());
	    } else {
	        System.out.println("Could not load DepositReservedParkingController.");
	    }
	}

	@SuppressWarnings("unused")
	private <T> void centerColumn(TableColumn<T, String> column) {
	    column.setCellFactory(col -> new TableCell<T, String>() {
	        private final Label label = new Label();

	        {
	            label.setAlignment(Pos.CENTER);
	            label.setMaxWidth(Double.MAX_VALUE);
	            label.setMaxHeight(Double.MAX_VALUE);
	            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	            setStyle("-fx-padding: 0;");
	        }

	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);

	            if (empty || item == null) {
	                setGraphic(null);
	            } else {
	                label.setText(item);
	                setGraphic(label);
	            }
	        }
	    });
	}





}
