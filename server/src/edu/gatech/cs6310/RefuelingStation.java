package edu.gatech.cs6310;

import java.util.Queue;
import java.util.LinkedList;

public class RefuelingStation implements Refueler {

    private String id;
    private Point location;
    private int maxDroneCount;
    private Queue<Drone> droneQueue;

    public RefuelingStation(String id, int capacity, int pointX, int pointY) {
        this.id = id;
        this.maxDroneCount = capacity;
        this.location = PointManager.getInstance().makePoint(pointX, pointY, capacity > 0);
        this.location.setAssociatedStation(this);
        this.droneQueue = new LinkedList<Drone>();
    }

    @Override
    public String toString() {
        return "id:" + this.id + ",location:" + this.location + ",capacity:" + this.maxDroneCount;
    }

    public void adjustLocation(int pointX, int pointY) {
        this.location.adjustLocation(pointX, pointY);
    }

    public void requestFuel(Drone drone) {
        droneQueue.add(drone);
        int cost = StoreManager.getInstance().getStoreForStation(this).payForFuel(drone.getRefuelAmount());
        System.out.println("Refueling cost: "+ cost);
        StoreManager.getInstance().getStoreForDrone(drone).deductFunds(cost);
    }

    public void update(Timestamp currentTime) {
        int i = 0;
        while (i < this.maxDroneCount && this.droneQueue.size() > 0) {
            this.droneQueue.remove().refuel();
            i++;
        }
    }

    /*
    public boolean isAvailable() {
        return this.currentDroneCount < this.maxDroneCount;
    }

    public void increaseCurrentDroneCount() {
        this.currentDroneCount += 1;
    }

    public void decreaseCurrentDroneCount() {
        this.currentDroneCount -= 1;
    }
    */

    public Point getLocation() {
        return this.location;
    }
}
