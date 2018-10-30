
/**
 * Base class for all types of incoming messages. Encapsulates information.
 */
public class Query {
    private String message;

    /**
     * Constructs only a simple message with a string.
     */
    public Query(String message) {
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
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