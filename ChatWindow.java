import java.util.List;

import javax.swing.JEditorPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/**
 * Customized JEditorPane that can display incoming chat messages
 * as a generated HTML page.
 */
public class ChatWindow extends JEditorPane {
    /** The page to be displayed */
    private Document messagesPage;
    private Element body;
    private Transformer transformer;

    /** A list of all the received messages */
    private List<Message> messages;

    public ChatWindow() throws ParserConfigurationException, TransformerConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        messagesPage = dBuilder.newDocument();
        Element html = messagesPage.createElement("html");
        // body = html.createElement("body");

        /* Create transformer for outputting */
        TransformerFactory tFactory = TransformerFactory.newInstance();
        transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        DOMImplementation domImpl = dBuilder.getDOMImplementation();
        DocumentType doctype = domImpl.createDocumentType("doctype", "html", "hello");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

        try {
            StringWriter writer = new StringWriter();
            DOMSource domSource = new DOMSource(messagesPage);
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);
            System.out.println(writer.toString());
        } catch(TransformerException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Appends a new message to the internal messages list, and updates the 
     * internal HTML page with the new message, and displays it.
     * @param who Who the message corresponds to. Me or you.
     * @param msg The message to be added.
     */
    public void addMessage(String who, Message msg) {

    }

    /**
     * Adds a new message that the local user sent.
     * @param msg The message to be added.
     */
    public void addMessage(Message msg) {

    }
}