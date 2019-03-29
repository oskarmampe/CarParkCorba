package model;

import Server.CompanyHQPOA;
import Server.LocalServer;
import Server.VehicleEvent;

import java.util.ArrayList;

public class CompanyHQImpl extends CompanyHQPOA {

    ArrayList<LocalServer> local_servers = new ArrayList<>();

    @Override
    public LocalServer[] local_servers() {
        return new LocalServer[0];
    }

    @Override
    public void raise_alarm(VehicleEvent event) {
        System.out.println("raised alarm");
    }

    @Override
    public void register_local_server(String server_name, String server_ior) {
        local_servers.add(new LocalServer(server_name, server_ior));
    }
}
