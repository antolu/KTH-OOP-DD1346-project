/**
 * Basic class encapsulating a chat message and formatting.
 */
public class Message extends Query {
    private String color = "000000";
    private String time;

    /**
     * Constructs a message without color.
     * @param message The text message. 
     */
    public Message(String message) {
        super(message);
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

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Message))
            return false;
        return ((Message) obj).getMessage().equals(getMessage());
    }
}