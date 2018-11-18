/**
 * Basic class encapsulating a query regarding encryption key request.
 */
public class KeyRequest extends Query {
    private String type;
    
    /**
     * Generic encapsulation constructor.
     * @param message The message to be encapsulated.
     * @param type The type of the encryption key queried.
     */
    public KeyRequest(String message, String type) {
        super(message);
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
}