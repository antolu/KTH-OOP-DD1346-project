import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.util.HashMap;

/**
 * The controller backend for the chat client. Supports
 * <p><ul>
 * <li> Receives all incoming messages and queries from socket clients
 * usung observer-observable pattern. 
 * <li> Forwards receives message to corresponding chat window.
 * <li> Handles other queries using internal methods, for instance
 * showing file transfer prompt, incoming request prompt.
 * <li> Handles buttonpresses from the menu bar, including setting up new
 * connections, disconnecting chats, changing username.
 */
public class Backend implements ActionListener {
    private static final int MAX_CONNECTIONS = 3;

    /** The menu bar containing buttons for new connection, disconnect, change name */
    private JPanel menuBar;
    /** The graphical element for each chat. */
    private JTabbedPane tabbedPane;
    /** The mother of sockets */
    private ServerSocket serverSocket;
    /** A list of all generated sockets */
    private List<Socket> socketList;
    /** A list of all connected, or previously connected users */
    private List<User> userList;
    /** Mapping chat panes to corresponding user */
    private HashMap<User, ChatPane> chatMap;

    /**
     * Instantiates the necessary backend elements, generates and displays
     * initial GUI. Prompts for new connection if is a client, else waits
     * for incoming connection. 
     * @param info Information passed along from the StartFrame. Contains 
     * name, port, serversocket and whether the backend is started as server
     * or client.
     */
    public Backend(ourStruct info) {

    }

    /**
     * Internal method to process an incoming connection. Displays connection
     * request prompt, appends socket and new user to corresponding lists, 
     * creates new ChatPane if connection is accepted.
     * @param socket The client socket where the connection was established.
     * @param connectionRequest The message encapsulated in the connection request.
     */
    private void addConnection(Socket socket, String connectionRequest) {

    }

    /**
     * Observer-observable update method. called by the observables. 
     * Used for receiving new messages and queries from the client sockets.
     * @param caller The object that called this method.
     * @param arg Something to be passed along. Preferably a message.
     */
    public void update(Object caller, Object arg) {

    }

    /**
     * Generic ActionListener method. Handles buttonpresses of all 
     * graphical elements, like menubar buttons, various prompts.
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {

    }

    /**
     * Called whena new incoming connection request is detected. 
     * Displays a new window prompting whether to accept the new
     * connection.
     * @param connectionRequest The message encapsulated in the connection
     * request. 
     */
    private void showConnectionRequest(String connectionRequest) {

    }

    /**
     * Called when a new file transfer request is received. 
     * Displays a new window prompting a yes / no answer and a 
     * custom message response (unformatted), and the request message
     * from the other user.
     * @param requestMessage The message encapsulated in the file request.
     */
    private void showFileRequest(String requestMessage) {

    }

    /**
     * Called when the user wants to set up a new connection as client.
     * Displays a new window prompting for IP, port and message.
     */
    private void newConnection() {
        
    }

    /**
     * Disconnects the socket in the argument, sending a
     * disconnect string to the other-end socket if this is
     * the disconnecting end. Else inform the user in the correct
     * ChatPane that user has disconnected. Keep the ChatPane open.
     * @param socket The socket that should be closed. 
     */
    private void disconnect(Socket socket) {

    }
}