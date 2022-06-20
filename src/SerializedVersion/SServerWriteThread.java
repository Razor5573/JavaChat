package SerializedVersion;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class SServerWriteThread extends Thread {

    private String name;
    private ArrayList<ObjectOutputStream> objectOutputStreams;
    private ArrayList<String> clients;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private ArrayDeque<SMessage> lastMessages;
    private Integer maxLastMessages;

    SServerWriteThread(Socket socket, ArrayDeque<SMessage> lastMessages, ObjectOutputStream toClient,
                       ArrayList<ObjectOutputStream> oos, ArrayList<String> clients) throws IOException {
        this.name = "New User";
        this.objectOutputStreams = oos;
        this.lastMessages = lastMessages;
        this.toClient = toClient;
        this.clients = clients;
        fromClient = new ObjectInputStream(socket.getInputStream());
        maxLastMessages = 10;
    }

    @Override
    public void run() {
        SMessage message;
        while (!this.isInterrupted()) {
            try {
                message = (SMessage) fromClient.readObject();

                switch (message.getType()) {
                    case "Message":
                        System.out.println(message.getDate() + " " + message.getSource() + ": " + message.getMessage());
                        toClient.writeObject(new SMessage("Success", "Message delivered"));
                        break;
                    case "Registration":
                        System.out.println("All say hello to " + message.getSource() + "!");
                        name = message.getSource();
                        toClient.writeObject(new SMessage("Success", "Registration successful"));
                        clients.add(name);
                        for (SMessage msg : lastMessages) {
                            toClient.writeObject(msg);
                        }
                        break;
                    case "Get user list":
                        System.out.println("Sending user list to " + message.getSource());
                        StringBuilder userList = new StringBuilder();
                        for (String client : clients)
                            userList.append(client).append("\n");
                        userList.deleteCharAt(userList.length() - 1);
                        toClient.writeObject(new SMessage("User list", userList.toString()));
                        System.out.println("User list sent!");
                }
                if (message.getType().equals("Message") || message.getType().equals("Registration"))
                    synchronized (lastMessages) {
                        lastMessages.add(message);
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
            } catch (Exception e) {
                System.out.println(name + " has left.");
                clients.remove(name);
                try {
                    objectOutputStreams.remove(toClient);
                    fromClient.close();
                    toClient.close();
                    message = new SMessage("Connection close", "", name);
                    synchronized (lastMessages) {
                        lastMessages.add(message);
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }
                currentThread().interrupt();
                break;
            }
        }
    }
}
