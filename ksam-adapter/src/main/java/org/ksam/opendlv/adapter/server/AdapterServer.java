package org.ksam.opendlv.adapter.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterServer.class);
    private static final int PORT = 8083;

    public AdapterServer() {
	(new Thread(this)).start();
    }

    @Override
    public void run() {
	LOGGER.info("KSAM Adapter server up");
	ServerSocket server = null;
	try {
	    server = new ServerSocket(PORT);
	} catch (IOException e1) {
	    LOGGER.error(e1.getMessage());
	}
	while (true) {
	    try {
		Socket socket = server.accept();

		DataInputStream dis = new DataInputStream(socket.getInputStream());
		byte[] data = new byte[1024];
		dis.read(data);
		LOGGER.info(new String(data));

		dis.close();
		socket.close();
	    } catch (IOException e1) {
		LOGGER.error(e1.getMessage());
	    }
	}
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
	new AdapterServer();
    }
}
