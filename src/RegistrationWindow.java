import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.regex.Pattern;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

@SuppressWarnings("ALL")
public class RegistrationWindow {

    private int port;
    private String ip;
    private String name;
    private Client client;

    public RegistrationWindow(Client client){
        this.client = client;
    }

    public void startReg(){
        JFrame frame = new JFrame();
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setLayout(grid);
        frame.setTitle("Chat v1.0");

        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel ipLabel = new JLabel("Enter server IP:");
        frame.add(ipLabel, gbc);
        gbc.gridy = 2;
        JLabel portLabel = new JLabel("Enter server port:");
        frame.add(portLabel, gbc);
        gbc.gridy = 4;
        JLabel nameLabel = new JLabel("Enter your name:");
        frame.add(nameLabel, gbc);
        gbc.gridy = 7;
        JLabel status = new JLabel("waiting for connect...");
        frame.add(status, gbc);

        gbc.gridy = 1;
        JTextField ip = new JTextField("127.0.0.1");
        ip.setColumns(8);
        ip.setHorizontalAlignment(JTextField.CENTER);
        frame.add(ip, gbc);

        gbc.gridy = 3;
        JTextField port = new JTextField("8080");
        port.setColumns(4);
        port.setHorizontalAlignment(JTextField.CENTER);
        frame.add(port, gbc);

        gbc.gridy = 5;
        JTextField name = new JTextField("Grisha");
        name.setColumns(6);
        name.setHorizontalAlignment(JTextField.CENTER);
        frame.add(name, gbc);

        gbc.gridy = 6;
        JButton button = new JButton("Login");
        button.addActionListener(e -> {
            if (!Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")
                    .matcher(ip.getText().trim())
                    .matches()) {
                ipLabel.setText("Enter a valid IP address.");
            } else if (name.getText().trim().equals("")) {
                nameLabel.setText("Enter your name.");
                //return;
            } else {
                try {
                    Integer.parseInt(port.getText().trim());
                    client.connect(ip.getText().trim(), Integer.parseInt(port.getText().trim()));
                    frame.setVisible(false);
                    ChatWindow cw = new ChatWindow(client);
                    client.registration(name.getText().trim(), cw.getUsers(), cw.getChat());
                    cw.launchChat();
                } catch (NumberFormatException e1) {
                    portLabel.setText("Enter a correct port number.");
                    //return;
                } catch (IOException e1) {
                    status.setText("Can`t connect to server!");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExceptionInInitializerError e1) {
                    status.setText("Can`t connect to server!");
                }

            }
        });
        frame.add(button, gbc);
        frame.setSize(200, 300);
        frame.setPreferredSize(frame.getSize());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);


    }

}
