package edu.gatech.cs6310;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class ClientOutputHandler extends Thread {
    
    private Socket socket;
    private Queue<String> toSend;

    public ClientOutputHandler(Socket socket) {
        this.socket = socket;
        this.toSend = new ConcurrentLinkedQueue<String>();
    }

    /*
    public void shutdown() {
        System.out.println("Client shutting down...");
        try {
            this.socket.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        System.out.println("Client shut down.");
    }
    */

    public void send(String text) {
        this.toSend.add(text);
    }

    public void run() {
        DataOutputStream remoteOutput = null;
        try {
            remoteOutput = new DataOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    sleep(10);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }
                if (!this.toSend.isEmpty()) {
                    remoteOutput.writeUTF(this.toSend.remove());
                }
            }

        } catch (IOException i) {
            i.printStackTrace();
        }
        
    }
}