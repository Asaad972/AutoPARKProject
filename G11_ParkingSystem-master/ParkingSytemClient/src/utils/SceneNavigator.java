package utils;

import client.ClientController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class to handle general scene navigation within the application.
 */
public class SceneNavigator {

    /**
     * Navigates to the specified FXML scene and sets the window title.
     * If the destination is the SubscriberDashboard, it injects the ClientController.
     *
     * Supports both ActionEvent and null (for non-button-based navigation).
     *
     * @param event    The ActionEvent that triggered the navigation, or null.
     * @param fxmlPath The relative path to the FXML file (e.g., "/subscriberGui/SubscriberDashboard.fxml").
     * @param title    The title to display on the stage after navigation.
     */
    public static void navigateTo(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Check if it's the Subscriber Dashboard and inject the client
            if (fxmlPath.equals("/subscriberGui/SubscriberDashboard.fxml")) {
                Object controller = loader.getController();
                if (controller instanceof subscriberGui.SubscriberDashboardController dashboardController) {
                    dashboardController.setClient(ClientController.getClient());
                }
            }

            Stage stage;

            if (event != null && event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                stage = ClientController.getPrimaryStage();
            }


            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle(title);
                stage.show();
            } else {
                System.err.println("Could not determine a valid stage for navigation.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the given FXML, returns its controller, and sets the window scene and title.
     * Useful for retrieving the controller to call methods like setClient().
     *
     * @param <T>      The type of the controller class.
     * @param event    The ActionEvent that triggered the navigation, or null.
     * @param fxmlPath Path to the FXML file.
     * @param title    The window title.
     * @return The controller of the loaded FXML, or null if there was an error.
     */
    public static <T> T navigateToAndGetController(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();

            T controller = loader.getController();

            Stage stage;

            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                stage = ClientController.getPrimaryStage();
            }

            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle(title);
                stage.show();
            } else {
                System.err.println("Could not determine a valid stage for navigation.");
            }

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    } 
}
