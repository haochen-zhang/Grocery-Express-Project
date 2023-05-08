import edu.gatech.cs6310.*;

import java.net.*;
import java.io.*;


public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to the Grocery Express Delivery Service - Client!");
        
        Socket socket = null;

        ExitHandler exitHandler = new ExitHandler();
        UserInputHandler userHandler = null;
        ServerInputHandler serverHandler = null;

        String ip = "127.0.0.1";
        if (args.length < 1) {
            System.out.println("assuming IP is 127.0.0.1");
        } else {
            ip = args[0];
        }
        int port = 6310;
        
        try {
            socket = new Socket(ip, port);
            System.out.println("Connected to " + ip + ", port " + Integer.toString(port));
            userHandler = new UserInputHandler(socket, exitHandler);
            serverHandler = new ServerInputHandler(socket, exitHandler);
        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        }

        userHandler.start();
        serverHandler.start();

        while (!exitHandler.getExit()) {
            try {
                java.lang.Thread.sleep(100);
            } catch (InterruptedException i) {
                i.printStackTrace();
            }
        }

        try {
            System.out.println("Client closing gracefully");
            socket.close();
            userHandler.join();
            serverHandler.join();
        } catch (Exception i) {
            i.printStackTrace();
        }

        System.out.println("Client closed");
    }
}
