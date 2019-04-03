package model;

import Server.*;
import application.App;
import org.omg.CORBA.Any;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * Implementation of the CompanyHQ IDL
 *
 */
public class CompanyHQImpl extends CompanyHQPOA {

    private ArrayList<AlarmEvent> events = new ArrayList<>();
    private ArrayList<Device> all_devices = new ArrayList<>();
    private HashMap<Device, Device[]> localServerDevices = new HashMap<>();

    /**
     *
     * This is a method that is not in the IDL as it is implementation specific.
     * In this companyHQ, I have decided to use a hashmap to hold all devices related to a server.
     * This could differ from language to language, therefore it is not included in the IDL.
     *
     * @param device {@link Device} local server that requires to see all of its devices
     * @return {@link Device[]} an array of devices linked to a local server
     */
    public Device[] getLocalServerDevices(Device device) {
        if (device.type == DeviceType.local_server) {
            return localServerDevices.get(device);
        }
        return null;
    }

    @Override
    public Device[] all_devices() {
        return all_devices.toArray(new Device[0]);
    }

    @Override
    public AlarmEvent[] events() {
        return events.toArray(new AlarmEvent[0]);
    }

    @Override
    public void raise_alarm(VehicleEvent in_event, VehicleEvent out_event, PayTicket pay_ticket) {
        events.add(new AlarmEvent(in_event, out_event, pay_ticket));
    }

    @Override
    public void register_local_server(String server_name, String server_ior) {
        all_devices.add(new Device(server_name, server_ior, DeviceType.local_server));
    }

    @Override
    public Device[] get_local_server_devices(Device local_server) {
        // Get the CORBA object to create the request to. In this case, a local server stub.
        org.omg.CORBA.Object localServer = App.orb.string_to_object(local_server.device_ior);

        // Create any to hold the value
        Any result = App.orb.create_any();

        // Create an empty device list to insert to any.
        Device[] device = new Device[0];

        // Using the helper, insert the device list into any
        devicesHelper.insert(result, device);

        // Create named values, representing any parameters that need to be passed.
        NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);

        // Create the actual request to send
        Request req = localServer._create_request(null, "_get_all_devices", null, resultVal);

        // Invoke the request
        req.invoke();

        // Extract the resulting Device array from the request
        Device[] value = devicesHelper.extract(req.result().value());

        // Store the device list with the corresponding local server
        localServerDevices.put(local_server, value);

        return value;
    }

    @Override
    public void turn_off_device(String device_ior) {
        // Get the CORBA object to create the request to.
        org.omg.CORBA.Object device = App.orb.string_to_object(device_ior);

        // Create the actual request to send
        Request req = device._create_request(null, "turn_off", null, null);

        // Send one way is different to invoke, as the request is sent, and the process does not wait for any result
        // Essentially, any errors, returning values are ignored, and freeing the thread.
        req.send_oneway();
    }

    @Override
    public double get_local_server_total(String device_ior) {
        // Get the CORBA object to create the request to. In this case, a local server stub.
        org.omg.CORBA.Object device = App.orb.string_to_object(device_ior);

        // Create any to hold the value
        Any result = App.orb.create_any();

        // Store the values in anys
        result.insert_double(0.0);

        // Create named values, representing any results that will be returned.
        NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);

        // Create the actual request to send
        Request req = device._create_request(null, "return_cash_total", null, resultVal);

        // Invoke the request
        req.invoke();

        return req.result().value().extract_double();
    }
}
