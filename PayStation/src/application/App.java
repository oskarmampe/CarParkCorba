package application;

import controller.MainController;
import controller.SceneNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;

public class App extends Application {

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
            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object ref = null;
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

            // The object name passed in on the command line
            //String name = args[0];
            // See if a stringified object reference/URL was provided
            //if (name.startsWith("corbaname") || name.startsWith("corbaloc") ||
            //        name.startsWith("IOR")) {
            //    System.out.println("Attempting to lookup " + args[0]);
            //    ref = orb.string_to_object(name);
            //}
            // Otherwise, do a traditional Naming Service lookup using the
            // services being referenced by our local ORB
            if (false) {
            } else {
                try {
                    ref = orb.resolve_initial_references("NameService");
                } catch (InvalidName invN) {
                    System.out.println("Couldn't locate a Naming Service");
                    System.exit(1);
                }
                NamingContext nameContext = NamingContextHelper.narrow(ref);
                NameComponent comp = new NameComponent("localServerName", "");
                NameComponent path[] = {comp};
                try {
                    ref = nameContext.resolve(path);
                    System.out.println("ref = " + ref);

                    Any any1 = orb.create_any();
                    Any any2 = orb.create_any();

                    NVList arglist = orb.create_list(2);
                    any1.insert_string("station_name");
                    any2.insert_string("station_ior");
                    NamedValue nvArg = arglist.add_value("station_name", any1, org.omg.CORBA.ARG_IN.value);
                    NamedValue nvArg2 = arglist.add_value("station_ior", any2, org.omg.CORBA.ARG_IN.value);

                    //CREATE RETURN VALUE
                    //Any result = orb.create_any();
                    //result.insert_boolean(false);
                    //NamedValue resultVal = orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);


                    Request req = ref._create_request(null, "add_pay_station", arglist, null);
                    req.invoke();
                } catch (Exception e) {
                    System.out.println("Error resolving name against Naming Service");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }
}