
/**
 * Base class for all types of incoming messages. Encapsulates information.
 */
public class Query {
    private String message;
    private String originalMessage = "";

    /**
     * Constructs only a simple message with a string.
     */
    public Query(String message) {
        this.message = message;
    }

    public Query(String message, String originalMessage) {
        this(message);
        this.originalMessage = originalMessage;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param originalMessage the originalMessage to set
     */
    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    /**
     * @return the originalMessage
     */
    public String getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Query))
            return false;
        return ((Query) obj).toString().equals(message);
    }
}