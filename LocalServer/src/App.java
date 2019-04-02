import Server.LocalServer;
import Server.LocalServerHelper;
import org.omg.CORBA.*;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.util.Scanner;

/**
 *
 * Main entry point of the application.
 *
 * @author Oskar Mampe U1564420
 *
 */
public class App {

    public static ORB orb = null;
    public static org.omg.CORBA.Object companyHQ = null;

    public static void main(String[] args) {
        try {
            // Initialize the ORB
            orb = ORB.init(args, null);

            //Initialise Scanner for input
            Scanner scanner = new Scanner(System.in);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Create the localServer servant object
            System.out.println("Type in the location of the Local Server");
            String name = scanner.nextLine();
            LocalServerImpl localServer = new LocalServerImpl(name);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(localServer);
            LocalServer cref = LocalServerHelper.narrow(ref);

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

            // bind the localServer object in the Naming service
            NameComponent[] localServerName = nameService.to_name(name);
            nameService.rebind(localServerName, cref);
            System.out.println(orb.object_to_string(ref));

            // Get the company hq name to connect to
            System.out.println("\nType in the name of the company HQ to connect to");
            String companyHqName = scanner.nextLine();

            // Connect to the company hq
            NameComponent comp = new NameComponent(companyHqName,  "");
            NameComponent path[] = {comp};
            try {
                App.companyHQ = nameService.resolve(path);
            } catch (Exception e) {
                System.out.println("Error resolving name against Naming Service");
                e.printStackTrace();
            }

            // Create anys to hold the value
            Any any1 = App.orb.create_any();
            Any any2 = App.orb.create_any();

            // Create the list that represesnts the arguments of the function
            NVList arglist = App.orb.create_list(2);

            // Store the values in any
            any1.insert_string(localServer.location());
            any2.insert_string(orb.object_to_string(ref));

            // Create named values, representing any parameters that need to be passed.
            NamedValue nvArg = arglist.add_value("server_name", any1, org.omg.CORBA.ARG_IN.value);
            NamedValue nvArg2 = arglist.add_value("server_ior", any2, org.omg.CORBA.ARG_IN.value);

            // Create the actual request to send
            Request req = App.companyHQ._create_request(null, "register_local_server", arglist, null);

            // Invoke the request
            req.invoke();

            System.out.println("Listening for input");

            //  wait for invocations from clients
            orb.run();


        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
