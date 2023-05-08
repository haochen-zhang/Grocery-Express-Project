package edu.gatech.cs6310;

public class Line {

    // Fields
    private Item item;
    private int quantity;
    private int price;

    // Methods
    public Line(Item item, int quantity, int price) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }

    public int calculatePrice() {
        return this.quantity * this.price;
    }

    public int calculateWeight() {
        return this.quantity * this.item.getWeight();
    }

    public String toString() {
        return "item_name:" + item.getName() + ",total_quantity:" + this.quantity + ",total_cost:" + this.calculatePrice() + ",total_weight:" + this.calculateWeight();
    }

    public Item getItem() {
        return this.item;
    }
}
