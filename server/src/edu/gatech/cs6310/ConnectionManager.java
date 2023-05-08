package edu.gatech.cs6310;
import java.net.*;
import java.io.*;
import java.util.*;

public class ConnectionManager extends Thread {
    
    private int port;
    private Queue<CommandResponse> commands;
    private List<ClientHandler> handlers;
    private boolean shutdown;
    private ServerSocket serverSocket;
    
    public ConnectionManager(int port, Queue<CommandResponse> commands) {
        this.port = port;
        this.commands = commands;
        this.handlers = new ArrayList<ClientHandler>();
        this.shutdown = false;
        this.serverSocket = null;
    }

    public void shutdown() {
        System.out.println("ConnectionManager shutting down...");
        for (ClientHandler handler : this.handlers) {
            handler.shutdown();
        }
        System.out.println("All handlers shut down...");

        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }

        this.shutdown = true;
        System.out.println("ConnectionManager shut down.");
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            Socket socket = null;

            while (!this.shutdown) {
                try {
                    socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket, this.commands);
                    this.handlers.add(handler);
                    handler.start();
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
        for (ClientHandler handler : this.handlers) {
            try {
                handler.join();
            } catch (InterruptedException i) {
                i.printStackTrace();
            }
        }
    }

}