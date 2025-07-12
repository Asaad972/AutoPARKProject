package client;

import bpark_common.ServerResponse;
import entities.*;
import guestGui.PublicAvailabilityController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ocsf.client.AbstractClient;
import subscriberGui.CarDepositController;
import subscriberGui.EditSubscriberDetailsController;
import subscriberGui.ReservationRequestController;
import subscriberGui.DepositReservedParkingController;
import utils.SceneNavigator;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adminGui.AdminLogsController;
import adminGui.AdminOrdersController;
import adminGui.AdminReportsController;
import adminGui.AdminSubscribersController;

/**
 * The main client-side controller that manages the connection to the server,
 * handles incoming messages, and provides access to GUI components.
 * 
 * This class extends {@link AbstractClient} and implements the core logic
 * for message handling and GUI coordination in the BPARK system.
 * 
 * @author BPARK
 */
public class ClientController extends AbstractClient {

    private static ClientController clientInstance;
    private MainMenuController guiController;
    private EditSubscriberDetailsController editSubscriberDetailsController;
    private subscriberGui.SubscriberDashboardController subscriberDashboardController;
    private String userRole;
    private Subscriber currentSubscriber;
    private static Stage primaryStage;
    private CarDepositController carDepositController;
    private subscriberGui.ExtendParkingController extendParkingController;
    private subscriberGui.CarPickupController carPickupController;
    public String accessMode;
    public ReservationRequestController reservationRequestController;
    private subscriberGui.ForgotCodeController forgotCodeController;
    private adminGui.AdminOrdersController adminOrdersController;
    private adminGui.AdminSubscribersController adminSubscribersController;
    private adminGui.AdminReportsController adminReportsController;
    private adminGui.AdminLogsController adminLogsController;
    private adminGui.AdminParkingHistoryController adminParkingHistoryController;



    /**
     * Sets the active singleton instance of the client.
     * 
     * @param client The client controller instance.
     */
    public static void setClient(ClientController client) {
        clientInstance = client;
    }

    /**
     * Gets the current singleton client instance.
     * 
     * @return the active {@link ClientController} instance.
     */
    public static ClientController getClient() {
        return clientInstance;
    }
    
    /**
     * Sets the controller responsible for handling forgot code UI logic.
     *
     * @param controller The {ForgotCodeController} instance.
     */
    public void setForgotCodeController(subscriberGui.ForgotCodeController controller) {
        this.forgotCodeController = controller;
    }
    /**
     * Constructs a new client controller and opens the connection to the server.
     *
     * @param host         the server host
     * @param port         the server port
     * @param guiController the main menu GUI controller
     * @throws IOException if connection fails
     */
    public ClientController(String host, int port, MainMenuController guiController) throws IOException {
        super(host, port);
        this.guiController = guiController;
        openConnection();
    }

    /**
     * Sets the reference to the main menu GUI controller.
     *
     * @param guiController the main GUI controller
     */
    public void setGuiController(MainMenuController guiController) {
        this.guiController = guiController;
    }

    /**
     * Sets the controller responsible for editing subscriber details.
     *
     * @param controller the edit subscriber controller
     */
    public void setEditSubscriberDetailsController(EditSubscriberDetailsController controller) {
        this.editSubscriberDetailsController = controller;
    }

    /**
     * Sets the subscriber dashboard controller.
     *
     * @param controller the dashboard controller
     */
    public void setSubscriberDashboardController(subscriberGui.SubscriberDashboardController controller) {
        this.subscriberDashboardController = controller;
    }

    /**
     * Stores reference to the CarDepositController.
     *
     * @param controller The CarDepositController instance.
     */
    public void setCarDepositController(CarDepositController controller) {
        this.carDepositController = controller;
    }

    /**
     * Gets the current CarDepositController instance.
     *
     * @return The CarDepositController instance.
     */
    public CarDepositController getCarDepositController() {
        return carDepositController;
    }

    /**
     * Sets the reference to the active CarPickupController.
     * This controller will be used to display pickup results when a response is received from the server.
     *
     * @param controller the CarPickupController instance to set
     */
    public void setCarPickupController(subscriberGui.CarPickupController controller) {
        this.carPickupController = controller;
    }

