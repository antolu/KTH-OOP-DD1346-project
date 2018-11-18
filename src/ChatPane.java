import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import java.net.ServerSocket;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

import java.io.File;
import java.io.IOException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

/**
 * The class responsible for each chat window for every chat/user.
 * Instantiates all graphical elements internally and can handle buttonpresses
 * on its own, sending messages of all forms to the correct socket.
 */
public class ChatPane extends JPanel {
    private Backend backend;
    private JScrollPane scrollPane;
    private ChatWindow chatWindow;
    private JTextField msgField;

    /** All relevant buttons for the GUI*/
    private JButton sendButton;
    private JButton sendFileButton;
    private JButton setEncryptionButton;
    private JButton setColorButton;
    private JButton disconnectButton;
    private ToggleableButton encryptButton;
    private JButton closeButton;
    private JButton keyRequest;

    /** Placeholder for multipart chats */
    private List<User> users;

    /** The current color all msesages are sent with */
    private volatile String currentColor = "000000";
    /** The current global enryption type */
    private String currentEncryptionType = "";
    /** Contains all the keys, each key corresponding to a type. <type,key>  */
    private HashMap<String, String> encryptionKeys;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Boolean isMultipartServer = false;
    private Boolean isMultipartClient = false;
    private FileHandler fileHandler;
    private KeyRequestChooser keyRequestChooser;

    /**
     * Basic constructor.
     * @param user The user this ChatPane should belong to
     * @param clientSocket The socket with which to communicate.
     */
    public ChatPane(Backend backend, User user , Boolean isMultipartClient) {
        this.backend = backend;
        this.isMultipartClient = isMultipartClient;


        users = new ArrayList<>();
        users.add(user);
        this.fileHandler = new FileHandler(users.get(0));
        this.keyRequestChooser = new KeyRequestChooser(users.get(0));
        encryptionKeys = new HashMap<>();

        createGUI();
        addActionListeners();

        /* Disables certain buttons, prevents mistakes */
        if (isMultipartClient) {
            disconnectButton.setEnabled(false);
            sendFileButton.setEnabled(false);
        }
    }

    /**
     * Creates a multipart chat
     * @param backend
     * @param users
     */
    public ChatPane(Backend backend, List<User> users) {
        this.backend = backend;
        this.users = users;

        isMultipartServer = true;

        encryptionKeys = new HashMap<>();

        createGUI();
        addActionListeners();

        /* Disables certain buttons, prevents mistakes */
        disconnectButton.setEnabled(false);
        closeButton.setEnabled(false);
        sendFileButton.setEnabled(false);
        keyRequest.setEnabled(false);

        /* Start multipart on all clients also */
        for (User user : users) {
            user.getClientSocket().send(Composer.MULTIPART_START);
        }
    }

