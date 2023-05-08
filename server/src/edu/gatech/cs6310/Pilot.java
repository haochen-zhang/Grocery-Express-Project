package edu.gatech.cs6310;

public class Pilot extends User {

    // Fields
    private String taxID;
    private String licenseID;
    private int experience;

    // Public Methods
    public Pilot(String account, String firstName, String lastName, String phoneNumber, String taxID, String licenseID, int experience) {
        super(account, firstName, lastName, phoneNumber);
        this.taxID = taxID;
        this.licenseID = licenseID;
        this.experience = experience;
    }

    public void completeOrder() {
        this.experience++;
    }

    public String toString() {
        return super.toString() + ",taxID:" + this.taxID + ",licenseID:" + this.licenseID + ",experience:" + this.experience;
    }

    public String getLicenseID() {
        return this.licenseID;
    }
}
