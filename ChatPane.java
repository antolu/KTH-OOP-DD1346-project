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
    /** The window that displays all the messages */
    private JScrollPane scrollPane;
    private ChatWindow chatWindow;
    /** The textfield where you type your messages */
    private JTextField msgField;
    /** Press here to send */
    private JButton sendButton;
    /** Press here to send a file */
    private JButton sendFileButton;
    /** Press here to set global encryption. */
    private JButton setEncryptionButton;
    /** Press here to open up a color selector */
    private JButton setColorButton;
    private JButton disconnectButton;
    private ToggleableButton encryptButton;
    private JButton closeButton;

    /** The user messages are sent to */
    private User user;
    /** The socket messages are sent to */
    private SocketClient clientSocket;

    /** Placeholder for multipart chats */
    private List<User> users;
    private List<SocketClient> sockets;

    /** The current color all msesages are sent with */
    private volatile String currentColor = "000000";
    /** If this JPanel is visible in the GUI currently or not. */
    private Boolean isVisible;
    /** The current global enryption type */
    private String currentEncryptionType = "";
    /** Contains all the keys, each key corresponding to a type. <type,key>  */
    private HashMap<String, String> encryptionKeys;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Boolean isMultipartServer = false;
    private Boolean isMultipartClient = false;
    private FileHandler fileHandler;

    /**
     * Basic constructor.
     * @param user The user this ChatPane should belong to
     * @param clientSocket The socket with which to communicate.
     */
    public ChatPane(Backend backend, User user , Boolean isMultipartClient) {
        this.backend = backend;
        this.user = user;
        this.clientSocket = user.getClientSocket();
        this.isMultipartClient = isMultipartClient;
        this.fileHandler = new FileHandler(users.get(0));

        users = new ArrayList<>();
        sockets = new ArrayList<>();
        users.add(user);
        sockets.add(clientSocket);

        encryptionKeys = new HashMap<>();

        createGUI();
        addActionListeners(this);

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

        users = new ArrayList<>();
        sockets = new ArrayList<>();
        encryptionKeys = new HashMap<>();

        createGUI();
        disconnectButton.setEnabled(false);
        closeButton.setEnabled(false);
        sendFileButton.setEnabled(false);
    }

    private void createGUI() {
        chatWindow = new ChatWindow(users.get(0));
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

        sendFileButton.setText("Send file");
        setEncryptionButton.setText("Set encryption");
        setColorButton.setText("Select color");
        disconnectButton.setText("Disconnect");
        sendButton.setText("Send");
        encryptButton.setText("Encrypt");
        closeButton.setText("Close");

        sendFileButton.setMaximumSize(new Dimension(140, 30));
        setEncryptionButton.setMaximumSize(new Dimension(140, 30));
        setColorButton.setMaximumSize(new Dimension(140, 30));
        disconnectButton.setMaximumSize(new Dimension(140, 30));
        sendButton.setMaximumSize(new Dimension(95, 30));
        encryptButton.setMaximumSize(new Dimension(95, 30));
        closeButton.setMaximumSize(new Dimension(140, 30));

        sendFileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        setEncryptionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        setColorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        disconnectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        closeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sendButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        encryptButton.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightPanel, BorderLayout.WEST);
        this.setPreferredSize(new Dimension(780, 470));
    }

    private void addActionListeners(ChatPane pane) {
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        msgField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(); 
            }
        });

        setColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new ColorChooser(pane);
                    }
                });
            }
        });

        setEncryptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new EncryptionChooser(pane);
                    }
                });
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

                        if(!currentEncryptionType.equals("")) {
                            encrKey=encryptionKeys.get(currentEncryptionType);
                        }

                        new FileChooser(currentEncryptionType, encrKey, users, fileHandler);
                    }
                });
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are You Sure to disconnect?", 
                    "Disconnect Confirmation", JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    disconnect();
                }
                // Disconnect using backend or inform backend. SHOW CONFIRMATION!!!
            }
        });

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are you sure you want to close this window? \n This will close the connection if it is still open.", 
                    "Disconnect Confirmation", JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    close();
                }
                // Disconnect using backend or inform backend. SHOW CONFIRMATION!!!
            }
        });
    }

    private void sendMessage() {
        String message = msgField.getText();
        msgField.setText("");
        Message msg = new Message(message, currentColor, dtf.format(LocalDateTime.now()), "Me");
        chatWindow.sentMessage(msg);


        // Actually send the message to the socket(s)
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

        for (User user : users) {
            user.getClientSocket().send(message);
        }
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public void addUser(User user) {
        addMessage(new Message(user + "connected."));
        users.add(user);
    }
    
    public void removeUser(User user) {
        users.remove(user);
    }

    private void close() {
        if (sendButton.isEnabled()) 
            disconnect();

        backend.close(this, users.get(0));
    }

    private void disconnect() {
        chatWindow.addMessage(new Message("You disconnected.", "000000", "", ""));
        disconnectExternal();
    }

    public void disconnectExternal() {
        disable();

        backend.disconnect(users.get(0));
    }

    @Override
    public void disable() {
        sendButton.setEnabled(false);
        sendFileButton.setEnabled(false);
        setEncryptionButton.setEnabled(false);
        encryptButton.setEnabled(false);
        disconnectButton.setEnabled(false);
        setColorButton.setEnabled(false);
        msgField.setEditable(false);

    }

    /**
     * Fetches the key for the specified encryption type.
     * @param type The encryption type we want the key for
     * @return The corresponding key.
     */
    public String getPublicKey(String type) {
        return encryptionKeys.get(type);
    }

    public void updateColor(String color) {
        currentColor = color;
    }

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