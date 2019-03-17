import Server.LocalServerPOA;
import Server.VehicleEvent;

public class LocalServerImpl extends LocalServerPOA {
    @Override
    public String location() {
        return null;
    }

    @Override
    public VehicleEvent[] log() {
        return new VehicleEvent[0];
    }

    @Override
    public void vehicle_in(VehicleEvent event) {
        VehicleEvent evt = new VehicleEvent(123, 123, "");
        System.out.println("Vehicle In");
        System.out.println(evt.toString());
    }

    @Override
    public void vehicle_out(VehicleEvent event) {
        System.out.println("Vehicle In");
        System.out.println(event.toString());
    }

    @Override
    public boolean vehicle_in_car_park(String registration_number) {
        return false;
    }

    @Override
    public int return_cash_total() {
        return 0;
    }

    @Override
    public void add_entry_gate(String gate_name, String gate_ior) {
        System.out.println(gate_name + gate_ior);
    }

    @Override
    public void add_exit_gate(String gate_name, String gate_ior) {
        System.out.println(gate_name + gate_ior);
    }

    @Override
    public void add_pay_station(String station_name, String station_ior) {
        System.out.println(station_name + station_ior);
    }
}