    /**
     * Creates the chat window, and all the relevant buttons.
     */
    private void createGUI() {
        if (!isMultipartClient && !isMultipartServer)
            chatWindow = new ChatWindow(users.get(0).getName());
        else
            chatWindow = new ChatWindow("");
        scrollPane = new JScrollPane(chatWindow);
        msgField = new JTextField();

        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

        /* Create and format buttons */
        sendFileButton = new JButton();
        setEncryptionButton = new JButton();
        setColorButton = new JButton();
        disconnectButton = new JButton();
        sendButton = new JButton();
        encryptButton = new ToggleableButton("Encrypted", "Decrypted");
        closeButton = new JButton();
        keyRequest = new JButton();

        sendFileButton.setText("Send file");
        setEncryptionButton.setText("Set encryption");
        setColorButton.setText("Select color");
        disconnectButton.setText("Disconnect");
        sendButton.setText("Send");
        encryptButton.setText("Encrypt");
        closeButton.setText("Close");
        keyRequest.setText("Key request");

        sendFileButton.setMaximumSize(new Dimension(140, 30));
        setEncryptionButton.setMaximumSize(new Dimension(140, 30));
        setColorButton.setMaximumSize(new Dimension(140, 30));
        disconnectButton.setMaximumSize(new Dimension(140, 30));
        sendButton.setMaximumSize(new Dimension(95, 30));
        encryptButton.setMaximumSize(new Dimension(95, 30));
        closeButton.setMaximumSize(new Dimension(140, 30));
        keyRequest.setMaximumSize(new Dimension(140, 30));

        sendFileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        setEncryptionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        setColorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        disconnectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        closeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sendButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        encryptButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        keyRequest.setAlignmentX(Component.LEFT_ALIGNMENT);

        /* Build all the other layers in the GUI */
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel msgFieldPanel = new JPanel();
        JPanel sendButtonPanel = new JPanel();

        /* Format all JPanels */
        msgField.setPreferredSize(new Dimension(500, 50));
        rightPanel.setPreferredSize(new Dimension(140, 460));
        sendButtonPanel.setPreferredSize(new Dimension(90, 50));
        msgFieldPanel.setPreferredSize(new Dimension(600, 60));
        msgField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sendButtonPanel.setLayout(new BoxLayout(sendButtonPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        /* Add all elements to their respective panels */
        sendButtonPanel.add(sendButton);
        sendButtonPanel.add(encryptButton);
        msgFieldPanel.add(msgField, BorderLayout.WEST);
        msgFieldPanel.add(sendButtonPanel, BorderLayout.EAST);

        leftPanel.add(scrollPane, BorderLayout.NORTH);
        leftPanel.add(msgFieldPanel, BorderLayout.SOUTH);

        rightPanel.add(sendFileButton);
        rightPanel.add(setEncryptionButton);
        rightPanel.add(setColorButton);
        rightPanel.add(disconnectButton);
        rightPanel.add(closeButton);
        rightPanel.add(keyRequest);

        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightPanel, BorderLayout.WEST);
        this.setPreferredSize(new Dimension(780, 470));
    }

    /**
     * Adds actionlisteners to all the buttons and the message field.
     */
    private void addActionListeners() {
        final ChatPane pane = this;

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Retrieve message from text field and send it if is not empty */
                String msg = msgField.getText();
                if (msg.equals("")) return;
                sendMessage(msg); 
                msgField.setText("");
            }
        });

        msgField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Retrieve message from text field and send it if is not empty */
                String msg = msgField.getText();
                if (msg.equals("")) return;
                sendMessage(msg); 
                msgField.setText("");
            }
        });

        setColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Creates a color chooser on a new thread */
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new ColorChooser(pane);
                    }
                });
            }
        });

        setEncryptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Creates an encryption chooser on a new thread */
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new EncryptionChooser(pane);
                    }
                });
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Checks if there is an encryption selected, and toggles 
                the button if so */
                if (currentEncryptionType.equals("")) {
                    JOptionPane.showMessageDialog(pane, "No encryption has been selected. Please select an encryption first.");
                    return;
                }
                encryptButton.toggleState();
            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run(){

                        String encrKey="";

                        if(!currentEncryptionType.equals("") && encryptButton.getState()) {
                            encrKey=encryptionKeys.get(currentEncryptionType);
                        }
                        //Choose file
                        new FileChooser(currentEncryptionType, encrKey, users, fileHandler);
                    }
                });
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Show confirmation dialog, disconnect if true */
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are You Sure to disconnect?", 
                    "Disconnect Confirmation", JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    disconnect();
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Show confirmation dialog, close pane if true */
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are you sure you want to close this window? \n This will close the connection if it is still open.", 
                    "Disconnect Confirmation", JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    close();
                }
            }
        });

        keyRequest.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        keyRequestChooser.createGUI();
                    }
                });
            }
        });
    }

    /**
     * Sends the a message to all users existing in this chatpane
     * @param message The message to be sent.
     */
    private void sendMessage(String message) {
  
        /* Display the message to the senders feed */
        Message msg = new Message(message, currentColor, dtf.format(LocalDateTime.now()), "Me");
        chatWindow.sentMessage(msg);

        // Actually send the message to the socket(s)
        /* Format the message accordingly if is a multipart message */
        if (isMultipartServer) {
            if (encryptButton.getState()) {
                message = Composer.composeMultiPartMessage(message, currentColor, currentEncryptionType, encryptionKeys.get(currentEncryptionType), backend.getMyName(), "server");
            }
            else {
                message = Composer.composeMultiPartMessage(message, currentColor, "", "", backend.getMyName(), "server");
            }
        }
        else if(isMultipartClient) {
            if (encryptButton.getState()) {
                message = Composer.composeMultiPartMessage(message, currentColor, currentEncryptionType, encryptionKeys.get(currentEncryptionType), backend.getMyName(), "client");
            }
            else {
                message = Composer.composeMultiPartMessage(message, currentColor, "", "", backend.getMyName(), "client");
            }
        }
        else {
            if (encryptButton.getState()) {
                
                message = Composer.composeMessage(message, currentColor, currentEncryptionType, encryptionKeys.get(currentEncryptionType), backend.getMyName());
            }
            else {
                message = Composer.composeMessage(message, currentColor, "", "", backend.getMyName());
            }
        }

        /* Send composed message to all users */
        for (User user : users) {
            user.getClientSocket().send(message);
        }
    }

    /**
     * Gets the file handler
     * @return The File handler
     */
    public FileHandler getFileHandler() {
        return fileHandler;
    }

    /**
     * Get the key request chooser
     * @return the key request chooser
     */
    public KeyRequestChooser getKeyRequestChooser() {
        return keyRequestChooser;
    }

    /**
     * Adds a user to a multipart chat
     * @param user The user to be added
     */
    public void addUser(User user) {
        /* Tell the user to start a client multipart chat */
        user.getClientSocket().send(Composer.MULTIPART_START);

        /* Inform "me" that a new user has been added */
        addMessage(new Message(user + " connected."));

        // /* Actually add the user */
        // users.add(user);
    }
    
    /**
     * Removes a user from a multipart chat
     * @param user The user to be removed
     */
    public void removeUser(User user) {
        users.remove(user);
        /* Tell "me" that a user has disconnected */
        sendMessage(user + " disconnected.");
    }

    /**
     * Close this chat pane
     */
    private void close() {
        /* Disconnect automatically if not a multipart chat */
        if (!isMultipartClient && sendButton.isEnabled()) 
            disconnect();

        /* Remove the pane from the GUI using backend */
        backend.close(this, users.get(0));
    }

    /**
     * Disconnect the connection represented in the current chat pane
     */
    private void disconnect() {
        /* Tell "me" that I have disconnected */
        chatWindow.addMessage(new Message("You disconnected.", "000000", "", ""));

        /* Disable the chat pane GUI (buttons) */
        disable();

        /* Use backend to disconnect the user */
        backend.disconnect(users.get(0));
    }

    /**
     * Disables the GUI: isables buttons.
     */
    public void disable() {
        sendButton.setEnabled(false);
        sendFileButton.setEnabled(false);
        setEncryptionButton.setEnabled(false);
        encryptButton.setEnabled(false);
        disconnectButton.setEnabled(false);
        setColorButton.setEnabled(false);
        msgField.setEditable(false);
        keyRequest.setEnabled(false);
    }

    /**
     * Fetches the key for the specified encryption type.
     * @param type The encryption type we want the key for
     * @return The corresponding key.
     */
    public String getPublicKey(String type) {
        return encryptionKeys.get(type);
    }

    /**
     * Updates the color used to send messages
     * @param color The new color
     */
    public void updateColor(String color) {
        currentColor = color;
    }

    /**
     * Updates the encryption used to send messages
     * @param type The new encryption type
     * @param key The new encryption key
     */
    public void updateEncryption(String type, String key) {
        this.currentEncryptionType = type;
        encryptionKeys.put(type, key);
    }

    /**
     * Adds a new message in the ChatWindow, callable from Backend when a
     * new message is received from the corresponding socket.
     * @param msg The message to be added to the feed. 
     */
    public void addMessage(Message msg) {
        chatWindow.addMessage(msg);
    }
}