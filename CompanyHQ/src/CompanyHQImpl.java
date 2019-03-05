public class CompanyHQImpl extends CompanyHQPOA {
    @Override
    public void raise_alarm(VehicleEvent event) {
        System.out.println("raised alarm");
    }

    @Override
    public void register_local_server(String server_name, String server_ior) {
        System.out.println("registered local server");
    }
}
