import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JEditorPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.HashMap;

/**
 * The class responsible for each chat window for every chat/user.
 * Instantiates all graphical elements internally and can handle buttonpresses
 * on its own, sending messages of all forms to the correct socket.
 */
public class ChatPane {
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

    /** The user messages are sent to */
    private User user;
    /** The socket messages are sent to */
    private Socket clientSocket;

    /** Placeholder for multipart chats */
    private List<User> users;

    /** The current color all msesages are sent with */
    private String currentColor;
    /** If this JPanel is visible in the GUI currently or not. */
    private Boolean isVisible;
    /** The current global enryption type */
    private String currentEncryptionType;
    /** Contains all the keys, each key corresponding to a type. <type,key>  */
    private HashMap<String, String> encryptionKeys;

    /**
     * Basic constructor.
     * @param user The user this ChatPane should belong to
     * @param clientSocket The socket with which to communicate.
     */
    public ChatPane(User user, Socket clientSocket) {
        this.user = user;
        this.clientSocket = clientSocket;
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
     * Adds a new message in the ChatWindow, callable from Backend when a
     * new message is received from the corresponding socket.
     * @param msg The message to be added to the feed. 
     */
    public void addMessage(Message msg) {

    }

    public void showEncryptionSelector() {

    }

    public void showColorPicker() {
        
    }

    /**
     * Handles all the button presses. For instance SendButton
     * would invoke a Transcriber.composeMessage() and then send
     * the composed message to the socket immediately. 
     * @param e The ActionEvent. Not used.
     */
    public void actionPerformed(ActionEvent e) {

    }

}