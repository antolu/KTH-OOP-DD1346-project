/**
 * Basic class encapsulating information from a new connection request.
 */
public class Request extends Query {
    private String name;

    /**
     * Generic encapsulation coinstructor
     * @param message The connection request message.
     * @param name The name of the user that sent the request.
     */
    public Request(String message, String name) {
        super(message);
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}