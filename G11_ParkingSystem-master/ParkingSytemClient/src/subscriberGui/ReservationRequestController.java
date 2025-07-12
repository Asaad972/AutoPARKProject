package subscriberGui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import utils.SceneNavigator;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import bpark_common.ClientRequest;
import client.ClientController;
import entities.Reservation;


/**
 * Controller for ReservationRequest.fxml.
 * Handles the reservation request process and navigates back to the dashboard.
 */
public class ReservationRequestController {
	
	@FXML
	private DatePicker datePicker;

	@FXML
	private ComboBox<LocalTime> timeCombo;

	@FXML
	private Label lblResult;


    /**
     * Handles the "Back" button action to return to the Subscriber Dashboard.
     * Reloads the dashboard FXML and refreshes its data.
     *
     * @param event The ActionEvent triggered by the user's interaction.
     */
    @FXML
    private void handleBackButton(ActionEvent event) {
    	SceneNavigator.navigateTo(event, "/subscriberGui/SubscriberDashboard.fxml", "BPARK - Subscriber Dashboard");
    }

    /**
     * Initializes the date picker and sets up valid date and time ranges.
     */
    @FXML
    public void initialize() {
    	
    	// Register this controller in the ClientController
        ClientController.getClient().reservationRequestController = this;
        
        // Restrict date picker to 24h from now up to 7 days
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate minDate = LocalDate.now().plusDays(1);
                LocalDate maxDate = LocalDate.now().plusDays(7);

                setDisable(empty || date.isBefore(minDate) || date.isAfter(maxDate));
            }
        });

        // Populate time combo box when date is selected
        datePicker.setOnAction(event -> onDatePicked());
    }

    /**
     * Populates the time combo box with valid start times for the selected date.
     * - Previously added times locally, but now just sends request to server and disables the combo box until data arrives.
     */
    private void updateAvailableTimes() {
        timeCombo.getItems().clear(); // Clear old data
        timeCombo.setDisable(true);   // Disable until server data arrives

        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) return;

        // Show loading state
        lblResult.setText("Loading available times...");
        lblResult.setStyle("-fx-text-fill: blue;");

        // Let server handle real validation
        String subscriberCode = ClientController.getClient().getCurrentSubscriber().getSubscriberCode();
        ClientRequest request = new ClientRequest("get_valid_start_times", new Object[]{selectedDate, subscriberCode});
        ClientController.getClient().sendObjectToServer(request);
    }



    /**
     * Called when the user picks a date.
     */
    @FXML
    private void onDatePicked() {

    	
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) return;
        
        // Immediately update available times locally
        updateAvailableTimes();
    }


    /**
     * Called by ClientController when the server sends available times
     *@param availableTimes list of available time slots to show in the combo box
     */
    public void updateTimeComboBox(List<LocalTime> availableTimes) {
        timeCombo.setDisable(false); // Enable the combo box

        timeCombo.getItems().clear();

        //NullPointerException
        if (availableTimes != null && !availableTimes.isEmpty()) {
            timeCombo.getItems().addAll(availableTimes);
            lblResult.setText("Available slots loaded!");
            lblResult.setStyle("-fx-text-fill: green;");
        } else {
            lblResult.setText("No available time slots. Please choose another date.");
            lblResult.setStyle("-fx-text-fill: red;");
        }
    }


    /**
     * Handles the reservation button click.
     * Validates that both date and time are selected, constructs a Reservation entity using the constructor,
     * and sends it to the server for database insertion.
     *
     * If the date or time is not picked, displays an error message.
     * Otherwise, builds the Reservation object and sends it to the server in a ClientRequest.
     */
    @FXML
    private void onReservationClick() {
        // Get selected date and time from the UI
        LocalDate selectedDate = datePicker.getValue();
        LocalTime selectedTime = timeCombo.getValue();

        // Validate date selection
        if (selectedDate == null) {
            lblResult.setText("Please select a date.");
            lblResult.setStyle("-fx-text-fill: red;");
            return;
        }

        // Validate time selection
        if (selectedTime == null) {
            lblResult.setText("Please select a time.");
            lblResult.setStyle("-fx-text-fill: red;");
            return;
        }

        // Combine date and time into a LocalDateTime object
        LocalDateTime reservationDate = LocalDateTime.of(selectedDate, selectedTime);

        // Get the subscriber code from the current session
        String subscriberCode = ClientController.getClient().getCurrentSubscriber().getSubscriberCode();

        // TODO: Replace with your real parking spot selection logic
        int parkingSpaceId = 1; // Example: default parking spot

        // Create Reservation object using constructor
        Reservation reservation = new Reservation(
            0,                  // reservationId (to be set by the database)
            subscriberCode,     // subscriber code
            parkingSpaceId,     // parking spot id
            reservationDate,    // reservation date and time
            null,               // confirmation code (optional, can be null)
            "active"            // status
        );

        // Send the reservation object to the server for DB insertion
        ClientRequest request = new ClientRequest("add_reservation", new Object[]{reservation});
        ClientController.getClient().sendObjectToServer(request);

        // Show a loading or confirmation message to the user
        lblResult.setText("Sending reservation request...");
        lblResult.setStyle("-fx-text-fill: blue;");
    }




}
