package XMLVersion;

import Client;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class XMLClient implements Client {

    private Socket mySocket;
    private String clientName;
    private XMLClientReadThread reader;
    private XMLWriter toServer;
    private XMLReader fromServer;
    private HashMap<String, String> otherClients;
    private String USID;

    XMLClient()  {
        otherClients = new HashMap<>();
    }

    public void connect (String ip, Integer port){
        try {
            mySocket = new Socket(ip, port);
            toServer = new XMLWriter(mySocket.getOutputStream());
            fromServer = new XMLReader(mySocket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ExceptionInInitializerError("Can`t connect!");
        }
    }

    @Override
    public void registration(String name, JTextArea users, JTextArea chat) throws InterruptedException {
        clientName = name;
        try {
            toServer.sendCommandMessage("login", name, "Client1.0", "", "");
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            e.printStackTrace();
        }
        reader = new XMLClientReadThread(users, chat, fromServer, otherClients);
        reader.start();
        synchronized (reader) {
            reader.wait();
            if (!reader.isSuccess()) {
                throw new ExceptionInInitializerError("Registration failed!");
            } else {
                USID = reader.getReader().getUSID();
            }
        }
        System.out.println("Registration successful!");
        getUsers();
    }

    @Override
    public void getUsers() {
        try {
            toServer.sendCommandMessage("list", "", "", USID, "");
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

    @Override
    public void writeMessage(String message) {
        synchronized (reader) {
            try {
                toServer.sendCommandMessage("message", "", "", USID, message);
                reader.wait();
            } catch (Exception e) {
                System.exit(0);
            }
            if (!reader.isSuccess()) {
                throw new ExceptionInInitializerError(reader.getReader().getMessage());
            }
        }
    }
}
