package SerializedVersion;

import Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class SClient implements Client {
    private Socket mySocket;
    private String clientName;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private SClientReadThread reader;
    private ArrayList<String> otherClients;

    SClient() {
        otherClients = new ArrayList<>();
    }

    public void connect (String ip, Integer port){
        try {
            mySocket = new Socket(ip, port);
            toServer = new ObjectOutputStream(mySocket.getOutputStream());
            fromServer = new ObjectInputStream(mySocket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ExceptionInInitializerError("Can`t connect!");
        }
    }

    public SClient(String ip, Integer port) {
        try {
            mySocket = new Socket(ip, port);
            toServer = new ObjectOutputStream(mySocket.getOutputStream());
            fromServer = new ObjectInputStream(mySocket.getInputStream());
            otherClients = new ArrayList<>();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ExceptionInInitializerError("Can`t connect!");
        }
    }


    public void registration(String name, JTextArea users, JTextArea chat) throws IOException, InterruptedException {
        clientName = name;
        toServer.writeObject(new SMessage("Registration", clientName, clientName));
        reader = new SClientReadThread(users, chat, fromServer, otherClients);
        reader.start();
        synchronized (reader) {
            reader.wait();
            if (!reader.isSuccess()) {
                throw new ExceptionInInitializerError("Registration failed!");
            } else {
                System.out.println("Registration successful!");
            }
        }
        getUsers();
    }

    public void getUsers() {
        try {
            toServer.writeObject(new SMessage("Get user list", "", clientName));
            synchronized (reader) {
                reader.wait();
                if (!reader.isSuccess()) {
                    throw new ExceptionInInitializerError("Can't get user list!");
                }
                otherClients = reader.getOtherClients();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeMessage(String message) {
        synchronized (reader) {
            try {
                toServer.writeObject(new SMessage("Message", message, clientName));
                reader.wait();
            } catch (Exception e) {
                System.exit(0);
            }
            if (!reader.isSuccess()) {
                throw new ExceptionInInitializerError("Message not delivered!");
            }
        }
    }

}
