import java.net.ServerSocket;

/** Basic struct class to encapsulate initial information from 
 * StartFrame to the chat backend.
 */
public class ourStruct {
    private int port;
    private String name;
    private Boolean isServer;
    private ServerSocket serverSocket;

    /**
     * Basic encapsulation constructor.
     * @param port The port where the server socket is bound to.
     * @param name  The name of the user.
     * @param isServer If this chat is a server or a client.
     * @param serverSocket The initialized server socket.
     */
    public ourStruct(int port, String name, Boolean isServer, ServerSocket serverSocket) {
        this.port = port;
        this.name = name;
        this.isServer = isServer;
        this.serverSocket = serverSocket; 
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the isServer
     */
    public Boolean getIsServer() {
        return isServer;
    }
    
    /**
     * @return the serverSocket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}