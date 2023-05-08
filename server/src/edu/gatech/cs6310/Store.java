package edu.gatech.cs6310;

import java.util.Map;
import java.util.TreeMap;
import java.util.Queue;
import java.util.LinkedList;

public class Store implements Refueler {

    // Fields
    private String name;
    private int revenue;
    private int purchases;
    private int overloads;
    private int transfers;
    private Map<String, Item> items;
    private Map<String, Order> orders;
    private Map<Integer, Drone> drones;
    private Map<String, RefuelingStation> refuelingStations;
    private Point location;
    private Queue<Drone> refuelQueue;
    

    // Helper Methods
    private boolean itemPresent(String name) {
        for (String current : items.keySet()) {
            if (current.equals(name)) {
                return true;
            }
        }

        return false;
    }

    private boolean dronePresent(int id) {
        for (Integer current : drones.keySet()) {
            if (current == id) {
                return true;
            }
        }

        return false;
    }

    private boolean orderPresent(String id) {
        for (String current : orders.keySet()) {
            if (current.equals(id)) {
                return true;
            }
        }

        return false;
    }

    private boolean refuelingStationPresent(String id) {
        return this.refuelingStations.containsKey(id);
    }

    // Public Methods
    public Store(String name, int revenue, int pointX, int pointY) {
        this.name = name;
        this.revenue = revenue;
        this.purchases = 0;
        this.overloads = 0;
        this.transfers = 0;

        this.items = new TreeMap<String, Item>();
        this.orders = new TreeMap<String, Order>();
        this.drones = new TreeMap<Integer, Drone>();
        this.refuelingStations = new TreeMap<>();

        this.location = PointManager.getInstance().makePoint(pointX, pointY, true);
        this.location.setAssociatedStation(this);
        this.refuelQueue = new LinkedList<Drone>();
    }

    public void requestFuel(Drone drone) {
        refuelQueue.add(drone);
        int cost = this.payForFuel(drone.getRefuelAmount());
        StoreManager.getInstance().getStoreForDrone(drone).deductFunds(cost);
    }

    public String toString() {
        return "name:" + this.name + ",revenue:" + this.revenue + ",location:" + this.location;
    }

    public ErrorCode sellItem(String name, int weight) {
        if (itemPresent(name)) {
            return ErrorCode.ITEM_ID_DUPLICATE;
        }

        this.items.put(name, new Item(name, weight));
        return ErrorCode.OK;
    }

