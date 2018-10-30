import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.awt.Dimension;

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

    public ChatWindow() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        messagesPage = dBuilder.newDocument();
        Element html = messagesPage.createElement("html");
        body = messagesPage.createElement("body");
        body.setTextContent("Yay!");

        messagesPage.appendChild(html);
        Element htmlNode = messagesPage.getDocumentElement();
        htmlNode.appendChild(body);

        /* Create transformer for outputting */
        TransformerFactory tFactory = TransformerFactory.newInstance();
        transformer = tFactory.newTransformer();

        /* Set formatting to HTML */
        DOMImplementation domImpl = dBuilder.getDOMImplementation();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        DocumentType doctype = domImpl.createDocumentType("doctype", "", "");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

        /* Convert document to human readable string */
        StringWriter writer = new StringWriter();
        DOMSource domSource = new DOMSource(messagesPage);
        StreamResult result = new StreamResult(writer);
        transformer.transform(domSource, result);
        System.out.println(writer.toString());

        setPreferredSize(new Dimension(400,300));

        setEditorKit(new HTMLEditorKit());
        setText(writer.toString());
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
        // Generate new HTML
        // setText(String the new html stream.toString())
    }
}