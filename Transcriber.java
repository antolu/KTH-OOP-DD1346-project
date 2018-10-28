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

public class Transcriber {
    private static final String errorMsg = "Incorrectly formatted message. Content omitted.";

    public static String byteToString(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public static byte[] stringToByte(String string) {
        return string.getBytes(Charset.forName("UTF-8"));
    }

    public static String byteToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    public static byte[] hexToByte(String hex) {
        return DatatypeConverter.parseHexBinary(hex);
    }

    public static Message parse(String msg) {
        msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + msg;
        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            Element message = doc.getDocumentElement();
            if (message.getTagName() == "message") {
                Element text = (Element) message.getElementsByTagName("text").item(0);
                if (text.getElementsByTagName("encrypted").item(0) != null) {
                    // Handle the encryption, <encrpyted type ="" key =""></encrypted>
                }
                String color = text.getAttribute("color");
                return new Message(text.getTextContent(), color);
            }
            else if (message.getTagName() == "request") {
                // YAY a new connection! <request>Hello!</reply>
            }
            else if (message.getTagName() == "keyrequest") {
                // <keyrequest type="">Something</keyrequest>
            }
            else if (message.getTagName() == "filerequest") {
                // Handle the file request, <filerequest name="" size="" type="" key=""></filerequest>
            }
            else if (message.getTagName() == "fileresponse") {
                // Handle it. <fileresponse reply="" port=""></fileresponse>
            }
            // Also need to handle <message><disconnect /></message>
            return new Message(errorMsg);
        } catch(Exception e) {
            e.printStackTrace();
            return new Message(errorMsg);
        }
    }

    private static String decodeHTML(String input) {
        return input;
    }

    private static String encodeHTML(String input) {
        return input;
    }

    public static String composeMessage(String msg, String color, String encryptionType, String encryptionKey) {
        return msg;
    }
}