import Server.*;
import com.sun.jmx.snmp.Timestamp;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static Server.DeviceType.*;

public class LocalServerImpl extends LocalServerPOA {
    private ArrayList<VehicleEvent> log = new ArrayList<>();
    private ArrayList<String> vehicles_inside = new ArrayList<>();
    private ArrayList<Device> all_devices = new ArrayList<>();
    private ArrayList<PayTicket> all_payments = new ArrayList<>();
    private String location;
    private int cash_total;

    LocalServerImpl(String location) {
        super();
        this.location = location;
        cash_total = 0;
    }

    @Override
    public String location() {
        return location;
    }

    @Override
    public String[] vehicles_inside() {
        return vehicles_inside.toArray(new String[0]);
    }

    @Override
    public VehicleEvent[] log() {
        return log.toArray(new VehicleEvent[0]);
    }

    @Override
    public Device[] all_devices() {
        return all_devices.toArray(new Device[0]);
    }

    @Override
    public PayTicket[] all_payments() {
        return all_payments.toArray(new PayTicket[0]);
    }

    @Override
    public void vehicle_in(String registration_number, int timestamp, String device_name) {
        for (Device device: all_devices) {
            if (device.device_name.equals(device_name)) {
                log.add(new VehicleEvent(timestamp, registration_number, true));
                vehicles_inside.add(registration_number);
            }
        }
        for(VehicleEvent event : log){
            System.out.println("Arriving: " + event.arrival + ", Registration Number: " + event.registration_number + ", Time:" + event.timestamp);
        }
        System.out.println(Arrays.toString(vehicles_inside.toArray()));



    }

    @Override
    public void vehicle_out(String registration_number, int timestamp, String device_name) {
        VehicleEvent out_event = new VehicleEvent(timestamp, registration_number, false);
        log.add(out_event);
        vehicles_inside.remove(registration_number);

        // Check if the time of entering and leaving is within ~5 minutes of each other.
        // Check if the vehicle has been paid for
        // If it has been paid for, check if its not prolonged
        // If it is allow, it.
        // Otherwise send an error to HQ.

        VehicleEvent in_event = new VehicleEvent(-1, "", true);
        PayTicket ticket = new PayTicket("", -1, -1, -1);

        for (VehicleEvent event: log()) {
            if(event.registration_number.equals(registration_number) && event.arrival) {
                in_event = event;
                break;
            }
        }

        for (PayTicket pay: all_payments) {
            if (pay.registration_number.equals(registration_number)){
                ticket = pay;
                break;
            }
        }

        Date in_date = new Date(in_event.timestamp);
        Date out_date = new Date(timestamp);

        System.out.println(out_event.timestamp - in_event.timestamp);

        if (out_event.timestamp - in_event.timestamp <= 5*60) {
            System.out.println("Less than 5 minutes");
        }

        if (ticket.amount == -1 || (out_event.timestamp - in_event.timestamp > 5*60) || (ticket.timestamp - out_event.timestamp < 0)){
            System.out.println("Sending");
            Any any1 = App.orb.create_any();
            Any any2 = App.orb.create_any();
            Any any3 = App.orb.create_any();

            VehicleEventHelper.insert(any1, in_event);
            VehicleEventHelper.insert(any2, out_event);
            PayTicketHelper.insert(any3, ticket);

            NVList arglist = App.orb.create_list(3);


            NamedValue nvArg = arglist.add_value("in_event", any1, org.omg.CORBA.ARG_IN.value);
            NamedValue nvArg2 = arglist.add_value("out_event", any2, org.omg.CORBA.ARG_IN.value);
            NamedValue nvArg3 = arglist.add_value("pay_ticket", any3, org.omg.CORBA.ARG_IN.value);

            //NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);


            Request req = App.companyHQ._create_request(null, "raise_alarm", arglist, null);
            req.invoke();
        }

        //if (System.currentTimeMillis())
        for(VehicleEvent event : log){
            System.out.println("Arriving: " + event.arrival + ", Registration Number: " + event.registration_number + ", Time:" + event.timestamp);
        }
        System.out.println(Arrays.toString(vehicles_inside.toArray()));

    }


    @Override
    public boolean vehicle_in_car_park(String registration_number) {
        System.out.println("checking");
        return vehicles_inside.contains(registration_number);
    }

    @Override
    public int return_cash_total() {
        return cash_total;
    }

    @Override
    public void add_entry_gate(String gate_name, String gate_ior) {
        all_devices.add(new Device(gate_name, gate_ior, entry_gate));
        for(Device device: all_devices){
            System.out.println("Device Name: " + device.device_name + ", Device Type: " + device.type);
        }
    }

    @Override
    public void add_exit_gate(String gate_name, String gate_ior) {
        all_devices.add(new Device(gate_name, gate_ior, exit_gate));
        System.out.println("added exit gate: " + gate_name);
        for(Device device: all_devices){
            System.out.println("Device Name: " + device.device_name + ", Device Type: " + device.type);
        }
    }

    @Override
    public void add_pay_station(String station_name, String station_ior) {
        all_devices.add(new Device(station_name, station_ior, paystation));
        System.out.println("added pay station: " + station_name);
        for(Device device: all_devices){

            System.out.println("Device Name: " + device.device_name + ", Device Type: " + device.type);
            System.out.println("IOR: " + station_ior);
        }
    }

    @Override
    public void remove_device(String device_name) {
        for (Device device: all_devices) {
            if (device.device_name.equals(device_name)) {
                System.out.println("removed device");
                all_devices.remove(device);
                break;
            }
        }
        for(VehicleEvent event : log){
            System.out.println("Arriving: " + event.arrival + ", Registration Number: " + event.registration_number + ", Time:" + event.timestamp);
        }
        System.out.println(Arrays.toString(vehicles_inside.toArray()));
    }

    @Override
    public void add_payment_event(String registration_number, double amount, int parked_for_timestamp, int timestamp) {
        all_payments.add(new PayTicket(registration_number, amount, parked_for_timestamp, timestamp));
        for (PayTicket payTicket: all_payments()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

            Calendar arr = Calendar.getInstance();
            arr.setTimeInMillis((long)payTicket.timestamp*1000);

            Calendar parked = Calendar.getInstance();
            parked.setTimeInMillis((long)payTicket.parked_for_timestamp*1000);
            System.out.println("Arriving: " + sdf.format(arr.getTime()) + ", Registration Number: " + payTicket.registration_number + ", Time Leaving:" + sdf.format(parked.getTime()) + "Amount: " + payTicket.amount);
        }
    }
}
