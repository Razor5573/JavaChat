package XMLVersion;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLServerWriteThread extends Thread {

    private String name;
    private ArrayList<XMLWriter> writers;
    //name, type
    private HashMap<String, String> clients;
    private XMLReader fromClient;
    private XMLWriter toClient;
    private ArrayDeque<XMLMessage> lastMessages;
    private Integer maxLastMessages;

    XMLServerWriteThread(Socket socket, ArrayDeque<XMLMessage> lastMessages, XMLWriter toClient,
                       ArrayList<XMLWriter> writers, HashMap<String, String> clients) throws IOException {
        this.name = "New User";
        this.writers = writers;
        this.lastMessages = lastMessages;
        this.toClient = toClient;
        this.clients = clients;
        fromClient = new XMLReader(socket.getInputStream());
        maxLastMessages = 10;
    }

    @Override
    public void run() {
       // SMessage message;
        while (!this.isInterrupted()) {
            try {
                fromClient.readXMLMessage();
                switch (fromClient.getCommandOrEventName()) {
                    case "message":
                        System.out.println(name + ": " + fromClient.getMessage());
                        toClient.sendSuccessMessage("", "", null);
                        break;
                    case "login":
                        System.out.println("All say hello to " + fromClient.getName() + "!");
                        name = fromClient.getName();
                        clients.put(name, fromClient.getType());
                        toClient.sendSuccessMessage("login", Integer.toString(clients.size()), null);
                        for (XMLMessage msg : lastMessages) {
                            toClient.sendEventMessage(msg.getEventType(), msg.getMessage(), msg.getName());
                        }
                        break;
                    case "list":
                        System.out.println("Sending user list to " + fromClient.getUSID());
                        toClient.sendSuccessMessage("list", "", clients);
                        System.out.println("User list sent!");
                }
                if (fromClient.getCommandOrEventName().equals("message"))
                    synchronized (lastMessages) {
                        lastMessages.add(new XMLMessage("message", fromClient.getMessage(), name));
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
                if (fromClient.getCommandOrEventName().equals("login"))
                    synchronized (lastMessages) {
                        lastMessages.add(new XMLMessage("userlogin", fromClient.getName()));
                        if (lastMessages.size() > maxLastMessages)
                            lastMessages.removeFirst();
                        lastMessages.notify();
                    }
            } catch (Exception e) {
                System.out.println(name + " has left.");
                clients.remove(name);
                writers.remove(toClient);
                //fromClient.close;
                //toClient.close();
                XMLMessage message = new XMLMessage("userlogout", "", name);
                synchronized (lastMessages) {
                    lastMessages.add(message);
                    if (lastMessages.size() > maxLastMessages)
                        lastMessages.removeFirst();
                    lastMessages.notify();
                }
                currentThread().interrupt();
                break;
            }
        }
    }
}
