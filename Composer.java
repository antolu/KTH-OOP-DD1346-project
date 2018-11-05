import java.lang.StringBuilder;
import java.lang.String;

/**
 * Convenience class with static methods to compose all required messages
 */
public class Composer {
    /**
     * Composes a connection request.
     * @param message A message.
     * @param name The name of the user sending the request.
     * @return The composed message.
     */
    public static String composeRequest(String message, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("<request");
        if (!name.equals("")) {
            sb.append(" name=\"");
            sb.append(name);
            sb.append("\"");
        }
        sb.append(">");
        sb.append(Transcriber.encodeHTML(message));
        sb.append("</request>");

        return sb.toString();
    }

    /**
     * Composes the reply to a connection request
     * @param name The name of the user sending the reply.
     * @param reply The reply (yes/no)
     * @return The composed message.
     */
    public static String composeRequestReply(String name, String reply) {
        StringBuilder sb = new StringBuilder();
        sb.append("<request");
            sb.append(" reply=\"");
            sb.append(reply);
            sb.append("\"");

            sb.append(" name=\"");
            sb.append(name);
            sb.append("\"");
        sb.append(">");
        sb.append("</request>");

        return sb.toString();
    }

    /**
     * Composes a text message.
     * @param message The message.
     * @param color The color of the message. 
     * @param encryptionType The encryption type of the message (if applicable).
     * @param encryptionKey The encryption key of the message (if applicable).
     * @param name The name of the user sending the message.
     * @return The composed message.
     */
    public static String composeMessage(String message, String color, String encryptionType, String encryptionKey, String name) {
        StringBuilder sb = new StringBuilder();
        /* <message> */
        sb.append("<message");
        if (!color.equals("")) {
            sb.append(" name=\"");
            sb.append(name);
            sb.append("\"");
        }
        sb.append(">");

        /* <text> */
        sb.append("<text");
        if (!color.equals("")) {
            sb.append(" color=\"");
            sb.append(color);
            sb.append("\"");
        }
        sb.append(">");

        /* <encrypted> */
        if (!encryptionType.equals("")) {
            sb.append("<encrypted type=\"");
            sb.append(encryptionType);
            sb.append("\"");
            sb.append(" key=\"");
            sb.append(encryptionKey);
            sb.append("\">");
            sb.append(Transcriber.byteToHex(Encrypter.encrypt(encryptionType, encryptionType, Transcriber.stringToByte(Transcriber.encodeHTML(message)))));
            sb.append("</encrypted>");
        }
        else {
            sb.append(Transcriber.encodeHTML(message));
        }
        sb.append("</text>");
        sb.append("</message>");

        return sb.toString();
    }

    /**
     * Composes a text message with an extra multipart flag.
     * @param message The message.
     * @param color The color of the message. 
     * @param encryptionType The encryption type of the message (if applicable).
     * @param encryptionKey The encryption key of the message (if applicable).
     * @param name The name of the user sending the message.
     * @param serverclient Whether the sender is a server or a client in the 
     * multipart conversation.
     * @return The composed message.
     */
    public static String composeMultiPartMessage(String message, String color, String encryptionType, String encryptionKey, String name, String serverclient) {
        StringBuilder sb = new StringBuilder();
        /* <message> */
        sb.append("<message");
        if (!color.equals("")) {
            sb.append(" name=\"");
            sb.append(name);
            sb.append("\"");
        }
        sb.append(" multipart=\"");
        sb.append(serverclient);
        sb.append("\"");
        sb.append(">");

        /* <text> */
        sb.append("<text");
        if (!color.equals("")) {
            sb.append(" color=\"");
            sb.append(color);
            sb.append("\"");
        }
        sb.append(">");

        /* <encrypted> */
        if (!encryptionType.equals("")) {
            sb.append("<encrypted type=\"");
            sb.append(encryptionType);
            sb.append("\"");
            sb.append(" key=\"");
            sb.append(encryptionKey);
            sb.append("\">");
            sb.append(Transcriber.byteToHex(Encrypter.encrypt(encryptionType, encryptionType, Transcriber.stringToByte(Transcriber.encodeHTML(message)))));
            sb.append("</encrypted>");
        }
        else {
            sb.append(Transcriber.encodeHTML(message));
        }
        sb.append("</text>");
        sb.append("</message>");

        return sb.toString();
    }

    /**
     * Composes a file request.
     * @param message A message.
     * @param filename The name of the file.
     * @param filesize The size of the file.
     * @param port The port where the connection should be made to.
     * @param encryptionType The type of the encryption, if the file is to be encrypted.
     * @param encryptionKey The key of the encryption, if the file is to be encrypted.
     * @return The composed message.
     */
    public static String composeFileRequest(String message, String filename, String filesize, String port, String encryptionType, String encryptionKey) {
        StringBuilder sb = new StringBuilder();

        /* <message> */
        sb.append("<message>");

        /* <filerequest> */
        sb.append("<filerequest");

        sb.append(" filename=\"");
        sb.append(filename);
        sb.append("\"");

        sb.append(" filesize=\"");
        sb.append(filesize);
        sb.append("\"");

        sb.append(" port=\"");
        sb.append(filesize);
        sb.append("\"");

        if (!encryptionType.equals("")) {
            sb.append(" type=\"");
            sb.append(encryptionType);
            sb.append("\"");
            sb.append(" key=\"");
            sb.append(encryptionKey);
            sb.append("\"");
        }
        sb.append(">");
        sb.append(Transcriber.encodeHTML(message));
        sb.append("</filerequest>");
        sb.append("</message>");

        return sb.toString();
    }

    /**
     * Composes the response to a file request.
     * @param message A message.
     * @param reply The reply (yes/no)
     * @return The composed message.
     */
    public static String composeFileResponse(String message, String reply) {
        StringBuilder sb = new StringBuilder();
        sb.append("<message>");

        /* <fileresponse> */
        sb.append("<fileresponse reply=\"");
        sb.append(reply);
        sb.append("\"");
        sb.append(">");
        sb.append(Transcriber.encodeHTML(message));
        sb.append("</fileresponse>");

        sb.append("</message>");

        return sb.toString();
    }

    /**
     * Composes a key request message.
     * @param message A message.
     * @param type The type of encryption the key is wished for.
     * @return The composed message.
     */
    public static String composeKeyRequest(String message, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("<message>");

        /* <keyrequest> */
        sb.append("<keyrequest type=\"");
        sb.append(type);
        sb.append("\"");
        sb.append(">");
        sb.append(Transcriber.encodeHTML(message));
        sb.append("</keyrequest>");

        sb.append("</message>");

        return sb.toString();
    }

    /**
     * Composes the response to a key request.
     * @param key The key to the response.
     * @return The composed message.
     */
    public static String composeKeyRequestResponse(String key) {
        StringBuilder sb = new StringBuilder();
        sb.append("<message>");

        /* <keyrequest> */
        sb.append("<keyrequest key=\"");
        sb.append(key);
        sb.append("\"");
        sb.append(">");
        sb.append("</keyrequest>");

        sb.append("</message>");

        return sb.toString();
    }

    public static String getDisconnectMessage() {
        return "<message><disconnect /></message>";
    }
}