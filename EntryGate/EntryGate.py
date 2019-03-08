from omniORB import CORBA, PortableServer

import CosNaming

import Server__POA


class EntryGate(Server__POA.EntryGate):
    def __init__(self, machine_name, argv):
        self.machine_name = machine_name
        self.argv = argv

    def turn_on(self):
        try:
            # Initialize the ORB
            orb = CORBA.ORB_init(self.argv, CORBA.ORB_ID)

            # Get a reference to the Naming service
            ns = orb.resolve_initial_references("NameService");
            rootContext = ns._narrow(CosNaming.NamingContext)

            if rootContext is None:
                print "Failed to narrow the root naming context"
                sys.exit(1)

            # resolve the Count object in the Naming service
            localServerName = [CosNaming.NameComponent("localServerName", "")]

            # Get the actual EntryGate Object
            localServer = rootContext.resolve(localServerName)
            in_args = [CORBA.TC_string, CORBA.TC_string]
            result = localServer._dynamic_op("add_entry_gate", in_args=in_args, out_args=None)
            result("hello", "from python")

            print("Turned On")
        except Exception as inst:
            print(inst)

    def turn_off(self):
        pass

    def reset(self):
        pass