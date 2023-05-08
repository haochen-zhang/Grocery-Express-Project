import edu.gatech.cs6310.*;

public class Main {
 
    public static void main(String[] args) {
        System.out.println("Welcome to the Grocery Express Delivery Service - Server!");
        DeliveryService simulator = new DeliveryService();
        simulator.commandLoop(1670101149-(60*60*5));
    }
}
