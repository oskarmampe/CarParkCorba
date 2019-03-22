from omniORB import CORBA, PortableServer

import CosNaming
import time
import Server__POA


class ExitGate(Server__POA.ExitGate):
    def __init__(self, argv, local_server):
        self.machine_name = ""
        self.turned_on = False
        self.argv = argv
        self.local_server = local_server

    def turn_on(self, machine_name, machine_ior):
        print(machine_ior)
        try:
            if not self.turned_on:
                in_args = [CORBA.TC_string, CORBA.TC_string]
                result = self.local_server._dynamic_op(
                    "add_exit_gate", in_args=in_args, out_args=None)
                result(machine_name, machine_ior)
                self.machine_name = machine_name
                self.turned_on = True
                print "Turned On..."
            else:
                print "Device already turned on..."
        except Exception as exc:
            print("Could not turn on.\nError using Corba: {} \
            \nExiting now....").format(exc)
            exit(1)

    def turn_off(self):
        try:
            if self.turned_on:
                in_args = [CORBA.TC_string]
                result = self.local_server._dynamic_op(
                    "remove_device", in_args=in_args, out_args=None)
                result(self.machine_name)
                self.turned_on = False
                print "Turned Off..."
            else:
                print "Device already turned off..."
        except Exception as exc:
            print("Could not turn off.\nError using Corba: {}").format(exc)

    def reset(self, machine_name, machine_ior):
        self.turn_off()
        self.turn_on(machine_name, machine_ior)

    def vehicle_out(self, car_registration):
        try:
            in_args = [CORBA.TC_string, CORBA.TC_long, CORBA.TC_string]
            result = self.local_server. \
                _dynamic_op("vehicle_out", in_args=in_args, out_args=None)
            result(car_registration, int(time.time()), self.machine_name)
        except Exception as exc:
            print("Could not send data.\nError using Corba: {}").format(exc)
