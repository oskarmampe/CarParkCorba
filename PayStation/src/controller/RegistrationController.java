package controller;

import Server.PayStation;
import Server.PayStationHelper;
import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.PayStationServer;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import static application.App.orb;
import static application.App.payStation;

/**
 *
 * Registration controller responsible for taking any inputs from the 'registration' view.
 *
 * @author Oskar Mampe
 *
 */
public class RegistrationController {

    @FXML
    TextField localServerTxt;

    @FXML
    TextField machineNameTxt;

    /**
     * Click handler for the register button.
     * Takes in the machine name, and local server name, and connects to a local server, whilst initialising the
     * paystation object.
     */
    public void onRegisterDeviceClickButton() {
        try {
            // Get the machine name
            String machine_name = machineNameTxt.getText();

            // Get the rootpoa
            POA rootpoa = POAHelper.narrow(App.orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Get the Name Service
            org.omg.CORBA.Object nameService = null;

            try {
                nameService = App.orb.resolve_initial_references("NameService");
            } catch (InvalidName invN) {
                System.out.println("Couldn't locate a Naming Service");
                System.exit(1);
            }

            // Narrow the naming service so that its methods can be used
            NamingContext nameContext = NamingContextHelper.narrow(nameService);
            NamingContextExt nameServiceExt = NamingContextExtHelper.narrow(nameContext);

            // Create the paystation object.
            payStation = new PayStationServer(machine_name);

            // Narrow the object so that it can be registered as a components that is widely understood by CORBA
            PayStation cref = PayStationHelper.narrow(rootpoa.servant_to_reference(payStation));
            System.out.println(orb.object_to_string(rootpoa.servant_to_reference(payStation)));

            NameComponent[] payStationName = nameServiceExt.to_name(machine_name);
            nameServiceExt.rebind(payStationName, cref);

            // Get the local server
            NameComponent comp = new NameComponent(localServerTxt.getText(),  "");
            NameComponent path[] = {comp};


            App.localServer = nameContext.resolve(path);

            App.payStation.turn_on(machine_name, rootpoa.servant_to_reference(payStation).toString());

            SceneNavigator.loadScene(SceneNavigator.PAY);
        } catch (Exception e) {
            System.out.println("Error resolving name against Naming Service");
            e.printStackTrace();
        }

    }

}
