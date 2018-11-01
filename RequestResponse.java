/**
 * Basic class encapsulating the response of a connection request.
 */
public class RequestResponse extends Query {
    private String reply;
    private String name;

    /**
     * Generic encapsulation constructor.
     * @param message The message to be encapsulated.
     * @param reply The file transfer query response to be encapsulated.
     */
    public RequestResponse(String name, String reply) {
        super("");
        this.name = name;
        this.reply = reply;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the reply
     */
    public String getReply() {
        return reply;
    }
}