import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

import java.util.Base64;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

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
        // try {
        //     return new String(bytes, "UTF-8");
        // } catch (Exception e)
        // {
        //     return "";
        // }
        return new String(bytes, StandardCharsets.UTF_8);
        // return Base64.getEncoder().encodeToString(bytes);
        // return Base64.encodeBase64String(bytes);
    }

    /**
     * Converts a string to a byte array.
     * @param string The input data to be converted into a byte array.
     * @return The converted byte array.
     */
    public static byte[] stringToByte(String string) {
        // try {
        //     return string.getBytes("UTF-8");
        // } catch (Exception e) {
        //     return null;
        // }
        return string.getBytes(StandardCharsets.UTF_8);
        // return Base64.getDecoder().decode(string);
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
        msg = unescapeHtml4(msg);
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
                else if (message.getAttribute("multipart").equals("start"))
                    return new Query("<multipartstart />");
                return parseMessage(doc, message);
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

    /**
     * Parses a message as a text message.
     * @param origMessage The original message, to be included in the 
     * generated Message object.
     * @param rootElement The root element of the message document.
     * @return Returns a Message object with all the information available
     * for the message. 
     */
    private static Message parseMessage(Document origMessage, Element rootElement) {
        Element text = (Element) rootElement.getElementsByTagName("text").item(0);
        String textMessage = "";

        /* If there are encrypted elements in the message, decrypt and append */
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

        /* Retrieve other information from the code */
        String color = text.getAttribute("color");
        String time = dtf.format(LocalDateTime.now());

        Message msg = new Message(unescapeHtml4(textMessage), color, time, rootElement.getAttribute("name"));

        /* If is a multipart message, swap client/server attribute and
         * add to the message
         */
        if (rootElement.hasAttribute("multipart")) {
            String multipartAttribute = rootElement.getAttribute("multipart");
            msg.setMultipartMode(multipartAttribute);

            if (multipartAttribute.equals("client")) {
                rootElement.setAttribute("multipart", "server");
            }
            else if (multipartAttribute.equals("server")) {
                rootElement.setAttribute("multipart", "client");
            }
            msg.setOriginalMessage(getXML(origMessage));
        }

        return msg;
    }

    /**
     * Parses a message as a connection request. 
     * @param rootElement The root element of the XML message.
     * @return A Request object with all available information encapsulated.
     */
    private static Request parseRequest(Element rootElement) {
        String textMessage = rootElement.getTextContent();
        String name = rootElement.getAttribute("name");

        return new Request(unescapeHtml4(textMessage), name);
    }

    /**
     * Parses a message as a response to a connection request.
     * @param rootElement The root element of the XML message.
     * @return A RequestResponse object with the reply.
     */
    private static RequestResponse parseRequestResponse(Element rootElement) {
        String reply = rootElement.getAttribute("reply");
        String name = rootElement.getAttribute("name");

        return new RequestResponse(name, reply);
    }

    /**
     * Parses a message as an encryption key request.
     * @param rootElement The root element of the XML message.
     * @return A KeyRequest object with the message and encryption type 
     * encapsulated.
     */
    private static KeyRequest parseKeyRequest(Element rootElement) {
        String textMessage = rootElement.getTextContent();
        String encryptionType = rootElement.getAttribute("type");

        return new KeyRequest(unescapeHtml4(textMessage), encryptionType);
        // <keyrequest type="">Something</keyrequest>
    }

    /**
     * Parses a message as a file request. 
     * @param rootElement The root element of the XML message.
     * @param socket The socket where the message originated from.
     * @return A FileRequest object with all available information encapsulated.
     */
    private static FileRequest parseFileRequest(Element rootElement, SocketClient socket) {
        String textMessage = unescapeHtml4(rootElement.getTextContent());
        String filesize = rootElement.getAttribute("size");
        String filename = rootElement.getAttribute("name");
        String port = rootElement.getAttribute("port");
        String encryptionType = rootElement.getAttribute("type");
        String encryptionKey = rootElement.getAttribute("key");

        return new FileRequest(textMessage, filename, filesize, socket.getSocketID(), port, encryptionType, encryptionKey);
        // Handle the file request, <filerequest name="" size="" type="" key=""></filerequest>
    }

    /**
     * Parses a message as a file transfer response.
     * @param rootElement The root element of the XML message.
     * @return A FileResponse object with reply encapsulated.
     */
    private static FileResponse parseFileResponse(Element rootElement) {
        String textMessage = unescapeHtml4(rootElement.getTextContent());
        String reply = rootElement.getAttribute("reply");

        if (reply.equals("")) {
            reply = "no";
        }

        return new FileResponse(textMessage, reply);
        // Handle it. <fileresponse reply="" ></fileresponse>
    }

    /**
     * Converts a Document to human readable string (one line XML).
     * @param doc The document to be interpreted.
     * @return A string representation of the XML document.
     */
    private static String getXML(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}