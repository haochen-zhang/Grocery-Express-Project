package edu.gatech.cs6310;

public abstract class User {

    // Fields
    private String account;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    // Public Methods
    public User(String account, String firstName, String lastName, String phoneNumber) {
        this.account = account;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return this.firstName + "_" + this.lastName;
    }

    public String toString() {
        return "name:" + this.getName() + ",phone:" + this.phoneNumber;
    }
}
