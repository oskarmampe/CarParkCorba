import Server.CompanyHQ;
import Server.CompanyHQHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class App {
    public static void main(String[] args) {
        try {
            // Initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Create the CompanyHQ servant object
            CompanyHQImpl companyHQ = new CompanyHQImpl();

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
            orb.run();




        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
