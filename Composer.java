import java.lang.StringBuilder;
import java.lang.String;

public class Composer {
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

    public static String getDisconnectMessage() {
        return "<message><disconnect /></message>";
    }
}