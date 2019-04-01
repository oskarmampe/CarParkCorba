package model;

import Server.PayStationPOA;
import application.App;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;

public class PayStationServer extends PayStationPOA {

    String machine_name;
    double amount;
    boolean turned_on = false;

    public PayStationServer(String machine_name) {
        this.machine_name = machine_name;
        amount = 0.0;
    }


    @Override
    public String machine_name() {
        try {
            System.out.println("hello");
            return "hello";
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public boolean turned_on() {
        return turned_on;
    }

    @Override
    public void turn_on(String machine_name, String machine_ior) {
        try {
            System.out.println("App.localServer = " + App.localServer);

            Any any1 = App.orb.create_any();
            Any any2 = App.orb.create_any();

            NVList arglist = App.orb.create_list(2);
            any1.insert_string(machine_name);
            any2.insert_string(machine_ior);
            NamedValue nvArg = arglist.add_value("station_name", any1, org.omg.CORBA.ARG_IN.value);
            NamedValue nvArg2 = arglist.add_value("station_ior", any2, org.omg.CORBA.ARG_IN.value);

            //CREATE RETURN VALUE
            //Any result = App.orb.create_any();
            //result.insert_boolean(false);
            //NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);


            Request req = App.localServer._create_request(null, "add_pay_station", arglist, null);
            req.invoke();
            turned_on = true;
            this.machine_name = machine_name;
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
            System.out.println("App.localServer = " + App.localServer);

            Any any1 = App.orb.create_any();

            NVList arglist = App.orb.create_list(4);
            any1.insert_string(this.machine_name());
            NamedValue nvArg = arglist.add_value("device_name", any1, org.omg.CORBA.ARG_IN.value);

            //CREATE RETURN VALUE
            //Any result = App.orb.create_any();
            //result.insert_boolean(false);
            //NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);


            Request req = App.localServer._create_request(null, "remove_device", arglist, null);
            req.invoke();
            turned_on = true;
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
                Any any1 = App.orb.create_any();
                Any any2 = App.orb.create_any();
                Any any3 = App.orb.create_any();
                Any any4 = App.orb.create_any();


                NVList arglist = App.orb.create_list(2);
                any1.insert_string(registration_number);
                any2.insert_double(amount);
                any3.insert_long(parked_for_timestamp);
                any4.insert_long(timestamp);
                NamedValue nvArg = arglist.add_value("registration_number", any1, org.omg.CORBA.ARG_IN.value);
                NamedValue nvArg2 = arglist.add_value("amount", any2, org.omg.CORBA.ARG_IN.value);
                NamedValue nvArg3 = arglist.add_value("parked_for_timestamp", any3, org.omg.CORBA.ARG_IN.value);
                NamedValue nvArg4 = arglist.add_value("timestamp", any4, org.omg.CORBA.ARG_IN.value);

                //CREATE RETURN VALUE
                //Any result = App.orb.create_any();
                //result.insert_boolean(false);
                //NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);


                Request req = App.localServer._create_request(null, "add_payment_event", arglist, null);
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
            Any any1 = App.orb.create_any();


            NVList arglist = App.orb.create_list(2);
            any1.insert_string(registration_number);
            NamedValue nvArg = arglist.add_value("registration_number", any1, org.omg.CORBA.ARG_IN.value);

            //CREATE RETURN VALUE
            Any result = App.orb.create_any();
            result.insert_boolean(false);
            NamedValue resultVal = App.orb.create_named_value("result", result, org.omg.CORBA.ARG_OUT.value);


            Request req = App.localServer._create_request(null, "vehicle_in_car_park", arglist, resultVal);
            req.invoke();

            return req.result().value().extract_boolean();

        } catch (Exception e) {
            System.out.println("Error sending the checking car in park request");
            e.printStackTrace();
            return false;
        }
    }
}