    /**
     * Sets the ExtendParkingController instance for handling extend parking logic.
     *
     * @param controller The ExtendParkingController instance.
     */
    public void setExtendParkingController(subscriberGui.ExtendParkingController controller) {
        this.extendParkingController = controller;
    }

    /**
     * Gets the current user role.
     *
     * @return the user role string
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * Sets the current user role.
     *
     * @param userRole the user role string
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    /**
     * Gets the currently logged-in subscriber.
     *
     * @return the current {@link Subscriber}
     */
    public Subscriber getCurrentSubscriber() {
        return currentSubscriber;
    }

    /**
     * Sets the current subscriber.
     *
     * @param subscriber the current {@link Subscriber}
     */
    public void setCurrentSubscriber(Subscriber subscriber) {
        this.currentSubscriber = subscriber;
    }
    
    /**
     * Sets the controller used for managing subscribers in the admin panel.
     *
     * @param adminSubscribersController The {@link AdminSubscribersController} instance.
     */
    public void setAdminSubscribersController(adminGui.AdminSubscribersController adminSubscribersController) {
		this.adminSubscribersController = adminSubscribersController;
	}
    
    /**
     * Gets the AdminSubscribersController currently set for managing subscribers.
     *
     * @return The {@link AdminSubscribersController} instance.
     */
    public adminGui.AdminSubscribersController getAdminSubscribersController() {
		return adminSubscribersController;
	}
    
    /**
     * Sets the controller used for displaying and managing active parking sessions in the admin UI.
     *
     * @param adminOrdersController The {@link AdminOrdersController} instance.
     */
    public void setAdminOrdersController(adminGui.AdminOrdersController adminOrdersController) {
		this.adminOrdersController = adminOrdersController;
	}
    
    /**
     * Gets the controller responsible for managing active parking sessions.
     *
     * @return The {@link AdminOrdersController} instance.
     */
    public adminGui.AdminOrdersController getAdminOrdersController() {
		return adminOrdersController;
	}
    
    /**
     * Sets the controller used for handling admin reports such as parking time and subscriber data.
     *
     * @param adminReportsController The {@link AdminReportsController} instance.
     */
    public void setAdminReportsController(adminGui.AdminReportsController adminReportsController) {
		this.adminReportsController = adminReportsController;
	}
    
    /**
     * Gets the controller responsible for managing and displaying admin reports.
     *
     * @return The {@link AdminReportsController} instance.
     */
    public adminGui.AdminReportsController getAdminReportsController() {
		return adminReportsController;
	}
    
    /**
     * Sets the controller used to display system logs to the admin.
     *
     * @param adminLogsController The {@link AdminLogsController} instance.
     */
    public void setAdminLogsController(adminGui.AdminLogsController adminLogsController) {
		this.adminLogsController = adminLogsController;
	}
    
    /**
     * Gets the controller responsible for displaying system logs to the admin.
     *
     * @return The {@link AdminLogsController} instance.
     */
    public adminGui.AdminLogsController getAdminLogsController() {
		return adminLogsController;
	}
    
    /**
     * Sets the controller used to display parking history for a specific subscriber in the admin panel.
     *
     * @param adminParkingHistoryController The {AdminParkingHistoryController} instance.
     */
    public void setAdminParkingHistoryController(adminGui.AdminParkingHistoryController adminParkingHistoryController) {
		this.adminParkingHistoryController = adminParkingHistoryController;
	}
 

    /**
     * Sets the primary application stage for GUI transitions.
     *
     * @param stage the JavaFX primary stage
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Gets the primary application stage.
     *
     * @return the JavaFX primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Sends an object to the server via the OCSF framework.
     *
     * @param msg the message object to send
     */
    public void sendObjectToServer(Object msg) {
        try {
            super.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles incoming messages from the server and delegates to appropriate handlers.
     * All responses from the server are expected to be {@link ServerResponse}.
     *
     * @param msg the received message
     */
    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof ServerResponse response) {
            handleServerResponse(response);
        } else {
            System.out.println("Unknown message type from server: " + msg.getClass().getSimpleName());
        }
    }

