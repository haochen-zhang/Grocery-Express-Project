package edu.gatech.cs6310;

import java.util.Map;
import java.util.TreeMap;

public class StoreManager { 

    // Fields
    private static StoreManager instance;
    private Map<String, Store> stores;

    // Public Methods
    public StoreManager() {
        this.stores = new TreeMap<String, Store>();
    }

    public static StoreManager getInstance() {
        if (instance == null) {
            instance = new StoreManager();
        }
        return instance;
    }

    public boolean storePresent(String name) {
        for (String current : stores.keySet()) {
            if (current.equals(name)) {
                return true;
            }
        }
        
        return false;
    }

    public Store getStoreByName(String name) {
        if (stores.containsKey(name)) {
            return stores.get(name);
        }
        return null; // should be handled properly
    }

    public ErrorCode makeStore(String name, int revenue, int pointX, int pointY) {
        if (storePresent(name)) {
            return ErrorCode.STORE_ID_DUPLICATE;
        }

        stores.put(name, new Store(name, revenue, pointX, pointY));
        return ErrorCode.OK;
    }

    public void displayStores() {
        for (Map.Entry<String, Store> entry : stores.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public ErrorCode sellItem(String storeName, String itemName, int itemWeight) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).sellItem(itemName, itemWeight);
    }

    public ErrorCode displayItems(String name) {
        if (!storePresent(name)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        stores.get(name).displayItems();
        return ErrorCode.OK;
    }

    public ErrorCode makeDrone(String storeName, int id, int weightCapacity, int fuelCapacity, int speed, int refuelThreshold) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).makeDrone(id, weightCapacity, fuelCapacity, speed, refuelThreshold);
    }

    public ErrorCode makeDrone(String storeName, int id, int weightCapacity, int fuelCapacity, int speed, int refuelThreshold, int rechargeRate) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).makeDrone(id, weightCapacity, fuelCapacity, speed, refuelThreshold, rechargeRate);
    }

    public ErrorCode displayDrones(String storeName) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        stores.get(storeName).displayDrones();
        return ErrorCode.OK;
    }

    public ErrorCode flyDrone(String storeName, int droneID, String pilotAccount) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).flyDrone(droneID, pilotAccount);
    }

    public ErrorCode makeRefuelingStation(String storeName, String id, int capacity, int pointX, int pointY) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).makeRefuelingStation(id, capacity, pointX, pointY);
    }

    public ErrorCode adjustRefuelingStation(String storeName, String id, int pointX, int pointY) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }
        return stores.get(storeName).adjustRefuelingStation(id, pointX, pointY);
    }

    public ErrorCode removeRefuelingStation(String storeName, String id) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }
        return stores.get(storeName).removeRefuelingStation(id);
    }

    public ErrorCode displayRefuelingStations(String storeName) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        stores.get(storeName).displayRefuelingStations();
        return ErrorCode.OK;
    }

    public ErrorCode startOrder(String storeName, String orderID, int droneID, String customerAccount) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).startOrder(orderID, droneID, customerAccount);
    }

    public ErrorCode displayOrders(String storeName) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        stores.get(storeName).displayOrders();
        return ErrorCode.OK;
    }

    public ErrorCode requestItem(String storeName, String orderID, String itemName, int quantity, int price) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).requestItem(orderID, itemName, quantity, price);
    }

    public ErrorCode purchaseOrder(String storeName, String orderID, Timestamp purchaseTime, CommandResponse command) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).purchaseOrder(orderID, purchaseTime, command);
    }

    public ErrorCode cancelOrder(String storeName, String orderID) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).cancelOrder(orderID);
    }

    public ErrorCode transferOrder(String storeName, String orderID, int newDroneID) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).transferOrder(orderID, newDroneID);
    }

    public void displayEfficiency() {
        for (Store current : stores.values()) {
            current.displayEfficiency();
        }
    }

    public ErrorCode deliverFuel(String storeName, int fromDroneID, int toDroneID, CommandResponse command) {
        if (!storePresent(storeName)) {
            return ErrorCode.STORE_ID_NOT_FOUND;
        }

        return stores.get(storeName).deliverFuel(fromDroneID, toDroneID, command);
    }

    public Store getStoreForDrone(Drone drone) {
        for (Map.Entry<String, Store> entry : stores.entrySet()) {
            if (entry.getValue().ownsDrone(drone)) {
                System.out.println(entry);
                return entry.getValue();
            }
        }

        // uh... shouldn't be possible to reach
        return null;
    }

    public Store getStoreForStation(RefuelingStation station) {
        for (Map.Entry<String, Store> entry : stores.entrySet()) {
            if (entry.getValue().ownsStation(station)) {
                return entry.getValue();
            }
        }

        // uh... shouldn't be possible to reach
        return null;
    }

    public void update(Timestamp currentTime) {
        for (Store current : stores.values()) {
            current.update(currentTime);
        }
    }
}
