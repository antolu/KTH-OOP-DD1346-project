/**
 * Basic class encapsulating a chat message and formatting.
 */
public class Message extends Query {
    private String color = "000000";
    private String time;
    private String username = "";

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
     * @param username Who sent this message.
     */
    public Message(String message, String color, String time, String username) {
        this(message);
        this.color = color;
        this.time = time;
        this.username = username;
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

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
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