    /**
     * Unified handler for {@link ServerResponse} messages from the server.
     * Decodes the command and routes to the appropriate GUI logic.
     *
     * @param response the {@link ServerResponse} object
     */
    private void handleServerResponse(ServerResponse response) {
        String command = response.getCommand();
        boolean success = response.isSuccess();
        String message = response.getMessage();
        Object data = response.getData();

        switch (command) {
            case "LOGIN" -> handleLoginResponse(success, message);
            case "SUBSCRIBER_DATA" -> handleSubscriberData(data);
            case "SUBSCRIBER_UPDATE" -> handleSubscriberUpdate(success, message);
            case "HISTORY_LIST" -> handleHistoryList(data);
            case "AVAILABLE_SPOTS" -> handleAvailableSpots(data);
            case "RANDOM_SPOT" -> handleRandomSpot(success, data, message);
            case "CHECK_ACTIVE" -> handleCheckActive(success, message);
            case "PARKING_DEPOSIT" -> handleParkingDeposit(success, message);
            case "EXTEND_PARKING" -> handleExtendParkingResult(success, message);
            case "CAR_PICKUP" -> handleCarPickupResult(success, message);
            case "ACCESS_MODE" -> handleAccessMode(data);
            case "check_reservation_availability" -> handleReservationAvailabilityResponse(success, data);
            case "get_valid_start_times" -> handleValidStartTimes(data);
            case "add_reservation" -> handleReservationResponse(success, message);
            case "send_code_email" -> handleForgotCodeEmailResponse(success, message);
            case "scan_tag_login" -> handleScanTagLoginResponse(success, data, message);
            case "CheckAndDepositReservedCar" -> handleReservedDepositResponse(success, message);
            case "CancelReservationByCode" -> handleCancelReservationByCodeResponse(message);
            case "ADMIN_ACTIVE_SESSIONS" -> handleAdminActiveSessionsResponse(data);
            case "ADMIN_SUBSCRIBERS" -> handleAdminSubscribersResponse(data);
            case "ADMIN_LOGS" -> handleAdminLogsResponse(data);
            case "monthly_parking_time_report_result" -> handleMonthlyParkingTimeReport(response);
            case "monthly_subscriber_report_result" -> handleMonthlySubscriberReport(response);
            case "get_subscriber_contact" -> handleSubscriberContactResponse(success, message, data);

            default -> System.out.println("Unknown server response command: " + command);
        }

    }
    /**
     * Handles the server response for the monthly parking time report.
     * Delegates the data to the adminReportsController to display.
     *
     * @param response the server response containing a MonthlyParkingTimeReport
     */
    private void handleMonthlyParkingTimeReport(ServerResponse response) {
        MonthlyParkingTimeReport report = (MonthlyParkingTimeReport) response.getData();
        if (adminReportsController != null) {
            adminReportsController.loadParkingTimeReport(report);
        }
    }

    /**
     * Handles the server response for the monthly subscriber report.
     * Delegates the data to the adminReportsController to display.
     *
     * @param response the server response containing a MonthlySubscriberReport
     */
    private void handleMonthlySubscriberReport(ServerResponse response) {
        MonthlySubscriberReport report = (MonthlySubscriberReport) response.getData();
        if (adminReportsController != null) {
            adminReportsController.loadSubscriberReport(report);
        }
    }

	/**
     * Handles the login response.
     * 
     * @param success if login succeeded
     * @param message response message
     */
    private void handleLoginResponse(boolean success, String message) {
        Platform.runLater(() -> {
            if (guiController != null) {
                guiController.handleLoginResponse(success, message);
            }
            if (success) {
                String prefix = "Login successful: ";
                if (message.startsWith(prefix)) {
                    setUserRole(message.substring(prefix.length()));
                }
            }
        });
    }

    /**
     * Handles receiving the full Subscriber data after login.
     * 
     * @param data the Subscriber entity from the server
     */
    private void handleSubscriberData(Object data) {
        if (data instanceof Subscriber subscriber) {
            this.currentSubscriber = subscriber;
            System.out.println("Subscriber received: " + subscriber.getFullName());
        }
    }

    /**
     * Handles the result of a subscriber data update.
     * 
     * @param success update result
     * @param message message from the server
     */
    private void handleSubscriberUpdate(boolean success, String message) {
        if (editSubscriberDetailsController != null) {
            editSubscriberDetailsController.onUpdateResponse(success, message);
        } else {
            System.out.println("UpdateResponse received but no controller instance set");
        }
    }

