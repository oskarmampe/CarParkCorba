import Server.LocalServer;
import Server.LocalServerHelper;
import Server.VehicleEvent;
import org.omg.CORBA.*;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class App {

    public static ORB orb = null;

    public static void main(String[] args) {
        try {
            // Initialize the ORB
            orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Create the localServer servant object
            LocalServerImpl localServer = new LocalServerImpl("Huddersfield");

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
            String name = "localServerName";
            NameComponent[] localServerName = nameService.to_name(name);
            nameService.rebind(localServerName, cref);
            System.out.println(orb.object_to_string(ref));

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
