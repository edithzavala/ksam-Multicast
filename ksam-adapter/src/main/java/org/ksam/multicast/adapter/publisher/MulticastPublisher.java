package org.ksam.multicast.adapter.publisher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulticastPublisher {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    private final int BROADCAST_PORT_SERVER = 19750;
    private final String cId = "111";
    private final String BROADCAST_IP = "255.0.0" + cId;
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;

    public void multicast(String multicastMessage) throws IOException {
	socket = new DatagramSocket();
	group = InetAddress.getByName(BROADCAST_IP);
	buf = multicastMessage.getBytes();

	DatagramPacket packet = new DatagramPacket(buf, buf.length, group, BROADCAST_PORT_SERVER);
	socket.send(packet);
	socket.close();
    }

    public static void main(String[] args) throws Exception {
	MulticastPublisher pub = new MulticastPublisher();
	try {
	    pub.multicast("hola");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
