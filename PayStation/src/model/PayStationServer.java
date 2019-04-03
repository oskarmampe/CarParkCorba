package model;

import Server.PayStationPOA;
import application.App;
import controller.SceneNavigator;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;

/**
 *
 * The implemention of the IDL PayStation.
 *
 * @author Oskar Mampe U1564420
 *
 */
public class PayStationServer extends PayStationPOA {

    private String machine_name;
    private double amount;
    private boolean turned_on = false;
    public String ior;

    public PayStationServer(String machine_name) {
        this.machine_name = machine_name;
        amount = 0.0;
    }

    @Override
    public String machine_name() {
        return machine_name;

    }

    @Override
    public boolean turned_on() {
        return turned_on;
    }

    @Override
    public void turn_on(String machine_name, String machine_ior) {
        // Creates a DII request
        try {
            // Create two anys to hold the values
            Any any1 = App.orb.create_any();
            Any any2 = App.orb.create_any();

            // Create the list that represesnts the arguments of the function
            NVList arglist = App.orb.create_list(2);

            // Store the values in any
            any1.insert_string(machine_name);
            any2.insert_string(machine_ior);

            // Create named values, representing any parameters that need to be passed.
            NamedValue nvArg = arglist.add_value("station_name", any1, org.omg.CORBA.ARG_IN.value);
            NamedValue nvArg2 = arglist.add_value("station_ior", any2, org.omg.CORBA.ARG_IN.value);

            // Create the actual request to send
            Request req = App.localServer._create_request(null, "add_pay_station", arglist, null);

            // Invoke the request
            req.invoke();
            turned_on = true;
            this.machine_name = machine_name;
            SceneNavigator.loadScene(SceneNavigator.PAY);
        } catch (Exception e) {
            System.out.println("Error sending the turn on request");
            e.printStackTrace();
        }
    }

    @Override
    public double cash_total() {
        return amount;
    }
    

    @Override
    public void turn_off() {
        try {

            System.out.println("Sending");

            // Create any to hold the value
            Any any1 = App.orb.create_any();

            // Create the list that represesnts the arguments of the function
            NVList arglist = App.orb.create_list(1);

            // Store the values in any
            any1.insert_string(this.machine_name());

            // Create named values, representing any parameters that need to be passed.
            NamedValue nvArg = arglist.add_value("device_name", any1, org.omg.CORBA.ARG_IN.value);

            // Create the actual request to send
            Request req = App.localServer._create_request(null, "remove_device", arglist, null);

            // Invoke the request
            req.invoke();
            turned_on = false;
            SceneNavigator.loadScene(SceneNavigator.REGISTRATION);
        } catch (Exception e) {
            System.out.println("Error sending the turn off request");
            e.printStackTrace();
        }
    }

    @Override
    public void reset(String machine_name, String machine_ior) {
        turn_off();
        turn_on(machine_name, machine_ior);
    }
    
    @Override
    public void pay(String registration_number, double amount, int parked_for_timestamp, int timestamp) {
        System.out.println(App.localServer);
        if(check_car_in_park(registration_number)) {
            try {

                // Create anys to hold the value
                Any any1 = App.orb.create_any();
                Any any2 = App.orb.create_any();
                Any any3 = App.orb.create_any();
                Any any4 = App.orb.create_any();
                Any any5 = App.orb.create_any();

                // Create the list that represesnts the arguments of the function
                NVList arglist = App.orb.create_list(5);

                // Store the values in anys
                any1.insert_string(registration_number);
                any2.insert_double(amount);
                any3.insert_long(parked_for_timestamp);
                any4.insert_long(timestamp);
                any5.insert_string(this.machine_name);

                // Create named values, representing any parameters that need to be passed.
                NamedValue nvArg = arglist.add_value("registration_number", any1, org.omg.CORBA.ARG_IN.value);
                NamedValue nvArg2 = arglist.add_value("amount", any2, org.omg.CORBA.ARG_IN.value);
                NamedValue nvArg3 = arglist.add_value("parked_for_timestamp", any3, org.omg.CORBA.ARG_IN.value);
                NamedValue nvArg4 = arglist.add_value("timestamp", any4, org.omg.CORBA.ARG_IN.value);
                NamedValue nvArg5 = arglist.add_value("device_name", any5, org.omg.CORBA.ARG_IN.value);

                // Create the actual request to send
                Request req = App.localServer._create_request(null, "add_payment_event", arglist, null);

                // Invoke the request
                req.invoke();
                this.amount += amount;
            } catch (Exception e) {
                System.out.println("Error sending the payment event");
                e.printStackTrace();
            }
        } else {
            System.out.println("Vehicle not in car park");
        }
    }

    @Override
    public boolean check_car_in_park(String registration_number) {
        try {

            // Create any to hold the value
            Any any1 = App.orb.create_any();

            // Create the list that represesnts the arguments of the function
            NVList arglist = App.orb.create_list(1);

            // Store the values in anys
            any1.insert_string(registration_number);

            // Create named values, representing any parameters that need to be passed.
            NamedValue nvArg = arglist.add_value("registration_number", any1, org.omg.CORBA.ARG_IN.value);

            //Create any to hold the result value
            Any result = App.orb.create_any();

            // Store the values in anys
            result.insert_boolean(false);

            // Create named values, representing any results that will be returned.
            NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);

            // Create the actual request to send
            Request req = App.localServer._create_request(null, "vehicle_in_car_park", arglist, resultVal);

            // Invoke the request
            req.invoke();

            return req.result().value().extract_boolean(); // return the value that was returned from the call.

        } catch (Exception e) {
            System.out.println("Error sending the checking car in park request");
            e.printStackTrace();
            return false;
        }
    }
}
