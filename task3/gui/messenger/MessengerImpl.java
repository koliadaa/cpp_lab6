package udpWork.task3_multicast.gui.messenger;

import udpWork.task3_multicast.gui.interfaces.Messenger;
import udpWork.task3_multicast.gui.interfaces.UITasks;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MessengerImpl implements Messenger {

    private final UITasks tasks;
    private MulticastSocket group;
    private final InetAddress address;
    private final int port;
    private final String name;

    private boolean canceled = false;

    public MessengerImpl(InetAddress address, int port, String name, UITasks tasks) {
        this.name = name;
        this.tasks = tasks;
        this.address = address;
        this.port = port;
        try {
            group = new MulticastSocket(port);
            group.setTimeToLive(2);
            group.joinGroup(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        Thread thread = new Receiver();
        thread.start();
    }

    @Override
    public void stop() {
        cancel();
        try {
            group.leaveGroup(address);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error disconnecting", "Error disconnecting", JOptionPane.ERROR_MESSAGE);
        } finally {
            group.close();
        }
    }

    @Override
    public void send() {
        new Sender().start();
    }

    private class Sender extends Thread {
        public void run() {
            try {
                String msg = name + ": " + tasks.getMessage();
                byte[] out = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(out, out.length, address, port);
                group.send(packet);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error sending", "Error sending", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class Receiver extends Thread {
        public void run() {
            try {
                byte[] in = new byte[512];
                DatagramPacket packet = new DatagramPacket(in, in.length);
                while (!isCanceled()) {
                    group.receive(packet);
                    tasks.setText(new String(packet.getData(), 0, packet.getLength()));
                }
            } catch (Exception e) {
                if (isCanceled()) {
                    JOptionPane.showMessageDialog(null, "Connecting ended");
                } else {
                    JOptionPane.showMessageDialog(null, "Error receiving", "Error receiving", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private synchronized boolean isCanceled() {
        return canceled;
    }

    public synchronized void cancel() {
        canceled = true;
    }
}
