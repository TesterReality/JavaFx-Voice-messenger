package org.voicemessanger.client.main;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPChannel  extends Thread{
    private DatagramSocket socket;
    private boolean running;


    UDPChannel()
    {

    }

    public void bind(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        byte buffer[] = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer , buffer.length);

        running = true;
        while (running) {
            try {
                socket.receive(packet);
                String msg = new String(buffer , 0 , packet.getLength());
                System.out.println("[КЛИЕНТ] ПО UDP ПРИШЛО:" + msg);

                //area.appendText(msg+"\n");

            }
            catch (IOException e) {
                break;
            }
        }
    }

    public void sendTo(InetSocketAddress address , String msg) throws IOException {
        byte [] buffer = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer , buffer.length);
        packet.setSocketAddress(address);

        socket.send(packet);
    }


}
