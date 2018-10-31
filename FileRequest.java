/**
 * Basic class encapsulating information from a file transfer query
 */
public class FileRequest extends Query {
    private String fileName;
    private int fileSize;
    private String encryptionType;
    private String encryptionKey;

    /**
     * Generic encapsulation constructor.
     * @param message The message to be encapsulated.
     * @param fileName The name of the file to be transferred.
     * @param fileSize The size of the file to be transferred.
     */
    public FileRequest(String message, String fileName, int fileSize, String encryptionType, String encryptionKey) {
        super(message);
        this.fileName = fileName;
        this.fileSize = fileSize;
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
    public int getFileSize() {
        return fileSize;
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