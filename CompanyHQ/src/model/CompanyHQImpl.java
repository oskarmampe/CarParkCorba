package model;

import Server.*;
import application.App;
import org.omg.CORBA.Any;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanyHQImpl extends CompanyHQPOA {

    ArrayList<Device> all_devices = new ArrayList<>();
    HashMap<Device, Device[]> localServerDevices = new HashMap<>();

    @Override
    public Device[] all_devices() {
        return all_devices.toArray(new Device[0]);
    }

    @Override
    public void raise_alarm(VehicleEvent in_event, VehicleEvent out_event, PayTicket pay_ticket) {
        System.out.println("raised alarm");
    }

    @Override
    public void register_local_server(String server_name, String server_ior) {
        all_devices.add(new Device(server_name, server_ior, DeviceType.local_server));
        System.out.println("Registered a device");
        for (Device device: all_devices) {
            System.out.println("Name: " + device.device_name + ", Type: " + device.type.value() + ", " + device.device_ior);
        }
    }

    @Override
    public Device[] get_local_server_devices(Device local_server) {
        org.omg.CORBA.Object localServer = App.orb.string_to_object(local_server.device_ior);
        Any any1 = App.orb.create_any();
        Any any2 = App.orb.create_any();

        //CREATE RETURN VALUE
        Any result = App.orb.create_any();
        Device[] device = new Device[0];
        devicesHelper.insert(result, device);
        NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);


        Request req = localServer._create_request(null, "_get_all_devices", null, resultVal);
        req.invoke();

        System.out.println("Success");

        for (Device dev: devicesHelper.extract(req.result().value())) {
            System.out.println(dev.device_name);
        }

        return devicesHelper.extract(req.result().value());
    }

    @Override
    public void turn_off_device(String device_ior) {
        org.omg.CORBA.Object device = App.orb.string_to_object(device_ior);
        Request req = device._create_request(null, "turn_off", null, null);
        req.invoke();
    }
}
