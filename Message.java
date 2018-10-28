public class Message {
    private String message;
    private String color = "";
    private String time;
    private String type = "msg";
    private Boolean containsStyling = false;

    public Message(String message) {
        this.message = message;
    }

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
}