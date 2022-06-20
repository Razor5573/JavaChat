import javax.swing.*;
import java.io.IOException;

public interface Client {

    void registration(String name, JTextArea users, JTextArea chat) throws IOException, InterruptedException;

    void connect(String ip, Integer port);

    void getUsers();

    void writeMessage(String message);
}
