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
            
            return new Message(doc.getElementsByTagName("message").item(0).getTextContent());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new Message("");
    }

    private static String interpretInlineXML(String input) {
        return input;
    }
}