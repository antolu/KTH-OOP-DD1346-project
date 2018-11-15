/**
 * Basic class encapsulating a query regarding encryption key request response.
 */
public class KeyRequestResponse extends Query {
    private String key;

    /**
     * Generic encapsulation constructor.
     * @param key The key of the encryption type queried.
     */
    public KeyRequestResponse(String key) {
        super("KeyResponse");
        this.key = key;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }
}