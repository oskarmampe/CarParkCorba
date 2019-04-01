package application;

import Server.CompanyHQ;
import Server.CompanyHQHelper;
import controller.MainController;
import controller.SceneNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.CompanyHQImpl;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;

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




    public static void main(String[] args) {
        try {
            // Initialize the ORB
            orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Create the CompanyHQ servant object
            companyHQ = new CompanyHQImpl();

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(companyHQ);
            CompanyHQ cref = CompanyHQHelper.narrow(ref);

            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj =
                    orb.resolve_initial_references ("NameService");
            if (nameServiceObj == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt nameService = NamingContextExtHelper.narrow(nameServiceObj);
            if (nameService == null) {
                System.out.println("nameService = null");
                return;
            }

            // bind the companyHQ object in the Naming service
            String name = "companyHQName";
            NameComponent[] companyHQName = nameService.to_name(name);
            nameService.rebind(companyHQName, cref);

            System.out.println("Listening for input");

            //  wait for invocations from clients
            //orb.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }
}