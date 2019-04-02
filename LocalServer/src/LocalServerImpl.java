import Server.*;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;

import java.text.SimpleDateFormat;
import java.util.*;

import static Server.DeviceType.*;

/**
 *
 * Implementation of the Local Server IDL
 *
 * @author Oskar Mampe U1564420
 *
 */
public class LocalServerImpl extends LocalServerPOA {

    private ArrayList<VehicleEvent> log = new ArrayList<>();
    private ArrayList<String> vehicles_inside = new ArrayList<>();
    private ArrayList<Device> all_devices = new ArrayList<>();
    private ArrayList<PayTicket> all_payments = new ArrayList<>();
    private String location;
    private double cash_total;

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

        if(!authorised(device_name)) {
            return;
        }

        log.add(new VehicleEvent(timestamp, registration_number, true));
        vehicles_inside.add(registration_number);

        printLog();

    }

    @Override
    public void vehicle_out(String registration_number, int timestamp, String device_name) {

        if(!authorised(device_name)) {
            return;
        }

        // Check if the time of entering and leaving is within ~5 minutes of each other.
        // Check if the vehicle has been paid for
        // If it has been paid for, check if its not prolonged
        // If it is allow, it.
        // Otherwise send an error to HQ.

        // First add the event to the log
        VehicleEvent out_event = new VehicleEvent(timestamp, registration_number, false);
        log.add(out_event);

        vehicles_inside.remove(registration_number); // Remove the vehicle from being inside

        // Create default events
        VehicleEvent in_event = new VehicleEvent(-1, "", true);
        PayTicket ticket = new PayTicket("", -1, -1, -1);

        // Create a reversed list so that the most recent events are first. This is just in case if the customer
        // wants to come back to the car park on the same day
        List<VehicleEvent> reversed_events = Arrays.asList(log());
        Collections.reverse(reversed_events);

        for (VehicleEvent event: reversed_events) { // Check if a pay event exists with the same registration number
            if(event.registration_number.equals(registration_number) && event.arrival) {
                in_event = event;
                break;
            }
        }

        // Create a reversed list so that the most recent events are first. This is just in case if the customer
        // wants to come back to the car park on the same day
        List<PayTicket> reversed_payments = Arrays.asList(all_payments());
        Collections.reverse(reversed_payments);

        for (PayTicket pay: reversed_payments) { // Check if a pay event exists with the same registration number
            if (pay.registration_number.equals(registration_number)){
                ticket = pay;
                break;
            }
        }

        // If the ticket is not found, the user stayed more than 5 minutes or the user overstayed, send an alarm to HQ
        if (ticket.amount == -1 || (out_event.timestamp - in_event.timestamp > 5*60) || (ticket.timestamp - out_event.timestamp < 0)){

            // Create anys to hold the value
            Any any1 = App.orb.create_any();
            Any any2 = App.orb.create_any();
            Any any3 = App.orb.create_any();

            // Create the list that represesnts the arguments of the function
            NVList arglist = App.orb.create_list(3);

            // Store the values in any by using helper methods to put in the correct structs
            VehicleEventHelper.insert(any1, in_event);
            VehicleEventHelper.insert(any2, out_event);
            PayTicketHelper.insert(any3, ticket);

            // Create named values, representing any parameters that need to be passed.
            NamedValue nvArg = arglist.add_value("in_event", any1, org.omg.CORBA.ARG_IN.value);
            NamedValue nvArg2 = arglist.add_value("out_event", any2, org.omg.CORBA.ARG_IN.value);
            NamedValue nvArg3 = arglist.add_value("pay_ticket", any3, org.omg.CORBA.ARG_IN.value);

            // Create the actual request to send
            Request req = App.companyHQ._create_request(null, "raise_alarm", arglist, null);

            // Invoke the request
            req.invoke();
        }

        printLog();

    }


    @Override
    public boolean vehicle_in_car_park(String registration_number) {
        System.out.println("Checking if the car is in the park.");
        return vehicles_inside.contains(registration_number);
    }

    @Override
    public double return_cash_total() {
        System.out.println("Returning the amount earned today");
        return cash_total;
    }

    @Override
    public void add_entry_gate(String gate_name, String gate_ior) {
        all_devices.add(new Device(gate_name, gate_ior, entry_gate));
        System.out.println("added entry gate: " + gate_name);
    }

    @Override
    public void add_exit_gate(String gate_name, String gate_ior) {
        all_devices.add(new Device(gate_name, gate_ior, exit_gate));
        System.out.println("added exit gate: " + gate_name);
    }

    @Override
    public void add_pay_station(String station_name, String station_ior) {
        all_devices.add(new Device(station_name, station_ior, paystation));
        System.out.println("added pay station: " + station_name);
    }

    @Override
    public void remove_device(String device_name) {
        System.out.println("received");
        for (Device device: all_devices) {
            if (device.device_name.equals(device_name)) {
                all_devices.remove(device);
                System.out.println("removed device");
                break;
            }
        }
    }

    @Override
    public void add_payment_event(String registration_number, double amount, int parked_for_timestamp, int timestamp, String device_name) {

        if(!authorised(device_name)) {
            return;
        }

        all_payments.add(new PayTicket(registration_number, amount, parked_for_timestamp, timestamp));

        cash_total += amount;

        // Loop through each pay ticket and print it to the console. Used for logging what happened.
        for (PayTicket payTicket: all_payments()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

            Calendar arr = Calendar.getInstance();
            arr.setTimeInMillis((long)payTicket.timestamp*1000);

            Calendar parked = Calendar.getInstance();
            parked.setTimeInMillis((long)payTicket.parked_for_timestamp*1000);
            System.out.println("Arriving: " + sdf.format(arr.getTime()) + ", Registration Number: " + payTicket.registration_number + ", Time Leaving:" + sdf.format(parked.getTime()) + "Amount: " + payTicket.amount);

        }
    }

    /**
     *
     * Method that checks whether the device is authorised to send the request.
     *
     * @param device_name String name of the device that sent a request
     * @return boolean to match whether a device is in the lits
     */
    private boolean authorised(String device_name) {
        boolean authorised = false;

        for (Device device: all_devices) { // Check if the device is authorised to allow vehicles in
            if (device.device_name.equals(device_name)) {
                authorised = true;
                break;
            }
        }

        return authorised;
    }

    /**
     *
     * Method that prints out some logs to the console. Used for logging purposes.
     *
     */
    private void printLog(){
        for(VehicleEvent event : log){
            System.out.println("Arriving: " + event.arrival + ", Registration Number: " + event.registration_number + ", Time:" + event.timestamp);
        }
        System.out.println(Arrays.toString(vehicles_inside.toArray()));
    }
}
