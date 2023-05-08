package edu.gatech.cs6310;
import java.net.*;
import java.io.*;
import java.util.*;


public class ClientInputHandler extends Thread {
    
    private Socket socket;
    private Queue<CommandResponse> commands;
    private ClientOutputHandler outputHandler;

    public ClientInputHandler(Socket socket, Queue<CommandResponse> commands, ClientOutputHandler outputHandler) {
        this.socket = socket;
        this.commands = commands;
        this.outputHandler = outputHandler;
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

    public void run() {
        DataInputStream remoteInput = null;
        try {
            remoteInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            String line = "";

            while (true) {
                try {
                    sleep(10);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }
                line = remoteInput.readUTF();
                CommandResponse command = new CommandResponse(line, this.outputHandler);
                commands.add(command);
            }

        } catch (IOException i) {
            i.printStackTrace();
        }
        
    }
}