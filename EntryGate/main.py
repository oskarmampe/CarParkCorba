# Entry Gate entry point
import sys

from omniORB import CORBA, PortableServer, any, tcInternal

import CosNaming

from EntryGate import EntryGate
import Server__POA

def main(argv):
    eg = None
    try:
        # Initialize the ORB
        orb = CORBA.ORB_init(argv, CORBA.ORB_ID)

        # Get a reference to the Naming service
        ns = orb.resolve_initial_references("NameService")
        rootContext = ns._narrow(CosNaming.NamingContext)

        if rootContext is None:
            print "Failed to narrow the root naming context"
            sys.exit(1)

        localServerName = [CosNaming.NameComponent("localServerName", "")]
        localServer = rootContext.resolve(localServerName)

        #vehicleEventName = [CosNaming.NameComponent("vehicleEventName", "")]
        #vehicleEvent = rootContext.resolve(vehicleEventName)

        # Initialise the POA.
        poa = orb.resolve_initial_references("RootPOA")

        # Create an instance of the server and get its reference
        # this will also implicitly activate the object in the POA
        eg = EntryGate(sys.argv, localServer)
        eo = eg._this()

        # Initialise and activate the POA Manager
        poaManager = poa._get_the_POAManager()
        poaManager.activate()

        # bind the Count object in the Naming service
        entryGateName = [CosNaming.NameComponent("entryGateName", "")]
        rootContext.rebind(entryGateName, eo)
    except Exception as exc:
        print "Error initialising Corba: {}\n Exiting now....".format(exc)
        exit()

    eg.turn_on("machine", orb.object_to_string(eg._this()))
    turned_on = True
    user_input = ""
    while user_input.lower() != 'q':

        if user_input.lower() == 'f' and not turned_on:
            print "\nMachine already turned off.\n"

        if user_input.lower() == 'n' and turned_on:
            print "\nMachine already turned on.\n"

        if user_input.lower() == 'r' and not turned_on:
            print "\nMachine must be turned on to reset."

        if user_input.lower() == 'f' and turned_on:
            eg.turn_off()
            turned_on = False

        if user_input.lower() == 'n' and not turned_on:
            new_machine_name = raw_input("Type in machine name\n")
            eg.turn_on(new_machine_name, orb.object_to_string(eg._this()))
            turned_on = True

        if user_input.lower() == 'r' and turned_on:
            new_machine_name = raw_input("Type in machine name\n")
            eg.reset(new_machine_name, orb.object_to_string(eg._this()))

        if user_input.lower() == 've':
            registration_number = raw_input("\nType in the registration number"
                                            "of the vechicle\n")
            ve = any.to_any({'date': None, 'time': None, 'registration_number': None})
            ve.value().date = CORBA.Any(CORBA.TC_long, 123)
            ve.value().time = CORBA.Any(CORBA.TC_long, 123)
            ve.value().registration_number = CORBA.Any(CORBA.TC_string, "123")
            print ve.value()
            print ve.value().date
            print ve.value().registration_number
            print ve.value().time
            # ve = (654654, 123, 123)
            in_args = [ve.typecode()]
            out_args = [CORBA.tk_struct]
            vehicle_in = localServer._dynamic_op("vehicle_out", in_args=in_args, out_args=None)
            vehicle_in(ve.value())

        user_input = raw_input("\nPress f to turn off,"
                               "n to turn on, r to reset, or q to quit\n")

        if user_input.lower() == 'q' and turned_on:
            user_input = ""
            print "\nPlease turn off the machine first."


if __name__ == '__main__':
    sys.exit(main(sys.argv))
