import Server.Device;
import Server.DeviceType;
import Server.LocalServerPOA;
import Server.VehicleEvent;
import com.sun.jmx.snmp.Timestamp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static Server.DeviceType.*;

public class LocalServerImpl extends LocalServerPOA {
    private ArrayList<VehicleEvent> log = new ArrayList<>();
    private ArrayList<String> vehicles_inside = new ArrayList<>();
    private ArrayList<Device> all_devices = new ArrayList<>();
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
        return (String[]) vehicles_inside.toArray();
    }

    @Override
    public VehicleEvent[] log() {
        return (VehicleEvent[]) log.toArray();
    }

    @Override
    public Device[] all_devices() {
        return (Device[]) all_devices.toArray();
    }

    @Override
    public void vehicle_in(String registration_number, int timestamp) {
        log.add(new VehicleEvent(timestamp, registration_number, true));
        vehicles_inside.add(registration_number);
        System.out.println("vehicle in");

    }

    @Override
    public void vehicle_out(String registration_number, int timestamp) {
        log.add(new VehicleEvent(timestamp, registration_number, false));
        vehicles_inside.remove(registration_number);
        System.out.println("vehicle out");
    }


    @Override
    public boolean vehicle_in_car_park(String registration_number) {
        return vehicles_inside.contains(registration_number);
    }

    @Override
    public int return_cash_total() {
        return cash_total;
    }

    @Override
    public void add_entry_gate(String gate_name, String gate_ior) {
        System.out.println("added entry gate");
        all_devices.add(new Device(gate_name, gate_ior, entry_gate));
    }

    @Override
    public void add_exit_gate(String gate_name, String gate_ior) {
        System.out.println("added exit gate");
        all_devices.add(new Device(gate_name, gate_ior, exit_gate));
    }

    @Override
    public void add_pay_station(String station_name, String station_ior) {
        System.out.println("added pay station");
        all_devices.add(new Device(station_name, station_ior, paystation));
    }

    @Override
    public void remove_device(String device_name) {
        for (Device device: all_devices) {
            if (device.device_name.equals(device_name)) {
                System.out.println("removed device");
                all_devices.remove(device);
                return;
            }
        }
    }
}
