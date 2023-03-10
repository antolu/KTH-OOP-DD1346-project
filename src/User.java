import java.net.Socket;

/**
 * Basic class to store information for each connected user.
 */
public class User {
    /** The name of the user */
    private String name;

    /** The IP adress and port of the user, used as UUID */
    private String ID;

    /** The socket with which to communicate with the user */
    private SocketClient clientSocket;

    public User(String name, String ID) {
        if (name.equals(""))    
            name = ID;
        else 
            this.name = name;
        this.ID = ID;
    }

    /**
     * Basic encapsulation constructor.
     * @param name The name of the user.
     * @param ID The IP adress and port of the user, used as UUID.
     * @param clientSocket The socket with which to communicate with the user
     */
    public User(String name, String ID, SocketClient clientSocket) {
        this(name, ID);
        this.clientSocket = clientSocket;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the iD
     */
    public String getID() {
        return ID;
    }

    /**
     * @return the clientSocket
     */
    public SocketClient getClientSocket() {
        return clientSocket;
    }

    @Override
    public String toString() {
        if (name == "")
            return ID;
        else
            return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;
        return ((User) obj).getID().equals(ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}