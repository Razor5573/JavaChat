package XMLVersion;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class XMLServerReadThread extends Thread{
    //message, name
    private ArrayDeque<XMLMessage> lastMessages;
    private ArrayList<XMLWriter> writers;

    XMLServerReadThread(ArrayDeque<XMLMessage> lastMessages, ArrayList<XMLWriter> writers) {
        this.lastMessages = lastMessages;
        this.writers = writers;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            synchronized (lastMessages) {
                try {
                    lastMessages.wait();
                    for (XMLWriter writer : writers) {
                        try {
                            writer.sendEventMessage(lastMessages.getLast().getEventType(),
                                    lastMessages.getLast().getMessage(),
                                    lastMessages.getLast().getName());
                        }
                        catch (IOException e1) {
                            System.out.println(e1.getMessage());
                            writers.remove(writer);
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());

                }
            }
        }
    }
}
