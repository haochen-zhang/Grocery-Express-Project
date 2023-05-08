package edu.gatech.cs6310;

import java.util.Map;
import java.util.TreeMap;

public class CustomerManager {

    // Fields
    private static CustomerManager instance;
    private Map<String, Customer> customers;

    // Public Methods
    public CustomerManager() {
        this.customers = new TreeMap<String, Customer>();
    }

    public static CustomerManager getInstance() {
        if (instance == null) {
            instance = new CustomerManager();
        }
        return instance;
    }

    public boolean customerPresent(String account) {
        for (String current : customers.keySet()) {
            if (current.equals(account)) {
                return true;
            }
        }
        
        return false;
    }

    public ErrorCode makeCustomer(String account, String firstName, String lastName, String phoneNumber, int rating, int credits, int pointX, int pointY) {
        if (customerPresent(account)) {
            return ErrorCode.CUSTOMER_ID_DUPLICATE;
        }

        customers.put(account, new Customer(account, firstName, lastName, phoneNumber, rating, credits, pointX, pointY));
        return ErrorCode.OK;
    }

    public void displayCustomers() {
        for (Map.Entry<String, Customer> entry : customers.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public Customer getCustomerByAccount(String account) {
        return customers.get(account);
    }
}
