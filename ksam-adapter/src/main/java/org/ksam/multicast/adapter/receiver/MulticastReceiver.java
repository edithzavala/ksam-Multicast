package org.ksam.multicast.adapter.receiver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulticastReceiver {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    // static ServerSocket variable
    private static ServerSocket server;
    // socket server port on which it will listen
    private static int port = 8083;

    public static void main(String args[]) throws IOException, ClassNotFoundException {
	// create the socket server object
	server = new ServerSocket(port);
	// keep listens indefinitely until receives 'exit' call or program terminates
	boolean finish = false;
	while (true) {
	    System.out.println("Waiting for client request");
	    // creating socket and waiting for client connection
	    Socket socket = server.accept();
	    // read from socket to ObjectInputStream object
	    // ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	    // //convert ObjectInputStream object to String
	    // String message = (String) ois.readObject();
	    // System.out.println("Message Received: " + message);
	    // create ObjectOutputStream object
	    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	    // write object to Socket
	    oos.writeObject("Hi Client ");
	    // close resources
	    // ois.close();
	    oos.close();
	    socket.close();
	    // terminate the server if client sends exit request
	    if (finish)
		break;
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	// System.out.println("Shutting down Socket server!!");
	// close the ServerSocket object
	server.close();
    }
}
