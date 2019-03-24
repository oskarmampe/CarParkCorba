package application;

import Server.PayStation;
import Server.PayStationHelper;
import controller.MainController;
import controller.SceneNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.PayStationServer;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;

public class App extends Application {

    public static ORB orb = null;
    public static org.omg.CORBA.Object nameService = null;
    public static org.omg.CORBA.Object localServer = null;
    public static PayStationServer payStation = null;
    public static NamingContext nameContext = null;
    public static NamingContextExt nameServiceExt = null;
    public static POA rootpoa = null;

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




    public static void main(String[] args) {
        try {
            // Initialize the ORB
            orb = ORB.init(args, null);

            rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

            // The object name passed in on the command line
            //String name = args[0];
            // See if a stringified object reference/URL was provided
            //if (name.startsWith("corbaname") || name.startsWith("corbaloc") ||
            //        name.startsWith("IOR")) {
            //    System.out.println("Attempting to lookup " + args[0]);
            //    localServer = orb.string_to_object(name);
            //}
            // Otherwise, do a traditional Naming Service lookup using the
            // services being referenced by our local ORB
            if (false) {
            } else {
                // Get the Name Service
                try {
                    nameService = orb.resolve_initial_references("NameService");
                } catch (InvalidName invN) {
                    System.out.println("Couldn't locate a Naming Service");
                    System.exit(1);
                }
                nameContext = NamingContextHelper.narrow(nameService);
                nameServiceExt = NamingContextExtHelper.narrow(nameContext);
                payStation = new PayStationServer("Huddersfield");


                org.omg.CORBA.Object ref = rootpoa.servant_to_reference(payStation);
                PayStation cref = PayStationHelper.narrow(ref);
                System.out.println(cref);
                String name = "paystationName";
                NameComponent[] payStationName = nameServiceExt.to_name(name);
                nameServiceExt.rebind(payStationName, cref);
                System.out.println(orb.object_to_string(ref));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }
}
