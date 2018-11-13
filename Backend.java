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

import java.util.ArrayList;
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
    /** A list of all connected, or previously connected users */
    private List<User> userList;
    private List<User> userListServer;
    /** Mapping chat panes to corresponding user */
    private HashMap<User, ChatPane> chatMap;
    private HashMap<User, ChatPane> multipartMap;
    private HashMap<String, User> userMap;

    private String myName;
    private int port;

    private ChatPane chatPane;

    private ChatPane multiPartPane = null;

    /**
     * Instantiates the necessary backend elements, generates and displays
     * initial GUI. Prompts for new connection if is a client, else waits
     * for incoming connection. 
     * @param info Information passed along from the StartFrame. Contains 
     * name, port, serversocket and whether the backend is started as server
     * or client.
     */
    public Backend(int port, String name, ServerSocket serverSocket) {
        Encrypter.initialize();

        /* Initialize all "storage" */
        chatMap = new HashMap<User, ChatPane>();
        multipartMap = new HashMap<>();
        userMap = new HashMap<String, User>();
        userList = new ArrayList<User>();
        userListServer = new ArrayList<>();

        /* Save other info */
        myName = name;
        this.port = port;
        this.serverSocket = serverSocket;

        createGUI();

        /* Start listening for incoming connections */
        waitForConnections(this);
    }

    /**
     * Creates and displays the general chat
     * GUI
     */
    private void createGUI() {
        frame = new JFrame("Chat");
        menuBar = new MenuBar(this);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        /** Disconnect all users/sockets if window is closed */
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are You Sure to Close Application?", 
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    disconnectAll();
                    disconnectServerSocket();
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
        User user = userMap.get(socket.getSocketID());
        
        /* If is a new message */
        if (query instanceof Message) {
            Message msg = (Message) query;
            
            /* If sent from a multipart server */
            if (msg.getMultipartMode().equals("server")) {
                /* Display msg to the correct chat (if multiple multiparts) */
                multipartMap.get(user).addMessage(msg);
                return;
            }
            /* If sent from a multipart client */
            else if (msg.getMultipartMode().equals("client")) {
                if (multiPartPane != null) {
                    /* Forward message to all other clients */
                    for (User usr : userListServer) {
                        if (usr == user) continue;
                        usr.getClientSocket().send(msg.getOriginalMessage());
                    }
                    multiPartPane.addMessage(msg);
                }
                return;
            }

            ChatPane chatPane = chatMap.get(user);
            chatPane.addMessage(msg);
        }
        /* If is a new connection request */
        // else if (query instanceof Request) {
        //     Request request = (Request) query;
        //     User newUser = new User(request.getName(), socket.getSocketID(), socket);

        //     /* Display prompt */
        //     showConnectionRequest(request, newUser);
        // }
        /* If is a reponse to a connection request */
        else if (query instanceof RequestResponse) {
            RequestResponse response = (RequestResponse) query;

            /* Add connection only if a "yes" is received */
            if (response.getReply().equals("yes")) {
                addConnectionAsClient(response.getName(), socket);
            }
            else {
                socket.close();
                JOptionPane.showMessageDialog(frame, "User at " + socket.getSocketID() + " denied your connection request.");
            }
        }
        else if (query instanceof FileRequest) {
            FileRequest fileRequest = (FileRequest) query;
            ChatPane chatPane = chatMap.get(user);
            FileHandler fileHandler = chatPane.getFileHandler();

            if(!fileHandler.getRunningStatus()) {
                fileHandler.ShowFileRequest(fileRequest, user.getClientSocket());
            }
            else{
                //skicka tillbaka error-meddelande
            }
        }
        else if (query instanceof FileResponse) {
            FileResponse fileResponse = (FileResponse) query;
            ChatPane chatPane = chatMap.get(user);
            FileHandler fileHandler = chatPane.getFileHandler();
            try {
                fileHandler.handleResponse(fileResponse);
            }catch(InterruptedException e1){
                //do something
            }
        }
        else if (query instanceof KeyRequest) {
            KeyRequest keyrequest = (KeyRequest) query;
            ChatPane chatPane = chatMap.get(user);

            String key = chatPane.getPublicKey(keyrequest.getType());
            if (key == null)
                return; // No suuuport for the key type
            user.getClientSocket().send(Composer.composeKeyRequestResponse(key));
        }
        else if (query.getMessage().equals("<disconnect />")) {
            ChatPane chatPane = chatMap.get(user);
            chatPane.disable();
            disconnect(user);

            /* If multipart chat is active, remove user from it */
            if (multiPartPane != null) {
                multiPartPane.addMessage(new Message(user.getName() + " disconnected."));
                multiPartPane.removeUser(user);
            }
        }
        else if (query.getMessage().equals("<multipartstart />")) {
            newClientMultipartConnection(socket);
        }
    }

    /**
     * Called whena new incoming connection request is detected. 
     * Displays a new window prompting whether to accept the new
     * connection.
     * @param request The message encapsulated in the connection
     * request. 
     */
    private void showConnectionRequest(Request request, User user, Boolean isPrimitive) {
        if (userList.size() >= MAX_CONNECTIONS) {
            if (isPrimitive)
                user.getClientSocket().send("<message>No</message>");
            else 
                user.getClientSocket().send(Composer.composeRequestReply(myName, "no"));
            JOptionPane.showMessageDialog(frame, "An user from " + user.getClientSocket().getSocketID() + " has tried to connect to you, but the max supported number of connections has been reached.");
            user.getClientSocket().close();
        }

        final Backend backendHere = this;

        /* Actually display the request */
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InConnectionPrompt(request.getMessage(), user, backendHere, isPrimitive);
            }
        });
    }

    /**
     * Internal method for creating a new multipart chat as a client. Creates
     * a new chatpane and registers it internally for receiving future messages
     * from the multipart chat.
     * @param socket The socket that the mutlipart chat is sent with
     */
    private void newClientMultipartConnection(SocketClient socket) {
        User user = userMap.get(socket.getSocketID());
        ChatPane newPane = new ChatPane(this, user, true);

        multipartMap.put(user, newPane);
        tabbedPane.add(newPane);
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

        menuBar.enableDisconnectButton();

        ChatPane newPane = new ChatPane(this, newUser, false);

        /* Save new chatpane and add it to the GUI */
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
    public void addConnectionAsServer(User user) {
        user.getClientSocket().send(Composer.composeRequestReply(myName, "yes"));

        menuBar.enableDisconnectButton();

        /* Save user to memory */
        userList.add(user);
        userListServer.add(user);
        userMap.put(user.getClientSocket().getSocketID(), user);

        ChatPane newPane = new ChatPane(this, user, false);

        /* Create a multipart chat if conditions are met */
        if (userListServer.size() > 1 && multiPartPane == null) {
            multiPartPane = new ChatPane(this, userListServer);
            multiPartPane.setName("general");
            tabbedPane.add(multiPartPane, 0);
        }
        /* Add user to multipart chat if it already exists */
        else if (multiPartPane != null) {
            multiPartPane.addUser(user);
        }

        /* Save chat tpane to memory and display it graphically */
        chatMap.put(user, newPane);
        tabbedPane.addTab(user.getName(), newPane);
    }

    /**
     * Internal method to process an incoming connection. Displays connection
     * request prompt, appends socket and new user to corresponding lists, 
     * creates new ChatPane if connection is accepted.
     * @param socket The client socket where the connection was established.
     * @param connectionRequest The message encapsulated in the connection request.
     */
    public void addNewConnection(Socket socket) {
        if (userList.size() >= MAX_CONNECTIONS) {
            JOptionPane.showMessageDialog(frame, "An user from " + socket.getRemoteSocketAddress() + " has tried to connect to you, but the max supported number of connections has been reached.");
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
        SocketClient newSocket = new SocketClient(socket, this);

        /* Wait for request message */
        int timeout = 1000;
        final int WAIT_TIME = 100;

        String message;
        Query parsedMessage;

        while (true) {
            message = newSocket.receive();
            System.err.println("Received connection request: " + message);

            /* Do not parse empty messages */
            if (message.equals("") || message == null)
                continue;

            /* Parse the message */
            parsedMessage = Transcriber.parse(message, newSocket);

            if (parsedMessage instanceof Request) {
                Request request = (Request) parsedMessage;
                User newUser = new User(request.getName(), newSocket.getSocketID(), newSocket);
    
                /* Display prompt */
                showConnectionRequest(request, newUser, false);
                break;
            }
            else if (parsedMessage instanceof Message) {
                Message msg = (Message) parsedMessage;
                User newUser = new User("", newSocket.getSocketID(), newSocket);

                Request fakeRequest = new Request("Does not say anything.", newSocket.getSocketID());
    
                /* Display prompt */
                showConnectionRequest(fakeRequest, newUser, true);
                break;
            }

            try {
                Thread.sleep(WAIT_TIME);
                timeout -= WAIT_TIME;
            } catch (InterruptedException e) {
                timeout -= WAIT_TIME;
            }

            /* Other end will not send a request, probably a primitive user */
            if (timeout <= 0) {
                User newUser = new User("", newSocket.getSocketID(), newSocket);

                Request fakeRequest = new Request("Does not say anything.", newSocket.getSocketID());
    
                /* Display prompt */
                showConnectionRequest(fakeRequest, newUser, true);
                break;
            }
        }

        /* Start listening for actual messages */
        newSocket.start();
    }

    /**
     * Continually listen for incoming connections
     * @param backend this backend object.
     */
    private void waitForConnections(Backend backend) {
        new Thread(new Runnable()
        {
            public void run() {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket newSocket = serverSocket.accept();
                        backend.addNewConnection(newSocket);
                    } catch (IOException e) {
                        System.err.print("Failed to accept connection.");
                        // Do nothing
                    }
                }
            }
        }).start();
    }
    
    /**
     * Disconnect all connections
     */
    public void disconnectAll() {
        for (int i = 0; i < userList.size(); i++)
        {
            User user = userList.get(i);
            chatMap.get(user).addMessage(new Message("You disconnected."));
            chatMap.get(user).disable();
            disconnect(user);
        }
    }

    /**
     * Disconnects the socket in the argument, sending a
     * disconnect string to the other-end socket if this is
     * the disconnecting end. Else inform the user in the correct
     * ChatPane that user has disconnected. Keep the ChatPane open.
     * @param user The socket that should be closed. 
     */
    public void disconnect(User user) {
        /* De-register the user */
        userList.remove(user);
        userListServer.remove(user);

        /* Send disconnect message to other user and close the socket */
        user.getClientSocket().send(Composer.DISCONNECT_MESSAGE);
        user.getClientSocket().close();

        /* Disable the disconnect all button if nothing left to disconnect */
        if (userList.size() <= 0)
            menuBar.disableDisconnectButton();

        /* Remove the user from the multipart chat */
        if (multipartMap.containsKey(user)) {
            ChatPane multipartPane = multipartMap.get(user);
            multipartMap.remove(user);

            multipartPane.disable();

            multipartPane.addMessage(new Message(user.getName() + " disconnected.", "000000", "", ""));
        }
    }
    
    /**
     * Closes a chat pane from the GUI
     * @param chatPane The chat pane to be removed
     * @param user The user associated with the chat pane to be removed
     */
    public void close(ChatPane chatPane, User user) {
        userMap.remove(user.getClientSocket().getSocketID());
        chatMap.remove(user);
        tabbedPane.remove(chatPane);
    }

    /**
     * Updates the name of the user
     * @param newName The new Name
     */
    public void updateName(String newName) {
        if (newName.equals("")) {
            myName = serverSocket.getLocalSocketAddress().toString();
        }
        else {
            myName = newName;
        }
    }

    /**
     * Closes the server socket
     */
    public void disconnectServerSocket() {
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            System.err.println("Server socket failed to close");
        }
    }

    /**
     * @return the myName
     */
    public String getMyName() {
        return myName;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
}