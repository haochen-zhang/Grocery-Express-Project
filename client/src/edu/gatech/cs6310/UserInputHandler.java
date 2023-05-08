package edu.gatech.cs6310;
import java.net.*;
import java.io.*;
import java.util.*;


public class UserInputHandler extends Thread {
    
    private Socket socket;
    private ExitHandler exitHandler;

    public UserInputHandler(Socket socket, ExitHandler exitHandler) {
        this.socket = socket;
        this.exitHandler = exitHandler;
    }

    public void run() {
        DataInputStream localInput = null;
        DataOutputStream remoteOutput = null;
        try {
            localInput = new DataInputStream(System.in);
            remoteOutput = remoteOutput = new DataOutputStream(socket.getOutputStream());

            String line = "";
            while (!line.equals("stop") && !line.startsWith("stop")) {
                line = localInput.readLine();
                if (line.startsWith("stop") && line.split(",").length == 2) {
                    try {
                        sleep(Integer.parseInt(line.split(",")[1]) * 1000);
                    } catch (InterruptedException i) {
                        
                    }
                }
                remoteOutput.writeUTF(line);
                System.out.println("> " + line);
                if (line.equals("stop") || line.startsWith("stop")) {
                    exitHandler.setExit();
                }
            }

            localInput.close();
            remoteOutput.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        
    }
}