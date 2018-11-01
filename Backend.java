import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;

import java.io.IOException;

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
 * </ul>
 */
public class Backend {
    private static final int MAX_CONNECTIONS = 3;

    private JFrame frame;
    /** The menu bar containing buttons for new connection, disconnect, change name */
    private MenuBar menuBar;
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
    private HashMap<String, User> userMap;

    private String myName;
    private int port;

    private ChatPane chatPane;

    /**
     * Instantiates the necessary backend elements, generates and displays
     * initial GUI. Prompts for new connection if is a client, else waits
     * for incoming connection. 
     * @param info Information passed along from the StartFrame. Contains 
     * name, port, serversocket and whether the backend is started as server
     * or client.
     */
    public Backend(ourStruct info) {
        Encrypter.initialize();
        chatMap = new HashMap<>();
        userMap = new HashMap<>();

        myName = info.getName();
        port = info.getPort();
        serverSocket = info.getServerSocket();

        frame = new JFrame("Chat");
        menuBar = new MenuBar(this);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are You Sure to Close Application?", 
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    disconnect();
                    System.exit(0);
                }
            }
        });

        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(760, 500));

        frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.setPreferredSize(new Dimension(770, 560));
        frame.add(menuBar, BorderLayout.NORTH);
        frame.add(tabbedPane, BorderLayout.SOUTH);

        // User testUser = new User("Other", "OtherIP");
        // chatPane = new ChatPane(testUser);

        // tabbedPane.add(testUser.getName(), chatPane);
        // new MessageTest(this);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Observer-observable update method. called by the observables. 
     * Used for receiving new messages and queries from the client sockets.
     * @param socket The socket that called this method.
     * @param query The new message.
     */
    public void receiveMessage(Query query, SocketClient socket) {
        if (query instanceof Message) {
            Message msg = (Message) query;

            User user = userMap.get(socket.getSocketID());
            ChatPane chatPane = chatMap.get(user);
            chatPane.addMessage(msg);
        }
        else if (query instanceof Request) {

        }
        else if (query instanceof RequestResponse) {
            RequestResponse response = (RequestResponse) query;
            if (response.getReply().equals("yes")) {
                addConnectionAsClient(response.getName(), socket);
                // add socket
            }
            else {
                socket.close();
                JOptionPane.showMessageDialog(frame, "User at " + socket.getSocketID() + " denied your connection request.");
            }
        }
        else if (query instanceof FileRequest) {
            
        }
        else if (query instanceof FileResponse) {

        }
        else if (query instanceof KeyRequest) {

        }
        else if (query.getMessage().equals("<disconnect />")) {
            // Disconnect the socket
        }
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
     * Called by the New connection popup window. Sends a connection request 
     * to the other end.
     */
    public void newClientConnection(InetAddress IP, int port, String message) throws IOException {

        Socket socket = new Socket(IP, port);

        final SocketClient socketClient = new SocketClient(socket, this);
        socketClient.send(Composer.composeRequest(message, myName));

        /* Start receiving messages, including response to request */
        socketClient.start();
    }

    /**
     * Internal method to process an incoming connection. Displays connection
     * request prompt, appends socket and new user to corresponding lists, 
     * creates new ChatPane if connection is accepted.
     * @param socket The client socket where the connection was established.
     * @param connectionRequest The message encapsulated in the connection request.
     */
    private void addConnectionAsClient(String name, SocketClient socket) {
        User newUser = new User(name, socket.getSocketID(), socket);

        userList.add(newUser);
        userMap.put(socket.getSocketID(), newUser);

        ChatPane newPane = new ChatPane(newUser);

        chatMap.put(newUser, newPane);
        tabbedPane.addTab(name, newPane);
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
     * Disconnects the socket in the argument, sending a
     * disconnect string to the other-end socket if this is
     * the disconnecting end. Else inform the user in the correct
     * ChatPane that user has disconnected. Keep the ChatPane open.
     * @param socket The socket that should be closed. 
     */
    private void disconnect(Socket socket) {

    }

    public void updateName(String newName) {
        if (newName.equals("")) {
            myName = serverSocket.getLocalSocketAddress().toString();
        }
        else {
            myName = newName;
        }
    }

    public void disconnect() {
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            System.err.println("Server socket failed to close");
        }
        // 
    }
}