
/**
 * Encapsulates the information of an incoming message compactly
 * and makes the information easily accessible by other methods.
 */
public class Message {
    private String message;
    private String color = "000000";
    private String time;
    private String type = "msg";
    private Boolean containsStyling = false;

    /**
     * Constructs only a simple message with a string.
     */
    public Message(String message) {
        this.message = message;
    }

    /**
     * Constructs a message with text and color of text.
     * @param message The text message.
     * @param color The color of the message.
     */
    public Message(String message, String color) {
        this(message);
        this.color = color;
    }

    public Message(String message, String color, String type, Boolean containsStyling) {
        this(message, color);
        this.type = type;
        this.containsStyling = containsStyling;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the containsStyling
     */
    public Boolean getContainsStyling() {
        return containsStyling;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Message))
            return false;
        return ((Message) obj).toString().equals(message);
    }

    @Override
    public int hashCode() {
        return time.hashCode() * message.hashCode();
    }
}