    public void displayItems() {
        for (Map.Entry<String, Item> entry : items.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public ErrorCode makeDrone(int id, int weightCapacity, int fuelCapacity, int speed, int refuelThreshold) {
        if (dronePresent(id)) {
            return ErrorCode.DRONE_ID_DUPLICATE;
        }

        this.drones.put(id, new Drone(id, weightCapacity, fuelCapacity, speed, refuelThreshold, this.location, this));
        return ErrorCode.OK;
    }

    public ErrorCode makeDrone(int id, int weightCapacity, int fuelCapacity, int speed, int refuelThreshold, int rechargeRate) {
        if (dronePresent(id)) {
            return ErrorCode.DRONE_ID_DUPLICATE;
        }

        this.drones.put(id, new SolarPoweredDrone(id, weightCapacity, fuelCapacity, speed, refuelThreshold, this.location, rechargeRate, this));
        return ErrorCode.OK;
    }

    public void displayDrones() {
        for (Map.Entry<Integer, Drone> entry : drones.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public ErrorCode flyDrone(int droneID, String pilotAccount) {
        if (!dronePresent(droneID)) {
            return ErrorCode.DRONE_ID_NOT_FOUND;
        }

        PilotManager pilotManager = PilotManager.getInstance();
        if (!pilotManager.pilotPresent(pilotAccount)) {
            return ErrorCode.PILOT_ID_NOT_FOUND;
        }

        pilotManager.assignPilotToDrone(pilotManager.getPilotByAccount(pilotAccount), drones.get(droneID));

        return ErrorCode.OK;
    }

    public ErrorCode startOrder(String orderID, int droneID, String customerAccount) {
        if (orderPresent(orderID)) {
            return ErrorCode.ORDER_ID_DUPLICATE;
        }

        if (!dronePresent(droneID)) {
            return ErrorCode.DRONE_ID_NOT_FOUND;
        }

        CustomerManager customerManager = CustomerManager.getInstance();
        if (!customerManager.customerPresent(customerAccount)) {
            return ErrorCode.CUSTOMER_ID_NOT_FOUND;
        }

        this.orders.put(orderID, new Order(orderID, customerManager.getCustomerByAccount(customerAccount), drones.get(droneID)));

        return ErrorCode.OK;
    }

    public void displayOrders() {
        for (Map.Entry<String, Order> entry : orders.entrySet()) {
            System.out.println(entry.getValue());
        }
    }
    
    public ErrorCode requestItem(String orderID, String itemName, int quantity, int price) {
        if (!orderPresent(orderID)) {
            return ErrorCode.ORDER_ID_NOT_FOUND;
        }

        if (!itemPresent(itemName)) {
            return ErrorCode.ITEM_ID_NOT_FOUND;
        }

        Order order = orders.get(orderID);
        Item item = items.get(itemName);
        if (order.itemPresent(item)) {
            return ErrorCode.ITEM_ALREADY_ORDERED;
        }

        return order.addItem(item, quantity, price);
    }

    public ErrorCode purchaseOrder(String orderID, Timestamp purchaseTime, CommandResponse command) {
        if (!orderPresent(orderID)) {
            return ErrorCode.ORDER_ID_NOT_FOUND;
        }

        // Orders when purchases are no longer immediate delivered. But the money is deducted immediately.

        Order order = orders.get(orderID);
        ErrorCode rc = order.purchase(purchaseTime, command);
        if (rc == ErrorCode.OK) {
            this.revenue += order.calculatePrice();
            this.purchases++;
            this.overloads += order.getDrone().getOverload();
            orders.remove(orderID);
        }

        return rc;
    }

    public void assessLateFee(int fee) {
        this.revenue -= fee;
    }

    public ErrorCode cancelOrder(String orderID) {
        if (!orderPresent(orderID)) {
            return ErrorCode.ORDER_ID_NOT_FOUND;
        }

        Order order = orders.get(orderID);
        ErrorCode rc = order.cancel();
        orders.remove(orderID);

        return rc;
    }

    public ErrorCode transferOrder(String orderID, int newDroneID) {
        if (!orderPresent(orderID)) {
            return ErrorCode.ORDER_ID_NOT_FOUND;
        }

        if (!dronePresent(newDroneID)) {
            return ErrorCode.DRONE_ID_NOT_FOUND;
        }

        ErrorCode rc = orders.get(orderID).transfer(drones.get(newDroneID));

        if (rc == ErrorCode.OK) {
            this.transfers++;
        }

        return rc;
    }

    public ErrorCode makeRefuelingStation(String id, int capacity, int pointX, int pointY) {
        if (refuelingStationPresent(id)) {
            return ErrorCode.REFUELING_STATION_ID_DUPLICATE;
        }

        this.refuelingStations.put(id, new RefuelingStation(id, capacity, pointX, pointY));
        return ErrorCode.OK;
    }

    public ErrorCode adjustRefuelingStation(String id, int pointX, int pointY) {
        if (!refuelingStationPresent(id)) {
            return ErrorCode.REFUELING_STATION_ID_NOT_FOUND;
        }
        this.refuelingStations.get(id).adjustLocation(pointX, pointY);
        return ErrorCode.OK;
    }

    public ErrorCode removeRefuelingStation(String id) {
        if (!refuelingStationPresent(id)) {
            return ErrorCode.REFUELING_STATION_ID_NOT_FOUND;
        }
        this.refuelingStations.remove(id);
        return ErrorCode.OK;
    }

    public void displayRefuelingStations() {
        for (Map.Entry<String, RefuelingStation> entry : refuelingStations.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public void displayEfficiency() {
        System.out.println("name:" + this.name + ",purchases:" + this.purchases + ",overloads:" + this.overloads + ",transfers:" + this.transfers);
    }

    public ErrorCode deliverFuel(int fromDroneID, int toDroneID, CommandResponse command) {
        if (!dronePresent(fromDroneID) || !dronePresent(toDroneID)) {
            return ErrorCode.DRONE_ID_NOT_FOUND;
        }

        if (!drones.get(fromDroneID).deliverFuel(drones.get(toDroneID), command)) {
            return ErrorCode.UNKNOWN_ERROR; // TODO
        }

        return ErrorCode.OK;
    }

    public String getName() {
        return this.name;
    }

    /*
    public RefuelingStation findNearestRefuelingStation(Point curLocation) {
        RefuelingStation result = null;
        int minDistance = Integer.MAX_VALUE;
        for (RefuelingStation station : this.refuelingStations.values()) {
            Point stationLocation = station.getLocation();
            int dist = stationLocation.getDistance(curLocation);
            if (dist < minDistance && station.isAvailable()) {
                minDistance = dist;
                result = station;
            }
        }
        return result;
    }
    */

    public int payForFuel(int amountOfFuel) {
        int cost = amountOfFuel * 10; // Assume 10 is the cost per fuel unit
        return cost;
    }

    public void deductFunds(int amount) {
        this.revenue -= amount;
    }

    public boolean ownsDrone(Drone drone) {
        for (Map.Entry<Integer, Drone> entry : drones.entrySet()) {
            if (entry.getValue() == drone) {
                return true;
            }
        }
        return false;
    }

    public boolean ownsStation(RefuelingStation station) {
        for (Map.Entry<String, RefuelingStation> entry : refuelingStations.entrySet()) {
            if (entry.getValue() == station) {
                return true;
            }
        }
        return false;
    }

    public void update(Timestamp currentTime) {
        while (this.refuelQueue.size() > 0) {
            this.refuelQueue.remove().refuel();
        }
        
        for (Map.Entry<Integer, Drone> entry : drones.entrySet()) {
            entry.getValue().update(currentTime);
        }
    }
}
