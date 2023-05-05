package udpWork.task2.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class ReceiverThread extends Thread {

    private final DatagramSocket socket;

    private volatile boolean stopped = false;

    ReceiverThread(DatagramSocket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        byte[] buffer = new byte[65507];
        while (!stopped) {
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(dp);
                String receivedText = new String(dp.getData(), 0, dp.getLength(), StandardCharsets.UTF_8);
                System.out.println(receivedText);
                Thread.yield();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void halt() {
        this.stopped = true;
    }
}
