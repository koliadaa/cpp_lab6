package udpWork.task2.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class SenderThread extends Thread {

    private final InetAddress server;
    private final int port;
    private final DatagramSocket socket;

    private volatile boolean stopped = false;

    public SenderThread(DatagramSocket socket, InetAddress address, int port) {
        this.server = address;
        this.port = port;
        this.socket = socket;
        this.socket.connect(server, port);
    }

    @Override
    public void run() {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while (!stopped) {
                String userText = userInput.readLine();
                if (userText.equals(".")) {
                    halt();
                    break;
                }
                byte[] data = userText.getBytes(StandardCharsets.UTF_8);
                DatagramPacket output = new DatagramPacket(data, data.length, server, port);
                socket.send(output);
                Thread.yield();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void halt() {
        stopped = true;
    }
}
