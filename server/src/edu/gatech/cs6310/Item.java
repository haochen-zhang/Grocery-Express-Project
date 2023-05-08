package edu.gatech.cs6310;

public class Item {

    // Fields
    private String name;
    private int weight;

    // Public Methods
    public Item(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String toString() {
        return this.name + "," + this.weight;
    }

    public String getName() {
        return this.name;
    }

    public int getWeight() {
        return this.weight;
    }
}
