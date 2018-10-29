import java.util.List;

import javax.swing.JEditorPane;

import org.w3c.dom.Document;

/**
 * Customized JEditorPane that can display incoming chat messages
 * as a generated HTML page.
 */
public class ChatWindow extends JEditorPane {
    /** The page to be displayed */
    private Document messagesPage;

    /** A list of all the received messages */
    private List<Message> messages;

    /**
     * Appends a new message to the internal messages list, and updates the 
     * internal HTML page with the new message, and displays it.
     * @param who Who the message corresponds to. Me or you.
     * @param msg The message to be added.
     */
    public void addMessage(String who, Message msg) {

    }
}