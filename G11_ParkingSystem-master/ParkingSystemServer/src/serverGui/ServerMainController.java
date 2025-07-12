package serverGui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import server.BParkServer;
import server.ClientInfo;
import server.DBController;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 * Main controller for the server-side GUI.
 * Handles server startup, shutdown, and monthly report scheduling.
 * Also updates the status label and manages connections to the database.
 */


public class ServerMainController {

    @FXML private TextField serverIpField, serverPortField, dbIpField, dbPortField, dbUserField;
    @FXML private PasswordField dbPassField;
    @FXML private Label statusLabel;
    @FXML private Button connectButton, disconnectButton;
    @FXML private TableView<ClientInfo> clientTable;
    @FXML private TableColumn<ClientInfo, String> ipColumn, hostColumn, statusColumn;

    private BParkServer server;
    private ObservableList<ClientInfo> clients = FXCollections.observableArrayList();
    private MonthlyReportScheduler reportScheduler;

    
    
    /**
     * Initializes the server GUI.
     * 
     * Sets default values for input fields (server/db IPs and credentials),
     * and configures the client connection table.
     * This method runs automatically when the FXML view is loaded.
     */
    @FXML
    public void initialize() {
        try {
            serverIpField.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            serverIpField.setText("Unknown");
        }
        dbIpField.setText("localhost");
        dbPortField.setText("3306");
        dbUserField.setText("root");
        dbPassField.setText("Aa123456");
        serverPortField.setText("5555");

        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        clientTable.setItems(clients);
    }

    
    
    /**
     * Handles the server startup process.
     * 
     * Connects to the MySQL database using the provided credentials,
     * starts the server on the selected port, and launches the background 
     * monthly report scheduler. Also tests manual report generation.
     */
    @FXML
    void handleConnect() {
        if (server != null && server.isListening()) {
            statusLabel.setText("Server is already running.");
            return;
        }

        String dbIp = dbIpField.getText();
        String dbPort = dbPortField.getText();
        String user = dbUserField.getText();
        String pass = dbPassField.getText();
        int serverPort = Integer.parseInt(serverPortField.getText());

        String jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPort + "/bpark?serverTimezone=Asia/Jerusalem&useSSL=false&allowPublicKeyRetrieval=true";

        try {
            Connection conn = DriverManager.getConnection(jdbcUrl, user, pass);
            conn.close();

            statusLabel.setText("DB connected. Starting server...");

            server = new BParkServer(serverPort, this);
            server.listen();

            statusLabel.setText("Server running on port " + serverPort);

            reportScheduler = new MonthlyReportScheduler(() ->
                    statusLabel.setText("Monthly reports generated and saved."));
            reportScheduler.start();

            testGenerateReportsNow(); // Optional: for development

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    
    /**
     * Handles server shutdown.
     * 
     * Stops the server listener, cancels the monthly report scheduler if active,
     * and updates all connected client statuses to "Disconnected".
     */
    @FXML
    void handleDisconnect() {
        if (server != null) {
            try {
                server.close();
                server = null;
                statusLabel.setText("Server stopped running.");

                for (ClientInfo client : clients) {
                    client.setStatus("Disconnected");
                }

                if (reportScheduler != null) {
                    reportScheduler.stop();
                    reportScheduler = null;
                }

                clientTable.refresh();
            } catch (Exception e) {
                statusLabel.setText("Failed to stop server.");
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Server is not running.");
        }
    }

    
    
    /**
     * Exits the entire application process immediately.
     */
    @FXML
    void handleExit() {
        System.exit(0);
    }

    
    
    /**
     * Adds a client entry to the connection table.
     * 
     * Removes any existing entries with the same IP/host or ID to avoid duplicates,
     * and refreshes the table UI with the new entry.
     *
     * @param ip   the IP address of the connected client
     * @param host the hostname of the connected client
     * @param id   a unique identifier for the client (used for distinguishing entries)
     */
    public void addClient(String ip, String host, int id) {
        Platform.runLater(() -> {
            clients.removeIf(client -> client.getId() == id);
            clients.removeIf(client -> client.getIp().equals(ip) && client.getHost().equals(host));
            clients.add(new ClientInfo(ip, host, "Connected", id));
            clientTable.refresh();
        });
    }

    
    
    /**
     * Updates a client's connection status in the UI table.
     * 
     * Searches the client list by unique ID and changes the status text,
     * then refreshes the table to reflect the change.
     *
     * @param id     the unique identifier of the client
     * @param status the updated status label ("Connected" or "Disconnected")
     */
    public void updateClientStatus(int id, String status) {
        Platform.runLater(() -> {
            for (ClientInfo client : clients) {
                if (client.getId() == id) {
                    client.setStatus(status);
                    clientTable.refresh();
                    break;
                }
            }
        });
    }

    
    
    /**
     * Triggers a one-time manual report generation.
     * 
     * This is useful for development and validation purposes,
     * generating reports for a fixed date range (June 2025).
     */
    public void testGenerateReportsNow() {
        int year = 2025;
        int month = 6;
        DBController.generateMonthlyReports(year, month);
        System.out.println("Manual generation done for " + year + "-" + String.format("%02d", month));
    }
}
