package controller;

import Server.CompanyHQ;
import Server.CompanyHQHelper;
import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.CompanyHQImpl;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import static application.App.companyHQ;

/**
 *
 * Registration controller responsible for taking any inputs from the 'registration' view.
 *
 * @author Oskar Mampe
 *
 */
public class RegistrationController {

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

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(App.orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Create the CompanyHQ servant object
            companyHQ = new CompanyHQImpl();

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(companyHQ);
            CompanyHQ cref = CompanyHQHelper.narrow(ref);

            // Get a reference to the Naming service
            org.omg.CORBA.Object nameServiceObj =
                    App.orb.resolve_initial_references ("NameService");
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
            NameComponent[] companyHQName = nameService.to_name(machine_name);
            nameService.rebind(companyHQName, cref);

            SceneNavigator.loadScene(SceneNavigator.DEVICES);
        } catch (Exception e) {
            System.out.println("Error resolving name against Naming Service");
            e.printStackTrace();
        }

    }

}
