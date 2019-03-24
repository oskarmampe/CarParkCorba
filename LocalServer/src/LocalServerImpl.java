import Server.*;
import com.sun.jmx.snmp.Timestamp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

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
        log.add(new VehicleEvent(timestamp, registration_number, false));
        vehicles_inside.remove(registration_number);
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
