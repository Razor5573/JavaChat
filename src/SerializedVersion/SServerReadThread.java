package SerializedVersion;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class SServerReadThread extends Thread{
    private ArrayDeque<SMessage> lastMessages;
    private ArrayList<ObjectOutputStream> objectOutputStreams;

    SServerReadThread(ArrayDeque<SMessage> lastMessages, ArrayList<ObjectOutputStream> oos) {
        this.lastMessages = lastMessages;
        this.objectOutputStreams = oos;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            synchronized (lastMessages) {
                try {
                    lastMessages.wait();
                    for (ObjectOutputStream oos : objectOutputStreams) {
                        try {
                            oos.writeObject(lastMessages.getLast());
                        } catch (IOException e1) {
                            System.out.println(e1.getMessage());
                            objectOutputStreams.remove(oos);
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());

                }
            }
        }
    }




}
