import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.awt.Dimension;

/**
 * Customized JEditorPane that can display incoming chat messages as a generated
 * HTML page.
 */
public class ChatWindow extends JEditorPane {
    /** The page to be displayed */
    private Document messagesPage;
    private Element body;
    private Element table;
    private Transformer transformer;

    /** A list of all the received messages */
    private List<Message> messages;

    public ChatWindow(User otherUser) {
        try {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        messagesPage = dBuilder.newDocument();

        /* Create all HTML elements */
        Element html = messagesPage.createElement("html");
        body = messagesPage.createElement("body");
        Element tr = messagesPage.createElement("tr");
        Element leftCol = messagesPage.createElement("td");
        Element messageCol = messagesPage.createElement("td");
        Element rightCol = messagesPage.createElement("td");

        /* Create table to display messages in */
        table = messagesPage.createElement("table");
        table.setAttribute("style", "width:100%");

        /* Set formatting of table */
        leftCol.setAttribute("width", "15%");
        messageCol.setAttribute("width", "70%");
        rightCol.setAttribute("width", "15%");

        leftCol.setTextContent("***");
        messageCol.setTextContent(otherUser.getName() + " is now connected.");
        rightCol.setTextContent("***");

        /* Add all HTML elements to document */
        messagesPage.appendChild(html);
        // Element htmlNode = messagesPage.getDocumentElement();
        html.appendChild(body);
        body.appendChild(table);
        table.appendChild(tr);
        tr.appendChild(leftCol);
        tr.appendChild(messageCol);
        tr.appendChild(rightCol);

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

        String HTML = getHTMLAsString();

        setPreferredSize(new Dimension(600, 400));

        setEditorKit(new HTMLEditorKit());
        setText(HTML);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends a new message to the internal messages list, and updates the internal
     * HTML page with the new message, and displays it.
     * 
     * @param who Who the message corresponds to. Me or you.
     * @param msg The message to be added.
     */
    public void addMessage(Message msg) {
        addTableElement(msg.getUsername(), msg.getMessage(), "", msg.getColor());
        refreshWindow();
    }

    /**
     * Adds a new message that the local user sent.
     * 
     * @param msg The message to be added.
     */
    public void sentMessage(Message msg) {
        /* Add new element to table */
        addTableElement("", msg.getMessage(), msg.getUsername() + "\n" + msg.getTime(), msg.getColor());
        refreshWindow();
    }

    private void addTableElement(String left, String message, String right, String color) {
        Element tr = messagesPage.createElement("tr");
        Element leftCol = messagesPage.createElement("td");
        Element messageCol = messagesPage.createElement("td");
        Element colorContent = messagesPage.createElement("font");
        Element rightCol = messagesPage.createElement("td");

        colorContent.setAttribute("color", color);
        colorContent.setTextContent(message);

        leftCol.setTextContent(left);
        messageCol.appendChild(colorContent);
        rightCol.setTextContent(right);

        tr.appendChild(leftCol);
        tr.appendChild(messageCol);
        tr.appendChild(rightCol);

        table.appendChild(tr);
    }

    private void refreshWindow() {
        String newPage = getHTMLAsString();
        setText(newPage);
    }

    private String getHTMLAsString() {
        try {
            /* Convert document to human readable string */
            StringWriter writer = new StringWriter();
            DOMSource domSource = new DOMSource(messagesPage);
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);
            System.out.println(writer.toString());
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return "";
        }
    }
}