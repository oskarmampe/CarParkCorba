package model;

import Server.PayStationPOA;

public class PayStationServer extends PayStationPOA {
    @Override
    public String machine_name() {
        return null;
    }

    @Override
    public double cash_total() {
        return 0;
    }

    @Override
    public void turn_on(String machine_name) {

    }

    @Override
    public void turn_off() {

    }

    @Override
    public void reset(String machine_name) {

    }

    @Override
    public void pay(String registration_number, double amount, int parked_for) {

    }
}
