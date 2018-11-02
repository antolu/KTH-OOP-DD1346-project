import java.nio.charset.Charset;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.String;
import javax.xml.bind.DatatypeConverter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  
import java.net.InetSocketAddress;

/**
 * Translates incoming messages in byte or string form to a fully readable
 * message incapsulated in a Message object.
 */
public class Transcriber {
    private static final String errorMsg = "Incorrectly formatted message. Content omitted.";
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Converts a byte array to a human readable string
     * @param bytes The input data to be converted to a string
     * @return The converted string.
     */
    public static String byteToString(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }

    /**
     * Converts a string to a byte array.
     * @param string The input data to be converted into a byte array.
     * @return The converted byte array.
     */
    public static byte[] stringToByte(String string) {
        return string.getBytes(Charset.forName("UTF-8"));
    }

    /**
     * Converts a byte array to a hexcode formatted string.
     * @param bytes The input data to be converted to hex.
     * @return The converted string.
     */
    public static String byteToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    /**
     * Converts a hex formatted string to a byte array.
     * @param hex The hex string to be converted.
     * @return The converted byte array.
     */
    public static byte[] hexToByte(String hex) {
        return DatatypeConverter.parseHexBinary(hex);
    }
    
    /**
     * Parses the received message and converts from string to object Message
     * @param String msg, the received xml message 
     * @return Message, the received message parsed
     */
    public static Query parse(String msg, SocketClient socket) {
        msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + msg;
        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            Element message = doc.getDocumentElement();
            if (message.getTagName() == "message") {
                if (((Element) message.getChildNodes().item(0)).getTagName().equals("filerequest"))
                    return parseFileRequest((Element) message.getChildNodes().item(0), socket);
                else if (((Element) message.getChildNodes().item(0)).getTagName().equals("fileresponse"))
                    return parseFileResponse((Element) message.getChildNodes().item(0));
                else if (((Element) message.getChildNodes().item(0)).getTagName().equals("disconnect"))
                    return new Query("<disconnect />");
                return parseMessage(message);
            }
            else if (message.getTagName() == "request") {
                if (message.hasAttribute("reply")) {
                    return parseRequestResponse(message);
                }
                return parseRequest(message);
            }
            else if (message.getTagName() == "keyrequest") {
                return parseKeyRequest(message);
            }
            return new Message(errorMsg);
        } catch(Exception e) {
            e.printStackTrace();
            return new Message(errorMsg);
        }
    }

    private static Message parseMessage(Element rootElement) {
        Element text = (Element) rootElement.getElementsByTagName("text").item(0);
        String textMessage = "";

        if (text.getElementsByTagName("encrypted").item(0) != null) {
            NodeList childNodes = text.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Element node = (Element) childNodes.item(i);
                String encryptionType = node.getAttribute("type");
                String encryptionKey = node.getAttribute("key");
                textMessage += byteToString(Encrypter.decrypt(encryptionType, encryptionKey, hexToByte(node.getTextContent())));
            }
        }
        else {
            textMessage = text.getTextContent();
        }

        String color = text.getAttribute("color");
        String time = dtf.format(LocalDateTime.now());

        return new Message(decodeHTML(textMessage), color, time, rootElement.getAttribute("name"));
    }

    private static Request parseRequest(Element rootElement) {
        String textMessage = rootElement.getTextContent();
        String name = rootElement.getAttribute("name");

        return new Request(decodeHTML(textMessage), name);
    }

    private static RequestResponse parseRequestResponse(Element rootElement) {
        String reply = rootElement.getAttribute("reply");
        String name = rootElement.getAttribute("name");

        return new RequestResponse(name, reply);
    }

    private static KeyRequest parseKeyRequest(Element rootElement) {
        String textMessage = rootElement.getTextContent();
        String encryptionType = rootElement.getAttribute("type");

        return new KeyRequest(decodeHTML(textMessage), encryptionType);
        // <keyrequest type="">Something</keyrequest>
    }

    private static FileRequest parseFileRequest(Element rootElement, SocketClient socket) {
        String textMessage = decodeHTML(rootElement.getTextContent());
        String filesize = rootElement.getAttribute("size");
        String filename = rootElement.getAttribute("name");
        String port = rootElement.getAttribute("port");
        String encryptionType = rootElement.getAttribute("type");
        String encryptionKey = rootElement.getAttribute("key");

        return new FileRequest(textMessage, filename, filesize, socket.getSocketID(), port, encryptionType, encryptionKey);
        // Handle the file request, <filerequest name="" size="" type="" key=""></filerequest>
    }

    private static FileResponse parseFileResponse(Element rootElement) {
        String textMessage = decodeHTML(rootElement.getTextContent());
        String reply = rootElement.getAttribute("reply");

        if (reply.equals("")) {
            reply = "no";
        }

        return new FileResponse(textMessage, reply);
        // Handle it. <fileresponse reply="" ></fileresponse>
    }
    
    /**
     * Turns HTML names into symbols
     * @param String input, received message with HTML names 
     * @return String, message to be displayed
     */
    private static String decodeHTML(String input) {
        input.replaceAll("&quot", "\"");
        input.replaceAll("&amp", "&");
        input.replaceAll("&lt", "<");
        input.replaceAll("&gt", ">");
        return input;
    }
    
    /**
     * Turns symbols into HTML names in a message to be sent
     * @param String input, message written in GUI
     * @return String, message to be sent with HTML names
     */
    public static String encodeHTML(String input) {
        input.replaceAll("\"", "&quot");
        input.replaceAll("&", "&amp");
        input.replaceAll("<", "&lt");
        input.replaceAll(">", "&gt");
        return input;
    }

    /**
     * Constructs a message with the appropriate xml-tags
     * @param msg, string with the content of the message to be sent
     * @param color, string with the desired color of the message
     * @param encryptionType, string with the desired encryption type, if any
     * @param encryptionKey, string with the key to the selected crypt
     * @return String, message to be sent with all needed xml-tags
     */
    public static String composeMessage(String msg, String color, String encryptionType, String encryptionKey) {
        return msg;
    }
}