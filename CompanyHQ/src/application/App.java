package application;

import controller.MainController;
import controller.SceneNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.CompanyHQImpl;
import org.omg.CORBA.*;

import java.io.IOException;

/**
 *
 * Main entry point for the application. Contains CORBA initialisation, including orb and naming service. Also registers
 * local server object with the POA. Further, initialises any JavaFX components.
 *
 *
 * @author Oskar Mampe U1564420
 *
 */
public class App extends Application {

    public static ORB orb = null;
    public static CompanyHQImpl companyHQ =  null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //SETTING STAGE
        primaryStage.setTitle("Assignment");

        primaryStage.setScene(createScene(loadMainPane()));

        //Needed to close properly, without this, the application is still running in the background
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
        SceneNavigator.mPrimaryStage = primaryStage;
    }

    /**
     * Loads the main fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = loader.load(getClass().getResourceAsStream(SceneNavigator.MAIN));

        //Main controller is the controller for the main fxml.
        MainController mainController = loader.getController();

        //Scenenavigator is a singleton responsible for scene switching. It needs the main controller to load the main fxml properly.
        SceneNavigator.setMainController(mainController);
        SceneNavigator.loadScene(SceneNavigator.REGISTRATION);

        return mainPane;
    }

    /**
     * Creates the main application scene.
     *
     * @param mainPane the main application layout.
     *
     * @return the created scene.
     */
    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);

        scene.getStylesheets().setAll(getClass().getResource("../resources/styles/styles.css").toExternalForm());

        return scene;
    }

    /**
     *
     * Main function. Initialises the orb and launches the FXML.
     *
     * @param args String[] which represent anything passed by console.
     */
    public static void main(String[] args) {
        try {
            // Initialize the ORB
            orb = ORB.init(args, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }
}
