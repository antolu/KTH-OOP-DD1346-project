/**
 * Basic class encapsulating the response of a file transfer query.
 */
public class FileResponse extends Query {
    private String reply;

    /**
     * Generic encapsulation constructor.
     * @param message The message to be encapsulated.
     * @param reply The file transfer query response to be encapsulated.
     */
    public FileResponse(String message, String reply) {
        super(message);
        this.reply = reply;
    }

    /**
     * @return the reply
     */
    public String getReply() {
        return reply;
    }
}