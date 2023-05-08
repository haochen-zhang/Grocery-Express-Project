package edu.gatech.cs6310;

import java.sql.Time;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;

public class Drone {

    public static String NORMAL = "Normal";
    public static String SOLAR = "SolarPowered";

    // Fields
    private int id;
    private String type;
    private Store store;
    private int currentWeight;
    private int weightCapacity;
    private int currentFuel;
    private int fuelCapacity;
    private Pilot pilot;
    private List<Order> orders;
    private List<Order> scheduledOrders;
    private List<Order> carriedOrders;
    private List<Drone> assignedFuelDeliveries;
    private List<Drone> carriedFuelDeliveries;
    private int speed;
    private int refuelThreshold;
    private Point location;
    private Point storeLocation;
    private Deque<Point> destinations;
    private CommandResponse refuelCommand;
    public boolean waitingForFuel;

    // Helper Methods
    private String toStringNoPilot() {
        return "droneID:" + this.id + ",type:" + this.type + ",total_cap:" + this.weightCapacity + ",num_orders:" + this.orders.size() +
                ",remaining_cap:" + this.getRemainingWeightCapacity() + ",trips_left:" + this.currentFuel +
                ",refuelThreshold:" + this.refuelThreshold + ",speed:" + this.speed;
    }

    private int getRemainingWeightCapacity() {
        int totalOutstanding = 0;
        for (Order current : orders) {
            totalOutstanding += current.calculateWeight();
        }
        return this.weightCapacity - totalOutstanding;
    }

    // Public Methods
    public Drone(int id, int weightCapacity, int fuelCapacity, int speed, int refuelThreshold, Point storeLocation, Store store) {
        this.id = id;
        this.currentWeight = 0;
        this.weightCapacity = weightCapacity;
        this.currentFuel = fuelCapacity;
        this.fuelCapacity = fuelCapacity;
        this.speed = speed;
        this.refuelThreshold = refuelThreshold;
        this.pilot = null;
        this.orders = new ArrayList<Order>();
        this.destinations = new ArrayDeque<Point>();
        this.scheduledOrders = new ArrayList<Order>();
        this.carriedOrders = new ArrayList<Order>();
        this.location = PointManager.getInstance().duplicatePoint(storeLocation);
        this.storeLocation = storeLocation;
        this.assignedFuelDeliveries = new ArrayList<Drone>();
        this.carriedFuelDeliveries = new ArrayList<Drone>();
        this.refuelCommand = null;
        this.waitingForFuel = false;
        this.store = store;
        setType(NORMAL);
    }

    public boolean deliverFuel(Drone otherDrone, CommandResponse command) {
        // calculate if route is possible based on fuel capacity
        // if so, enter in route here with necessary refuelings on the way
        Queue<Point> path = PointManager.getInstance().calculateRouteToPoint(this.storeLocation, otherDrone.location, this.speed, this.fuelCapacity);

        if (path.size() < 1) {
            return false;
        }

        this.assignedFuelDeliveries.add(otherDrone);
        otherDrone.refuelCommand = command;

        for (Point current : path) {
            this.destinations.add(current);
        }

        return true;

    }

    public String toString() {
        String returnString = this.toStringNoPilot();
        if (pilot == null) {
            return returnString;
        }
        return returnString + ",flown_by:" + pilot.getName();
    }

    public boolean canCarry(int weight) {
        return weight <= this.getRemainingWeightCapacity();
    }

    public void assignOrder(Order order) {
        orders.add(order);
    }

    public void assignPilot(Pilot newPilot) {
        this.pilot = newPilot;
    }

    public void unassignPilot() {
        this.pilot = null;
    }

    public boolean hasPilot() {
        return this.pilot != null;
    }

    public boolean hasFuel() {
        return this.currentFuel > 0;
    }

    public int getOverload() {
        return this.orders.size();
    }

    public Pilot getPilot() {
        return this.pilot;
    }

    protected void refuel(int amount) {
        this.currentFuel += amount;
        if (this.currentFuel > this.fuelCapacity) {
            this.currentFuel = this.fuelCapacity;
        }
    }

    public void refuel() {
        this.currentFuel = this.fuelCapacity;
        this.waitingForFuel = false;
        System.out.println("Drone refueled");
    }


    public boolean completeOrder(Order order) {
        this.currentFuel--;
        PilotManager.getInstance().completePilotAssignment(this.pilot);
        return orders.remove(order);
    }

    public boolean cancelOrder(Order order) {
        PilotManager.getInstance().cancelPilotAssignment(this.pilot);
        return orders.remove(order);
    }

    public ErrorCode goToLocation(Point targetLocation) {
        // Check if enough fuel. If yes, change drone's location and update drone's fuel
        int fuelRequired = getFuelRequired(targetLocation);
        if (this.currentFuel < fuelRequired) {
            return ErrorCode.NOT_ENOUGH_FUEL;
        }

        this.location = targetLocation;
        this.currentFuel -= fuelRequired;
        return ErrorCode.OK;
    }

    public int getFuelRequired(Point targetLocation) {
        // Calculate distance then multiply by fuelUseRate to get the fuel required
        //int distance = this.location.getDistance(targetLocation);
        int distance = this.destinations.peekLast().getDistance(targetLocation); // get the distance from the last location
        //int fuelRequired = distance * this.fuelUseRate;
        int fuelRequired = 0;
        int timeUnitsRequired = distance / this.speed;
        if (distance % speed != 0) {
            timeUnitsRequired += 1;
        }

        return fuelRequired;
    }

    protected void addDestination(Point destination) {
        this.destinations.add(destination);
    }

    protected Point getStoreLocation() {
        return this.storeLocation;
    }

    public int getRefuelAmount() {
        return this.fuelCapacity - this.currentFuel;
    }

