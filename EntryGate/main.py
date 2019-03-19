# Entry Gate entry point
import sys

from omniORB import CORBA, PortableServer

import CosNaming
from EntryGate import EntryGate

# Struct not working
# from omniORB import any
# ve = any.to_any({'date': None, 'time': None, 'registration_number': None})
# ve.value().date = any.to_any(123)
# ve.value().time = any.to_any(123)
# ve.value().registration_number = any.to_any("CORBA.Any(CORBA.TC_string, )")
# print ve.value()
# print ve.value().date
# print ve.value().registration_number
# print ve.value().time
# ve = (654654, 123, 123)
# in_args = [ve.typecode()]
# out_args = [CORBA.tk_struct]
# vehicle_in = localServer._dynamic_op(
#    "vehicle_in", in_args=in_args, out_args=None)
# vehicle_in(ve.value())

'''
Main Entry Point for the Entry gate application.
Requires to run with argument -ORBInitRef NameService={{IOR}}
Initially, the application initialises any orb componenxts.
A while loop is then ran to mimic a GUI application.
'''


def main(argv):
    eg = None
    initial_machine_name = "default"
    try:
        # Initialize the ORB
        orb = CORBA.ORB_init(argv, CORBA.ORB_ID)

        # Get a reference to the Naming service
        ns = orb.resolve_initial_references("NameService")
        rootContext = ns._narrow(CosNaming.NamingContext)

        # If the naming context is not found,
        # then the connection cannot be established
        if rootContext is None:
            print "Failed to narrow the root naming context"
            sys.exit(1)
        # Get the name of the local server to connect to.
        local_server_input = raw_input("\nType the name of the "
                                       "local server to connect to.\n")
        # Connect to the local server
        local_server_name = [CosNaming.NameComponent(local_server_input, "")]
        local_server = rootContext.resolve(local_server_name)

        # Initialise the POA.
        poa = orb.resolve_initial_references("RootPOA")

        # Create an instance of the server and get its reference
        # this will also implicitly activate the object in the POA
        eg = EntryGate(sys.argv, local_server)
        eo = eg._this()

        # Initialise and activate the POA Manager
        poaManager = poa._get_the_POAManager()
        poaManager.activate()

        initial_machine_name = raw_input("\nType the initial device "
                                         "name of the entry gate.\n")
        # bind the Count object in the Naming service
        entry_gate_name = [CosNaming.NameComponent(initial_machine_name, "")]
        rootContext.rebind(entry_gate_name, eo)
    except Exception as exc:
        print "Error initialising Corba: {}\n Exiting now....".format(exc)
        exit()

    eg.turn_on(initial_machine_name, orb.object_to_string(eg._this()))
    user_input = ""
    while user_input.lower() != 'q':

        if user_input.lower() == 'f':
            eg.turn_off()

        if user_input.lower() == 'n':
            new_machine_name = raw_input("\nType in machine name\n")
            eg.turn_on(new_machine_name, orb.object_to_string(eg._this()))

        if user_input.lower() == 'r':
            new_machine_name = raw_input("\nType in machine name\n")
            eg.reset(new_machine_name, orb.object_to_string(eg._this()))

        if user_input.lower() == 've':
            registration_number = raw_input("\nType in the registration number"
                                            "of the vechicle\n")
            eg.vehicle_in(registration_number)

        user_input = raw_input("\nPress f to turn off,"
                               "n to turn on, r to reset, ve to register a"
                               " vehicle event or q to quit\n")


if __name__ == '__main__':
    sys.exit(main(sys.argv))
