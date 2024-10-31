package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.*;

/**
 * Main application class for the Checkers application. ,
 * This class extends from {@link Application} and sets up the primary stage of the application.
 */
public class Main extends Application {

    /**
     * Starts the primary stage of the application.
     * This method sets up the main window of the application, loading the necessary FXML,
     * setting the scene, and displaying the primary stage.
     * 
     * @param primaryStage The primary stage for this application, onto which 
     *                     the application scene can be set.
     * @throws Exception If there is any issue loading the FXML or setting up the scene.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("MainPage.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            // scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            primaryStage.setTitle("Welcome to Checkers");
            primaryStage.setScene(scene);
            primaryStage.show(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to launch the application.,
     * This is the entry point method from where the JavaFX application is launched.
     * 
     * @param args Command line arguments passed to the application. 
     *             Not used in this application.
     */
    public static void main(String[] args) {
        launch(args); 
    }
}

