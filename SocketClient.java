import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetSocketAddress;

import java.lang.Runnable;
import java.lang.Thread;
import java.lang.StringBuilder;
import java.io.InputStreamReader;

/**
 * A custom class encapsulating a socket client. One instance of this class per
 * connection. 
 */
public class SocketClient implements Runnable {
    
    private Thread t;
	
    private volatile Socket clientSocket;
    private Backend backend;
    private PrintWriter out;
    private BufferedReader in;
	
	/**
	 * Constructor that gets a "pointer" to the backend, and the socket that was initialized in the backend
	 * @param socket, through all communication with the client is happening
	 * @param backend, to be able to "talk" with the backend
	 */
	public SocketClient(Socket socket, Backend backend) {
        clientSocket = socket;
        this.backend = backend;

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            // Process the exception
        }
	}
	
	/**
	 * 
	 */
	public void notifyObservers(Object obj) {
		
	}
	
	/**
	 * Where the socket receives messages and sends them to the parser and to notify observers
	 */
	private String receive() {
        StringBuilder string = new StringBuilder();

        try {
            String inputLine;
            if ((inputLine = in.readLine()) != null) {
                string.append(inputLine);
                System.err.println(inputLine);
            }
        }
        catch (IOException e ) {
            // Do something maybe?
            System.err.println("Something went wrong");
        }

        return string.toString();
	}
	
	/**
	 * Where the socket sends messages 
	 * @param msg, the message to be sent 
	 */
	public void send(String msg) {
        out.println(msg);
        out.flush();
    }
    
    public String getSocketID() {
        return ((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getAddress().toString();
    }

    public void close() {
        try {
        clientSocket.close();
        } catch (IOException e) {
            System.err.println("Unable to close socket");
            // Do nothing
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SocketClient))
            return false;
        return ((SocketClient) obj).getSocketID().equals(clientSocket.getRemoteSocketAddress().toString());
    }

    public void run() {
        String message;
        Query parsedMessage;
        while (!clientSocket.isClosed()) {
            message = receive();
            System.err.println("Received message " + message);
            if (message.equals(""))
                continue;

            parsedMessage = Transcriber.parse(message, this);

            backend.receiveMessage(parsedMessage, this);
        }
    }

    public void start () {
        // System.out.println("Starting " +  threadName );
        if (t == null) {
           t = new Thread(this);
           t.start();
        }
     }
}