    public boolean scheduleOrder(Order order) {
        // calculate if route is possible based on fuel capacity
        // if so, enter in route here with necessary refuelings on the way
        Queue<Point> path = PointManager.getInstance().calculateRouteToPoint(this.storeLocation, order.getLocation(), this.speed, this.fuelCapacity);

        if (path.size() < 1) {
            return false;
        }

        this.scheduledOrders.add(order);

        for (Point current : path) {
            this.destinations.add(current);
        }

        return true;
    }

    protected void moveTowardDestination() {
//        System.out.println("DEBUG: moveTowardDestination destinations start");
        if (this.destinations.size() > 0) {
//            System.out.println("DEBUG: we have a destination");
            System.out.println(this.location);
            System.out.println(this.destinations.peekFirst());
        }
        if (this.destinations.size() > 0 && !this.location.equals(this.destinations.peekFirst())) {
//            System.out.println("DEBUG: Moving toward destination");
            this.location.moveTowardPoint(this.destinations.peekFirst(), this.speed);
            this.currentFuel--;
        }
//        System.out.println("DEBUG: moveTowardDestination destinations stop");
    }

    private void pickupOrders() 
    {
        if (this.location.equals(this.storeLocation)) {
            for (Order order : this.scheduledOrders) {
                if (!this.carriedOrders.contains(order)) {
                    this.carriedOrders.add(order);
//                    System.out.println("DEBUG: picked up order");
                }
            }
            for (Drone drone : this.assignedFuelDeliveries) {
                this.carriedFuelDeliveries.add(drone);
            }
        }
    }

    private void completeDeliveries() {
        List<Order> ordersToRemove = new ArrayList<Order>();
        for (Order order : this.carriedOrders) {
            if (order.getLocation().equals(this.location)) {
                order.complete();
                ordersToRemove.add(order);
            }
        }

        this.orders.removeAll(ordersToRemove);
        this.scheduledOrders.removeAll(ordersToRemove);
        this.carriedOrders.removeAll(ordersToRemove);
    }

    private void completeFuelDeliveries() {
        List<Drone> dronesToRemove = new ArrayList<Drone>();
        for (Drone drone : this.carriedFuelDeliveries) {
            if (drone.location.equals(this.location)) {
                drone.refuel(drone.fuelCapacity);
                drone.refuelCommand.setResponse("Drone fuel delivery complete.");
                dronesToRemove.add(drone);
            }
        }

        this.assignedFuelDeliveries.removeAll(dronesToRemove);
        this.carriedFuelDeliveries.removeAll(dronesToRemove);
    }

    protected void updateMovement(Timestamp currentTime) {
        // 1) figure out where the drone is going and then get it going there
        // this could be nowhere, to a fuel station if it is under the fuel threshold, or to another destination
        
        // if the drone is out of fuel, we can't move anywhere...
        // also don't move if waiting for fuel
        if (this.currentFuel > 0 && !this.waitingForFuel) {
            // if the destination is a fuel station, proceed.
            if (this.destinations.peekFirst() != null && PointManager.getInstance().isRefuelingStationAtPoint(this.destinations.peekFirst())) {
                this.moveTowardDestination();
            }
            // if the destination is not a refuel station and we are at or below the fuel threshold,
            // find the nearest refuel station and make that the new destination
            // OR
            // if there is a destination and we have fuel to proceed, figure out how much more we need to get there
            // if it is insufficient, route us to a refuel station first
            else if ((this.currentFuel <= this.refuelThreshold) || ((this.destinations.peekFirst() != null) && (this.getFuelRequired(this.destinations.peekFirst()) > this.currentFuel))) {
//                System.out.println("Looking for a refueling station");
                Point closestStation = PointManager.getInstance().getClosestRefuelingStation(this.location);
                if (closestStation == null) {
                    System.out.println("Couldn't find any refueling stations");
                    // TODO - there are no refueling stations
                    // would only be possible if all the stores and stations were deleted after being created...
                    // there are more problems for the companies if that happens...
                }   
//                System.out.println("Found a refueling station");
                System.out.println(closestStation);
                this.destinations.addFirst(closestStation);
                this.moveTowardDestination();
            }
            // if there is a destination and we have fuel to make it all the way, just proceed.
            else if (this.destinations.size() > 0) {
                this.moveTowardDestination();
            }
        }
    }

    protected void handleDestination(Timestamp currentTime) {
        // 2) now that the drone has moved (if necessary / possible), we need to check if it has arrived at its destination
        if (this.destinations.size() > 0 && this.destinations.peekFirst().equals(this.location)) {
//            System.out.println("DEBUG: We've arrived at a destination");
            // we've arrived, if the destination is a fueling station we can refuel
            Point destination = this.destinations.removeFirst();
            System.out.println(destination);
            if (PointManager.getInstance().isRefuelingStationAtPoint(destination) && this.currentFuel < this.fuelCapacity) {
                Refueler refueler = PointManager.getInstance().getRefuelingStationAtPoint(destination);
                refueler.requestFuel(this);
                this.waitingForFuel = true;
//                System.out.println("DEBUG: Drone has arrived at refueling station");
                refuel();
                TimeManager.getInstance().incrementTimeByMinutes(2);
//                System.out.println("DEBUG: Drone has leaved refueling station");
            }

            // if any of our Orders are at this location, complete delivery
            this.completeDeliveries();

            // Pickup orders if applicable
            this.pickupOrders();

            // Deliver fuel to any drones if applicable
            this.completeFuelDeliveries();
        }


    }

    public void update(Timestamp currentTime) {
        this.updateMovement(currentTime);
        this.handleDestination(currentTime);
    }

    public Store getStore() {
        return this.store;
    }

    public void setType(String type) {
        this.type = type;
    }
}
