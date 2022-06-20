package SerializedVersion;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class SServer {
    private ServerSocket mainSocket;
    private ArrayList<String> clients;
    private ArrayList<ObjectOutputStream> streams;
    private ArrayDeque<SMessage> lastMessages;
    private static final boolean SERVER_IS_ONLINE = true;

    SServer() {
        try {
            mainSocket = new ServerSocket(8080, 5000);
            clients = new ArrayList<>();
            streams = new ArrayList<>();
            lastMessages = new ArrayDeque<>();
            SServerReadThread reader = new SServerReadThread(lastMessages, streams);
            reader.start();
            listen();
        } catch (IOException e) {
            System.out.println("Server died");
        }
    }

    private void listen() throws IOException {
        System.out.println("Server is online");
        ObjectOutputStream oos;
        while (SERVER_IS_ONLINE) {
            Socket socket = mainSocket.accept();
            oos = new ObjectOutputStream(socket.getOutputStream());
            streams.add(oos);
            System.out.println("New client: " + socket.getRemoteSocketAddress().toString());
            SServerWriteThread client = new SServerWriteThread(socket, lastMessages, oos, streams, clients);
            client.start();
        }
    }
}
