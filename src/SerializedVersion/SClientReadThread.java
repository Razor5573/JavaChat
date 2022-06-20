package SerializedVersion;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SClientReadThread extends Thread {
    private ObjectInputStream fromServer;
    private JTextArea chat;
    private JTextArea users;
    private boolean success;
    private ArrayList<String> otherClients;

    SClientReadThread(JTextArea users, JTextArea chat, ObjectInputStream from, ArrayList<String> oc) {
        this.chat = chat;
        this.users = users;
        otherClients = oc;
        fromServer = from;
        success = false;
    }

    @Override
    public void run() {
        SMessage message;
        while(!isInterrupted()) {
            try {
                message = (SMessage) fromServer.readObject();
                    switch (message.getType()) {
                        case "Connection close":
                            otherClients.remove(message.getSource());
                            users.setText("");
                            for (String user : otherClients)
                                users.append(user + "\n");
                            chat.append(message.getSource() + " has left\n");
                        case "Success":
                            synchronized (this) {
                                success = true;
                                notify();
                            }
                            break;
                        case "Error":
                            synchronized (this) {
                                success = false;
                                notify();
                            }
                            break;
                        case "Message":
                            chat.append(message.getDate() + " " + message.getSource() + ": " + message.getMessage() + "\n");
                            break;
                        case "Registration":
                            chat.append("All say hello to " + message.getSource() + "!" + "\n");
                            otherClients.add(message.getSource());
                            users.setText("");
                            for (String user : otherClients)
                                users.append(user + "\n");
                            break;
                        case "User list":
                            synchronized (this) {
                                otherClients = Stream
                                        .of(message.getMessage().split("\n"))
                                        .collect(Collectors.toCollection(ArrayList::new));
                                users.setText("");
                                for (String user : otherClients)
                                    users.append(user + "\n");
                                success = true;
                                notify();
                            }
                    }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                chat.append("Server has stopped working. Write anything to quit.\n");
                interrupt();
            }
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<String> getOtherClients() {
        return otherClients;
    }
}
