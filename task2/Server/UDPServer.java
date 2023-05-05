package udpWork.task2.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class UDPServer implements Runnable {

    private final int bufferSize;
    private final int port;

    private volatile boolean isShutDown = false;

    public UDPServer(int port, int bufferSize) {
        this.bufferSize = bufferSize;
        this.port = port;
    }

    public UDPServer(int port) {
        this(port, 8192);
    }

    public UDPServer() {
        this(12345, 8192);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[bufferSize];
        try (DatagramSocket socket = new DatagramSocket(port)) {
            while (!isShutDown) {
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(incoming);
                    this.response(socket, incoming);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (SocketException exception) {
            exception.printStackTrace();
        }
    }

    public void shutDown() {
        this.isShutDown = true;
    }

    public abstract void response(DatagramSocket socket, DatagramPacket request) throws IOException;

}
