/**
 * Basic class encapsulating information from a file transfer query
 */
public class FileRequest extends Query {
    private String fileName;
    private String fileSize;
    private String IP;
    private String port;
    private String encryptionType;
    private String encryptionKey;

    /**
     * Generic encapsulation constructor.
     * @param message The message to be encapsulated.
     * @param fileName The name of the file to be transferred.
     * @param fileSize The size of the file to be transferred.
     */
    public FileRequest(String message, String fileName, String fileSize, String IP,
                       String port, String encryptionType, String encryptionKey) {
        super(message);
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.IP = IP;
        this.port = port;
        this.encryptionType = encryptionType;
        this.encryptionKey = encryptionKey;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the fileSize
     */
    public String getFileSize() {
        return fileSize;
    }

    /**
     * @return the IP
     */
    public String getIP() {
        return IP;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @return the encryptionType
     */
    public String getEncryptionType() {
        return encryptionType;
    }

    /**
     * @return the encryptionKey
     */
    public String getEncryptionKey() {
        return encryptionKey;
    }

    public Boolean isEncrypted() {
        return !encryptionType.equals("");
    }
}