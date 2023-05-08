package edu.gatech.cs6310;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

public class PilotManager {

    // Fields
    private static PilotManager instance;
    private Map<String, Pilot> pilots;
    private Map<Pilot, Drone> pilotDroneAssignments;

    // Helper Methods
    private boolean isLicenseDuplicate(String licenseID) {
        for (Pilot current : pilots.values()) {
            if (current.getLicenseID().equals(licenseID)) {
                return true;
            }
        }
        return false;
    }

    // Public Methods
    public PilotManager() {
        this.pilots = new TreeMap<String, Pilot>();
        this.pilotDroneAssignments = new HashMap<Pilot, Drone>();
    }

    public static PilotManager getInstance() {
        if (instance == null) {
            instance = new PilotManager();
        }
        return instance;
    }

    public boolean pilotPresent(String account) {
        for (String current : pilots.keySet()) {
            if (current.equals(account)) {
                return true;
            }
        }
        
        return false;
    }

    public ErrorCode makePilot(String account, String firstName, String lastName, String phoneNumber, String taxID, String licenseID, int experience) {
        if (pilotPresent(account)) {
            return ErrorCode.PILOT_ID_DUPLICATE;
        }

        if (isLicenseDuplicate(licenseID)) {
            return ErrorCode.PILOT_LICENSE_DUPLICATE;
        }

        pilots.put(account, new Pilot(account, firstName, lastName, phoneNumber, taxID, licenseID, experience));
        return ErrorCode.OK;
    }

    public void displayPilots() {
        for (Map.Entry<String, Pilot> entry : pilots.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public Pilot getPilotByAccount(String account) {
        return pilots.get(account);
    }

    public void assignPilotToDrone(Pilot pilot, Drone drone) {
        if (pilotDroneAssignments.keySet().contains(pilot)) {
            pilotDroneAssignments.get(pilot).unassignPilot();
        }
        drone.assignPilot(pilot);
        pilotDroneAssignments.put(pilot, drone);
    }

    public void completePilotAssignment(Pilot pilot) {
        pilotDroneAssignments.remove(pilot);
        pilot.completeOrder();
    }

    public void cancelPilotAssignment(Pilot pilot) {
        pilotDroneAssignments.remove(pilot);
    }
}
