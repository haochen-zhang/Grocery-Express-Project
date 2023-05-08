package edu.gatech.cs6310;

public class SolarPoweredDrone extends Drone{

    private int rechargeRate;
    private boolean isRefueling;

    public SolarPoweredDrone(int id, int weightCapacity, int fuelCapacity, int speed, int refuelThreshold, Point storeLocation, int rechargeRate, Store store) {
        super(id, weightCapacity, fuelCapacity, speed, refuelThreshold, storeLocation, store);
        this.rechargeRate = rechargeRate;
        this.isRefueling = false;
        setType(SOLAR);
    }

    public boolean scheduleOrder(Order order) {
        boolean result = super.scheduleOrder(order);

        // solar drones can always make a delivery if they have a positive recharge rate
        this.isRefueling = canBeRefueled(order.getTargetTime());
        if (!result && this.rechargeRate > 0 && isRefueling) {
            // drones will first return to the store to pickup the order
            super.addDestination(super.getStoreLocation());

            // they will then go to the order location
            super.addDestination(order.getLocation());

            return true;
        }
        return result;
    }

    public void update(Timestamp currentTime) {
        if (currentTime.isDay()) {
            super.refuel(this.rechargeRate);
            // System.out.println("DEBUG: solar drone recharging during day");
        } else {
            // System.out.println("DEBUG: solar drone not recharging during night");
        }
        super.updateMovement(currentTime);
        super.handleDestination(currentTime);
    }

    public boolean canBeRefueled(Timestamp time) {
        return time.isDay();
    }
}
