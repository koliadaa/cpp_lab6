package udpWork.task3_multicast.gui;

import udpWork.task3_multicast.gui.interfaces.Messenger;
import udpWork.task3_multicast.gui.interfaces.UITasks;
import udpWork.task3_multicast.gui.messenger.EDTInvocationHandler;
import udpWork.task3_multicast.gui.messenger.MessengerImpl;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientApp extends JFrame {
    private JPanel contentPane;

    private JTextField addressTextField;
    private JTextField portTextField;
    private JTextField nameTextField;
    private JLabel addressLabel;
    private JLabel portLabel;
    private JLabel nameLabel;

    private JTextArea allMessagesTextArea;
    private JTextField messageField;
    private JButton sendMessage;

    private JButton connect;
    private JButton disconnect;
    private JButton clear;
    private JButton exit;

    private Messenger messenger;

    public ClientApp(String title) {
        super(title);

        addButtonActions();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(contentPane);
    }

    private void createUIComponents() {

        addressTextField = new JTextField("224.0.0.1");
        portTextField = new JTextField("6666");
        nameTextField = new JTextField("Anna");
        addressLabel = new JLabel("Address");
        portLabel = new JLabel("Port");
        nameLabel = new JLabel("Name");

        sendMessage = new JButton("Send");

        connect = new JButton("Connect");
        disconnect = new JButton("Disconnect");
        disconnect.setEnabled(false);
        clear = new JButton("Clear");
        exit = new JButton("Exit");
    }

    private void addButtonActions() {
        sendMessage.addActionListener(event -> {
            if (messenger != null) {
                messenger.send();
            }
        });

        connect.addActionListener(event -> {
            UITasks tasks = (UITasks) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{UITasks.class}, new EDTInvocationHandler(new UITasksImpl()));
            try {
                messenger = new MessengerImpl(
                        InetAddress.getByName(addressTextField.getText()),
                        Integer.parseInt(portTextField.getText()),
                        nameTextField.getText(),
                        tasks);
                messenger.start();
                connect.setEnabled(false);
                disconnect.setEnabled(true);
            } catch (UnknownHostException exception) {
                exception.printStackTrace();
            }
        });

        disconnect.addActionListener(event -> {
            messenger.stop();
            disconnect.setEnabled(false);
            connect.setEnabled(true);
        });

        clear.addActionListener(event -> allMessagesTextArea.setText(""));

        exit.addActionListener(event -> {
            if (disconnect.isEnabled()) {
                messenger.stop();
            }
            this.dispose();
        });
    }

    private class UITasksImpl implements UITasks {

        @Override
        public String getMessage() {
            String res = messageField.getText();
            messageField.setText("");
            return res;
        }

        @Override
        public void setText(String txt) {
            allMessagesTextArea.append(txt + "\n");
        }
    }

    public static void main(String[] args) {
        Frame frame = new ClientApp("Koliada Anna's chat");
        frame.setSize(600,500);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2 - frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

        frame.setVisible(true);
    }
}
