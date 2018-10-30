/**
 * Basic class encapsulating information from a file transfer query
 */
public class FileRequest extends Query {
    private String fileName;
    private int fileSize;

    /**
     * Generic encapsulation constructor.
     * @param message The message to be encapsulated.
     * @param fileName The name of the file to be transferred.
     * @param fileSize The size of the file to be transferred.
     */
    public FileRequest(String message, String fileName, int fileSize) {
        super(message);
        this.fileName = fileName;
        this.fileSize = fileSize;
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
}