from omniORB import CORBA, PortableServer

import CosNaming

import Server__POA


class EntryGate(Server__POA.EntryGate):
    def __init__(self, argv, localServer):
        self.machine_name = ""
        self.argv = argv
        self.localServer = localServer

    def turn_on(self, new_machine_name, machine_ior):
        print(machine_ior)
        try:
            in_args = [CORBA.TC_string, CORBA.TC_string]
            result = self.localServer. \
                _dynamic_op("add_entry_gate", in_args=in_args, out_args=None)
            result(new_machine_name, machine_ior)

            print "Turned On..."
        except Exception as exc:
            print "Could not turn on.\nError using Corba: {} \
            \nExiting now....".format(exc)

    def turn_off(self):
        try:
            print "Turned Off..."
        except Exception as exc:
            print("Could not turn on.\nError using Corba: {}"
                  "\nExiting now....").format(exc)

    def reset(self, new_machine_name, machine_ior):
        self.turn_off()
        self.turn_on(new_machine_name, machine_ior)

