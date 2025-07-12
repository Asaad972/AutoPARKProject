package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the BPARK client application.
 * Launches the JavaFX GUI starting with the connection screen.
 */
public class ClientMain extends Application {

    /**
     * Initializes the primary stage of the application.
     * Loads the ConnectView.fxml and sets it as the main scene.
     *
     * @param primaryStage the primary window for the JavaFX application
     * @throws Exception if the FXML cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/client/ConnectView.fxml"));
        Scene scene = new Scene((Parent) loader.load());
        primaryStage.setTitle("Connect to Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
