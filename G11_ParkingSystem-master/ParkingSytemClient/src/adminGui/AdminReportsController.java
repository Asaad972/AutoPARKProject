package adminGui;

import bpark_common.ClientRequest;
import client.ClientController;
import entities.MonthlyParkingTimeReport;
import entities.MonthlySubscriberReport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import utils.SceneNavigator;

import java.time.Year;
import java.util.List;

/**
 * Controller for AdminReports.fxml.
 * Displays monthly parking time and subscriber reports using charts.
 */
public class AdminReportsController {

    private ClientController client;

    @FXML private Label labelTotalHours;
    @FXML private PieChart parkingTimePieChart;
    @FXML private BarChart<String, Number> subscribersBarChart;
    @FXML private ComboBox<Integer> comboYear;
    @FXML private ComboBox<String> comboMonth;
    @FXML private Button btnBack;

    private int selectedYear;
    private int selectedMonth;

    /**
     * Sets the client controller, initializes year and month dropdowns,
     * and requests the initial reports for the default selected month.
     *
     * @param client the client controller used for communication
     */
    public void setClient(ClientController client) {
        this.client = client;
        client.setAdminReportsController(this);

        // populate year combo (current year and previous 2)
        int currentYear = Year.now().getValue();
        comboYear.setItems(FXCollections.observableArrayList(
                currentYear, currentYear - 1, currentYear - 2
        ));
        comboYear.getSelectionModel().selectFirst();

        // populate months
        comboMonth.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        comboMonth.getSelectionModel().select(java.time.LocalDate.now().getMonthValue() - 1);

        selectedYear = comboYear.getValue();
        selectedMonth = comboMonth.getSelectionModel().getSelectedIndex() + 1;

        requestBothReports(selectedYear, selectedMonth);
    }

    /**
     * Sends requests to the server for both parking time and subscriber reports.
     *
     * @param year  the selected year
     * @param month the selected month (1-based)
     */
    private void requestBothReports(int year, int month) {
        client.sendObjectToServer(new ClientRequest("get_monthly_parking_time_report", new Object[]{year, month}));
        client.sendObjectToServer(new ClientRequest("get_monthly_subscriber_report", new Object[]{year, month}));
    }

    /**
     * Handles changes in the selected year or month from the combo boxes.
     * Triggers a request for updated reports.
     *
     * @param event the action event triggered by the selection
     */
    @FXML
    private void handleMonthOrYearSelection(ActionEvent event) {
        selectedYear = comboYear.getValue();
        selectedMonth = comboMonth.getSelectionModel().getSelectedIndex() + 1;
        requestBothReports(selectedYear, selectedMonth);
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

    /**
     * Loads and displays the monthly parking time report in the admin UI.
     * Populates the pie chart with normal, extended, and delayed hours,
     * and updates the total hours label.
     *
     * @param report the MonthlyParkingTimeReport received from the server
     */
    public void loadParkingTimeReport(MonthlyParkingTimeReport report) {
        if (report == null) {
            Platform.runLater(() -> {
                labelTotalHours.setText("No parking time report available for this month.");
                parkingTimePieChart.getData().clear();
            });
            return;
        }

        Platform.runLater(() -> {
            int total = report.getNormalHours() + report.getExtendedHours() + report.getDelayedHours();
            labelTotalHours.setText(
                    String.format("Report for %s – Total: %d hours", report.getMonth(), total)
            );

            parkingTimePieChart.getData().clear();

            PieChart.Data normalData = new PieChart.Data("Normal (" + report.getNormalHours() + "h)", report.getNormalHours());
            PieChart.Data extendedData = new PieChart.Data("Extended (" + report.getExtendedHours() + "h)", report.getExtendedHours());
            PieChart.Data delayedData = new PieChart.Data("Delayed (" + report.getDelayedHours() + "h)", report.getDelayedHours());

            parkingTimePieChart.getData().addAll(normalData, extendedData, delayedData);
        });
    }

    /**
     * Loads and displays the monthly subscriber report in the admin UI.
     * Populates the bar chart with daily subscriber counts.
     *
     * @param report the MonthlySubscriberReport received from the server
     */
    public void loadSubscriberReport(MonthlySubscriberReport report) {
        if (report == null) {
            Platform.runLater(() -> {
                subscribersBarChart.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("No data available");
                subscribersBarChart.getData().add(series);
            });
            return;
        }

        Platform.runLater(() -> {
            subscribersBarChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Subscribers");

            List<Integer> counts = report.getDailySubscriberCounts();
            for (int day = 1; day <= 31; day++) {
                int count = (day <= counts.size()) ? counts.get(day - 1) : 0;
                series.getData().add(new XYChart.Data<>(String.valueOf(day), count));
            }

            subscribersBarChart.getData().add(series);
        });
    }
}
