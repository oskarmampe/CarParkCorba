import Server.LocalServer;
import Server.LocalServerHelper;
import Server.VehicleEvent;
import org.omg.CORBA.*;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.util.Scanner;

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

            System.out.println("\nType in the name of the company HQ to connect to");
            String companyHqName = scanner.nextLine();

            NameComponent comp = new NameComponent(companyHqName,  "");
            NameComponent path[] = {comp};
            try {
                App.companyHQ = nameService.resolve(path);
            } catch (Exception e) {
                System.out.println("Error resolving name against Naming Service");
                e.printStackTrace();
            }

            Any any1 = App.orb.create_any();
            Any any2 = App.orb.create_any();

            NVList arglist = App.orb.create_list(2);
            any1.insert_string(localServer.location());
            any2.insert_string(orb.object_to_string(ref));
            NamedValue nvArg = arglist.add_value("server_name", any1, org.omg.CORBA.ARG_IN.value);
            NamedValue nvArg2 = arglist.add_value("server_ior", any2, org.omg.CORBA.ARG_IN.value);

            //CREATE RETURN VALUE
            //Any result = App.orb.create_any();
            //result.insert_boolean(false);
            //NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);


            Request req = App.companyHQ._create_request(null, "register_local_server", arglist, null);
            req.invoke();

            System.out.println("Listening for input");


//            try {
//                org.omg.CORBA.Object server_ref = App.orb.string_to_object("IOR:000000000000001a49444c3a5365727665722f50617953746174696f6e3a312e30000000000000010000000000000082000102000000000a3132372e302e312e3100a3c900000031afabcb0000000020b1c09d5800000001000000000000000100000008526f6f74504f410000000008000000010000000014000000000000020000000100000020000000000001000100000002050100010001002000010109000000010001010000000026000000020002");
//                //CREATE RETURN VALUE
//                Any result = App.orb.create_any();
//                result.insert_string("");
//                NamedValue resultVal = App.orb.create_named_value("machine_name", result, org.omg.CORBA.ARG_OUT.value);
//
//
//                Request req = server_ref._create_request(null, "machine_name", null, resultVal);
//                req.invoke();
//                System.out.println(req.result().value().extract_string());
//            } catch (Exception e){
//                e.printStackTrace();
//            }


            //  wait for invocations from clients
            orb.run();


        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
