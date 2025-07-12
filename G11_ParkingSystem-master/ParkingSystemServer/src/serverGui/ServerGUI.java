package serverGui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application launcher for the Server GUI.
 * <p>
 * This class initializes and displays the server-side user interface defined in the
 * {@code ServerMain.fxml} layout file.
 * </p>
 */
public class ServerGUI extends Application {

	 /**
     * JavaFX entry point called after the application is launched.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if loading the FXML or initializing the scene fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Tell FXMLLoader where to find the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerMain.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Server GUI");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * The main method used to launch the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
