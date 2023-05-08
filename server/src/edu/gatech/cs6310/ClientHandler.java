package edu.gatech.cs6310;
import java.net.*;
import java.io.*;
import java.util.*;


public class ClientHandler extends Thread {
    
    private Socket socket;
    private Queue<CommandResponse> commands;

    public ClientHandler(Socket socket, Queue<CommandResponse> commands) {
        this.socket = socket;
        this.commands = commands;
    }

    public void shutdown() {
        System.out.println("Client shutting down...");
        try {
            this.socket.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        System.out.println("Client shut down.");
    }

    public void run() {
        try {
            ClientOutputHandler outputHandler = new ClientOutputHandler(this.socket);
            ClientInputHandler inputHandler = new ClientInputHandler(this.socket, this.commands, outputHandler);
            outputHandler.start();
            inputHandler.start();

            outputHandler.join();
            inputHandler.join();
            
        } catch (Exception i) {
            i.printStackTrace();
        }
    }
}