    /**
     * Handles a list of parking history records.
     *
     * @param data a List of ParkingHistory objects
     */
    @SuppressWarnings("unchecked")
    private void handleHistoryList(Object data) {
        if (data instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof ParkingHistory) {
            List<ParkingHistory> historyList = (List<ParkingHistory>) list;
            Platform.runLater(() -> {
                System.out.println("Received parking history: " + historyList.size() + " records");
                
                if (ClientController.getClient().getCurrentSubscriber() != null && subscriberDashboardController != null) {
                    subscriberDashboardController.setParkingHistoryData(
                        FXCollections.observableArrayList(historyList)
                    );
                }
                else if(adminParkingHistoryController != null) {
                	adminParkingHistoryController.setParkingHistoryData(FXCollections.observableArrayList(historyList));
                }
                else {
                    System.out.println("subscriberDashboardController is null.");
                }
            });
        }
    }

    /**
     * Handles available parking spots list.
     *
     * @param data a List of ParkingSpace objects
     */
    @SuppressWarnings("unchecked")
    private void handleAvailableSpots(Object data) {
        if (data instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof ParkingSpace) {
            PublicAvailabilityController controller = PublicAvailabilityController.getCurrentInstance();
            if (controller != null) {
                Platform.runLater(() -> controller.updateTable((List<ParkingSpace>) list));
            } else {
                System.out.println("PublicAvailabilityController instance is null.");
            }
        }
    }



    /**
     * Handles the server response for getting a random parking spot.
     * If the spot is unavailable (ID == -1), notifies user and goes back to dashboard.
     *
     * @param success   whether a spot was found
     * @param data      the ParkingSpace object or null
     * @param message   the response message
     */
    private void handleRandomSpot(boolean success, Object data, String message) {
        Platform.runLater(() -> {
            if (!success || !(data instanceof ParkingSpace spot) || spot.getParkingSpaceId() == -1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Available Parking");
                alert.setHeaderText(null);
                alert.setContentText("There are currently no available parking spots. Please try again later.");
                alert.showAndWait();

                SceneNavigator.navigateTo(null,
                    "/subscriberGui/SubscriberDashboard.fxml",
                    "BPARK - Subscriber Dashboard");
            } else {
                CarDepositController controller = this.getCarDepositController();
                if (controller != null) {
                    controller.setSpot(String.valueOf(((ParkingSpace) data).getParkingSpaceId()));
                } else {
                    System.out.println("CarDepositController is not registered.");
                }
            }
        });
    }

