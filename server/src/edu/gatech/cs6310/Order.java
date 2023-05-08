package edu.gatech.cs6310;

import java.util.List;
import java.util.ArrayList;

public class Order {

    // Fields
    private String id;
    private Customer customer;
    private Drone drone;
    private List<Line> lines;
    private CommandResponse command;
    private Point location;
    private Timestamp targetTime;

    // Methods
    public Order(String id, Customer customer, Drone drone) {
        this.id = id;
        this.customer = customer;
        customer.addOrder(this);
        this.drone = drone;
        drone.assignOrder(this);
        this.lines = new ArrayList<Line>();
        this.command = null;
        this.location = customer.getLocation();
        this.targetTime = null;
    }

    public String getID() {
        return this.id;
    }
    public boolean itemPresent(Item item) {
        for (Line current : lines) {
            if (current.getItem() == item) {
                return true;
            }
        }
        
        return false;
    }

    public ErrorCode addItem(Item item, int quantity, int price) {
        if (!this.customer.canAfford(quantity * price)) {
            return ErrorCode.CUSTOMER_INSUFFICIENT_CREDIT;
        }

        if (!this.drone.canCarry(item.getWeight() * quantity)) {
            return ErrorCode.DRONE_INSUFFICIENT_CAPACITY;
        }

        this.lines.add(new Line(item, quantity, price));
    
        return ErrorCode.OK;
    }

    public ErrorCode purchase(Timestamp targetTime, CommandResponse command) {
        if (!this.drone.hasPilot()) {
            return ErrorCode.DRONE_NEEDS_PILOT;
        }

        // No longer need to check this, fuel is handled at runtime
        //if (!this.drone.hasFuel()) {
        //    return ErrorCode.DRONE_NEEDS_FUEL;
        //}
        setTargetTime(targetTime);

        if (!drone.scheduleOrder(this)) {
            // the route is impossible to create - too far between fuel stations
            return ErrorCode.TRIP_UNROUTABLE;
        }

        if (!customer.completeOrder(this)) {
            return ErrorCode.UNKNOWN_ERROR;
        }

        this.command = command; // we will use this to notify the user when their order is completed.

        return ErrorCode.OK;
    }

    public void complete() {
        this.command.setResponse("Order completed.");
        if (TimeManager.getInstance().getTimestamp().havePassed(this.targetTime)) {
            // late fee
//            System.out.println("Late fee");
            this.drone.getStore().assessLateFee(100); // Assume late fee flat at 100
        } else {
//            System.out.println("No late fee");
        }
    }

    public ErrorCode cancel() {
        if (!customer.cancelOrder(this) || !drone.cancelOrder(this)) {
            return ErrorCode.UNKNOWN_ERROR;
        }
        
        return ErrorCode.OK;
    }
    
    public ErrorCode transfer(Drone newDrone) {
        if (!newDrone.canCarry(this.calculateWeight())) {
            return ErrorCode.NEW_DRONE_INSUFFICIENT_CAPACITY;
        }

        if (newDrone == this.drone) {
            return ErrorCode.DRONE_NO_CHANGE;
        }

        Pilot pilot = drone.getPilot();
        drone.cancelOrder(this);
        PilotManager.getInstance().assignPilotToDrone(pilot, newDrone);

        return ErrorCode.OK;
    }

    public String toString() {
        String returnString = "orderID:" + this.id;
        for (Line current : lines) {
            returnString += "\n" + current;
        }
        return returnString;
    }

    public int calculatePrice() {
        int total = 0;
        for (Line current : lines) {
            total += current.calculatePrice();
        }
        return total;
    }

    public int calculateWeight() {
        int total = 0;
        for (Line current : lines) {
            total += current.calculateWeight();
        }
        return total;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Drone getDrone() {
        return this.drone;
    }

    public Point getLocation() {
        return this.location;
    }

    public void setTargetTime(Timestamp targetTime) {
        this.targetTime = targetTime;
    }

    public Timestamp getTargetTime() {
        return this.targetTime;
    }
}
