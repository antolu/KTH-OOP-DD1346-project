import javax.swing.JPanel;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ChatWindow chatWindow;
    /** The textfield where you type your messages */
    private JEditorPane msgField;
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

    /**
     * Basic constructor.
     * @param user The user this ChatPane should belong to
     * @param clientSocket The socket with which to communicate.
     */
    public ChatPane(Backend backend, User user) {
        this.backend = backend;
        this.user = user;
        this.clientSocket = user.getClientSocket();

        users = new ArrayList<>();
        sockets = new ArrayList<>();
        users.add(user);
        sockets.add(clientSocket);

        encryptionKeys = new HashMap<>();

        createGUI();
        addActionListeners(this);
    }

    private void createGUI() {
        chatWindow = new ChatWindow(user);
        msgField = new JEditorPane();

        /* Create and format buttonss */
        sendFileButton = new JButton();
        setEncryptionButton = new JButton();
        setColorButton = new JButton();
        disconnectButton = new JButton();
        sendButton = new JButton();
        encryptButton = new ToggleableButton("Encrypted", "Decrypted");

        sendFileButton.setText("Send file");
        setEncryptionButton.setText("Set encryption");
        setColorButton.setText("Select color");
        disconnectButton.setText("Disconnect");
        sendButton.setText("Send");
        encryptButton.setText("Encrypt");

        sendFileButton.setMaximumSize(new Dimension(140, 30));
        setEncryptionButton.setMaximumSize(new Dimension(140, 30));
        setColorButton.setMaximumSize(new Dimension(140, 30));
        disconnectButton.setMaximumSize(new Dimension(140, 30));
        sendButton.setMaximumSize(new Dimension(95, 30));
        encryptButton.setMaximumSize(new Dimension(95, 30));

        sendFileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        setEncryptionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        setColorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        disconnectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        leftPanel.add(chatWindow, BorderLayout.NORTH);
        leftPanel.add(msgFieldPanel, BorderLayout.SOUTH);

        rightPanel.add(sendFileButton);
        rightPanel.add(setEncryptionButton);
        rightPanel.add(setColorButton);
        rightPanel.add(disconnectButton);

        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightPanel, BorderLayout.WEST);
        this.setPreferredSize(new Dimension(780, 470));
    }

    private void addActionListeners(ChatPane pane) {
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = msgField.getText();
                msgField.setText("");
                Message msg = new Message(message, currentColor, dtf.format(LocalDateTime.now()), "Me");
                chatWindow.sentMessage(msg);


                // Actually send the message to the socket(s)
                if (encryptButton.getState()) {
                    message = Composer.composeMessage(message, currentColor, currentEncryptionType, encryptionKeys.get(currentEncryptionType), backend.getMyName());
                }
                else {
                    message = Composer.composeMessage(message, currentColor, "", "", backend.getMyName());
                }

                for (SocketClient socket : sockets) {
                    socket.send(message);
                }
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
                // Open up the file selection dialog
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Disconnect using backend or inform backend. SHOW CONFIRMATION!!!
            }
        });
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