    /**
     * Handles the check for active deposit status.
     * If the user has an active deposit, shows an alert.
     * If not, navigates to the CarDeposit.fxml GUI.
     *
     * @param hasActive whether an active deposit exists
     * @param message   the message to display
     */
    private void handleCheckActive(boolean hasActive, String message) {
        Platform.runLater(() -> {
            if (hasActive) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Deposit Blocked");
                alert.setHeaderText(null);
                alert.setContentText("You already have an active parking.");
                alert.showAndWait();
            } else {
                // Directly navigate to the CarDeposit screen
                CarDepositController controller = SceneNavigator.navigateToAndGetController(
                        null, "/subscriberGui/CarDeposit.fxml", "BPARK - Vehicle Deposit");

                if (controller != null) {
                    controller.setClient(this);                // Inject client controller
                    controller.onLoaded();                     // Request spot
                    this.setCarDepositController(controller);  // Register controller for callbacks
                } else {
                    System.out.println("Failed to load CarDepositController.");
                }
            }
        });
    }


    /**
     * Handles the result of a parking deposit attempt.
     *
     * @param success success flag
     * @param message message text
     */
    private void handleParkingDeposit(boolean success, String message) {
        Platform.runLater(() -> {
            if (!success && "You already have an active parking reservation.".equals(message)) {
                CarDepositController controller = getCarDepositController();
                if (controller != null) {
                    controller.setDepositRejected(true);
                }
            }
            
        });
    }

    /**
     * Handles the result of an extend parking request.
     *
     * @param success success flag
     * @param message message text
     */
    private void handleExtendParkingResult(boolean success, String message) {
        if (extendParkingController != null) {
            extendParkingController.onUpdateResponse(success, message);
        } else {
            System.out.println("No ExtendParkingController registered for extend result.");
        }
    }

    /**
     * Handles the result of a car pickup request.
     *
     * @param success success flag
     * @param message message text
     */
    private void handleCarPickupResult(boolean success, String message) {
        if (carPickupController != null) {
            carPickupController.handlePickupResponse(success, message);
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle(success ? "Car Pickup" : "Pickup Failed");
                alert.setHeaderText(null);
                alert.setContentText((success ? "yes" : "no") + message);
                alert.showAndWait();
            });
        }
    }
    
    /**
     * Handles the received access mode data from the server.
     *
     * @param data the access mode ("home" or "shop")
     */
    private void handleAccessMode(Object data) {
    	this.accessMode = (String) data;
    	System.out.println("Access mode received: " + accessMode);

    }
    
    /**
     * Handles the response from the server for the check reservation availability request.
     * If at least 40% of the parking spots are available, opens the reservation window.
     * Otherwise, shows an alert to the user.
     *
     * @param success Indicates if the request to the server succeeded.
     * @param data    The server's response data (should be a Boolean for availability).
     */
    private void handleReservationAvailabilityResponse(boolean success, Object data) {
        if (success && data instanceof Boolean canReserve && canReserve) {
            Platform.runLater(() -> {
                if (subscriberDashboardController != null) {
                 //   subscriberDashboardController.openReservationWindow(subscriberDashboardController.getLastEvent());
                }
            });
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Reservation Unavailable");
                alert.setHeaderText(null);
                alert.setContentText("Reservation is not possible: less than 40% of parking spots are available.");
                alert.showAndWait();
            });
        }
    }


    /**
     * Handles the list of valid start times received from the server.
     * Updates the UI to show only the valid time slots.
     *
     * @param data The list of LocalTime objects (valid start times).
     */
    @SuppressWarnings("unchecked")
    private void handleValidStartTimes(Object data) {
        List<LocalTime> availableTimes = (List<LocalTime>) data;

        Platform.runLater(() -> {
            if (reservationRequestController != null) {
                reservationRequestController.updateTimeComboBox(availableTimes);
            }
        });
    }

    /**
     * Handles the server response for a reservation attempt.
     * Shows a confirmation or error message to the user.
     *
     * @param success True if reservation succeeded, false otherwise.
     * @param message The server message to show.
     */
    private void handleReservationResponse(boolean success, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(success ? "Reservation Success" : "Reservation Failed");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();

            // Optionally, after a successful reservation, you can navigate the user somewhere
            if (success && subscriberDashboardController != null) {
                SceneNavigator.navigateTo(null,
                    "/subscriberGui/SubscriberDashboard.fxml",
                    "BPARK - Subscriber Dashboard");
            }
        });
    }
    /**
     * Handles the server response for sending the parking code to the subscriber's email.
     * Delegates the response handling to the ForgotCodeController's handleEmailResponse method.
     *
     * @param success Whether the operation was successful.
     * @param message The message from the server.
     */
    private void handleForgotCodeEmailResponse(boolean success, String message) {
        if (forgotCodeController != null) {
            // Construct a ServerResponse to pass to the controller (for UI handling)
            ServerResponse response = new ServerResponse(
                "send_code_email",
                success,
                message,
                null
            );
            forgotCodeController.handleEmailResponse(response);
        } else {
            // Optional: fallback in case controller is not set
            Platform.runLater(() -> {
                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle(success ? "Code Sent" : "Error");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        }
    }
    
    /**
     * Handles the server response for a scan tag login attempt.
     *
     * @param success Indicates if the login was successful.
     * @param data    The data returned from the server, typically user credentials.
     * @param message A message accompanying the response.
     */
    private void handleScanTagLoginResponse(boolean success, Object data, String message) {
        Platform.runLater(() -> {
            if (success && data instanceof String[] credentials && credentials.length == 2) {
                String username = credentials[0];
                String password = credentials[1];
                if (guiController != null) {
                    guiController.autoLogin(username, password);
                }
            } else {
                // Display an error message to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            }
        });
    }
    
    /**
     * Handles the server response containing all active parking sessions.
     * Passes the list to the {@link AdminOrdersController} for display.
     *
     * @param data The data object from the server, expected to be a list of {@link ParkingHistory}.
     */
    @SuppressWarnings("unchecked")
	private void handleAdminActiveSessionsResponse(Object data) {
    	if (!(data instanceof List<?> list && (list.isEmpty() || list.get(0) instanceof ParkingHistory))) {
            System.err.println("Invalid or failed session data.");
            return;
        }

        List<ParkingHistory> sessions = (List<ParkingHistory>) data;

        AdminOrdersController controller = getAdminOrdersController();
        if (controller != null) {
            controller.setActiveSessions(sessions);
        } else {
            System.err.println("AdminOrdersController not registered.");
        }
    }
    
    /**
     * Handles the server response containing a list of all subscribers.
     * Passes the list to the {@link AdminSubscribersController} for display.
     *
     * @param data The data object from the server, expected to be a list of {@link Subscriber}.
     */
    @SuppressWarnings("unchecked")
	private void handleAdminSubscribersResponse(Object data) {
    	if (!(data instanceof List<?> list && (list.isEmpty() || list.get(0) instanceof Subscriber))) {
            System.err.println("Invalid or failed session data.");
            return;
        }

        List<Subscriber> sessions = (List<Subscriber>) data;

        AdminSubscribersController controller = getAdminSubscribersController();
        if (controller != null) {
            controller.setAllSubscribers(sessions);
        } else {
            System.err.println("AdminOrdersController not registered.");
        }
    }
    
    private void handleAdminLogsResponse(Object data) {
        if (!(data instanceof List<?> rawList)) {
            System.err.println("Invalid or missing log data.");
            return;
        }

        // Create a safe typed list
        List<SystemLog> logs = new ArrayList<>();

        for (Object item : rawList) {
            if (item instanceof SystemLog log) {
                logs.add(log);
            } else {
                System.err.println("Skipping invalid log entry: " + item);
            }
        }

        AdminLogsController controller = getAdminLogsController();
        if (controller != null) {
            controller.setLogs(logs);
        } else {
            System.err.println("AdminLogsController not registered.");
        }
    }
    /**
     * Handles the server response for retrieving subscriber contact information (email and phone).
     * If successful, passes the masked values to the {@link ForgotCodeController} to display updated text.
     *
     * @param success Indicates whether the server operation succeeded.
     * @param message The message from the server (informational or error).
     * @param data    A Map containing "email" and "phone" keys with their respective values.
     */
    private void handleSubscriberContactResponse(boolean success, String message, Object data) {
        if (success && data instanceof Map<?, ?> map) {
            Object emailObj = map.get("email");
            Object phoneObj = map.get("phone");

            String email = (emailObj instanceof String) ? (String) emailObj : null;
            String phone = (phoneObj instanceof String) ? (String) phoneObj : null;

            if (forgotCodeController != null) {
                if (email != null && !email.isEmpty()) {
                    forgotCodeController.updateEmailDisplay(email);
                }
                if (phone != null && !phone.isEmpty()) {
                    forgotCodeController.updatePhoneDisplay(phone);
                }
            } else {
                System.out.println("forgotCodeController is null!");
            }
        } else {
            System.out.println("Failed to get subscriber contact info: " + message);
        }
    }
    
    /**
     * Handles the server response for reserved car deposit using confirmation code.
     *
     * @param success success status from the server
     * @param message feedback or error message
     */
    private void handleReservedDepositResponse(boolean success, String message) {
        Platform.runLater(() -> {
            DepositReservedParkingController controller = DepositReservedParkingController.getInstance();
            if (controller != null) {
                controller.showServerResponse(message);
            } else {
                System.out.println("DepositReservedParkingController is not registered.");
            }
        });
    }

    /**
     * Handles the response from the server after a reservation cancellation attempt.
     *
     * @param message the success or error message returned from the server
     */
    private void handleCancelReservationByCodeResponse(String message) {
        Platform.runLater(() -> {
            DepositReservedParkingController controller = DepositReservedParkingController.getInstance();
            if (controller != null) {
                controller.showServerResponse(message);
            }
        });
    }


}
