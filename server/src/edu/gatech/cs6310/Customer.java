package edu.gatech.cs6310;

import java.util.List;
import java.util.ArrayList;

public class Customer extends User {

    // Fields
    private int rating;
    private int credits;
    private List<Order> orders;
    private Point location;

    // Public Methods
    public Customer(String account, String firstName, String lastName, String phoneNumber, int rating, int credits, int pointX, int pointY) {
        super(account, firstName, lastName, phoneNumber);
        this.rating = rating;
        this.credits = credits;
        this.orders = new ArrayList<Order>();
        this.location = PointManager.getInstance().makePoint(pointX, pointY);
    }

    public boolean canAfford(int price) {
        int totalOutstanding = 0;
        for (Order current : orders) {
            totalOutstanding += current.calculatePrice();
        }
        return price <= (this.credits - totalOutstanding);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public boolean completeOrder(Order order) {
        this.credits -= order.calculatePrice();
        return orders.remove(order);
    }

    public boolean cancelOrder(Order order) {
        return orders.remove(order);
    }

    public Point getLocation() {
        return this.location;
    }

    public String toString() {
        return super.toString() + ",rating:" + this.rating + ",credit:" + this.credits + ",location" + this.location;
    }
}
