package SerializedVersion;

import RegistrationWindow;

public class ClientMain {
    public static void main(String[] args) {

        SClient client = new SClient();
        try {
            RegistrationWindow rw = new RegistrationWindow(client);
            rw.startReg();
        } catch (ExceptionInInitializerError e) {
            System.out.println("Can`t connect to this server!");
        }
    }
}
