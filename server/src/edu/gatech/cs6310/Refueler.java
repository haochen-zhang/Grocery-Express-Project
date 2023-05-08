package edu.gatech.cs6310;

public interface Refueler {

    public void requestFuel(Drone drone);
    public void update(Timestamp currentTime);
}