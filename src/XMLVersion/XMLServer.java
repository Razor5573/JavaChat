package XMLVersion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLServer {
    private ServerSocket mainSocket;
    private HashMap<String, String> clients;
    private ArrayList<XMLWriter> writers;
    private ArrayDeque<XMLMessage> lastMessages;
    private static final boolean SERVER_IS_ONLINE = true;

    XMLServer() {
        try {
            mainSocket = new ServerSocket(8080, 5000);
            clients = new HashMap<>();
            writers = new ArrayList<>();
            lastMessages = new ArrayDeque<>();
            XMLServerReadThread reader = new XMLServerReadThread(lastMessages, writers);
            reader.start();
            listen();
        } catch (IOException e) {
            System.out.println("Server died");
        }
    }

    private void listen() throws IOException {
        System.out.println("Server is online");
        XMLWriter writer;
        while (SERVER_IS_ONLINE) {
            Socket socket = mainSocket.accept();
            writer = new XMLWriter(socket.getOutputStream());
            writers.add(writer);
            System.out.println("New client: " + socket.getRemoteSocketAddress().toString());
            XMLServerWriteThread client = new XMLServerWriteThread(socket, lastMessages, writer, writers, clients);
            client.start();
        }
    }
}
