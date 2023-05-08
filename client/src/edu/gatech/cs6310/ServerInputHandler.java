package edu.gatech.cs6310;
import java.net.*;
import java.io.*;
import java.util.*;


public class ServerInputHandler extends Thread {
    
    private Socket socket;
    private ExitHandler exitHandler;

    public ServerInputHandler(Socket socket, ExitHandler exitHandler) {
        this.socket = socket;
        this.exitHandler = exitHandler;
    }

    public void run() {
        DataInputStream remoteInput = null;
        try {
            remoteInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String line = "";
            while (!this.exitHandler.getExit()) {
                try {
                    sleep(10);
                } catch (InterruptedException i) {
                    i.printStackTrace();
                }
                line = remoteInput.readUTF();
                System.out.println(line);
            }

            remoteInput.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        
    }
}