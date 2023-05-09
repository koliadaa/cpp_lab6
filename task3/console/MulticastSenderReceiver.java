package udpWork.task3_multicast.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSenderReceiver {

    private final String name;
    private InetAddress addr;
    private final int port = 3456;
    private MulticastSocket group;

    public MulticastSenderReceiver(String name) {
        this.name = name;
        try {
            addr = InetAddress.getByName("224.0.0.1");
            group = new MulticastSocket(port);
            new Receiver().start();
            new Sender().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Sender extends Thread {
        public void run() {
            try {
                BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String msg = name + ": " + fromUser.readLine();
                    byte[] out = msg.getBytes();
                    DatagramPacket packet = new DatagramPacket(out, out.length, addr, port);
                    group.send(packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Receiver extends Thread {
        public void run() {
            try {
                byte[] in = new byte[256];
                DatagramPacket packet = new DatagramPacket(in, in.length);
                group.joinGroup(addr);
                while (true) {
                    group.receive(packet);
                    System.out.println(new String(packet.getData(), 0, packet.getLength()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new MulticastSenderReceiver(args[0]);
    }
}

