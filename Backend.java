import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Backend implements ActionListener {
    private static final int MAX_CONNECTIONS = 3;

    private JPanel menuBar;
    private JTabbedPane tabbedPane;
    private ServerSocket serverSocket;
    private List<Socket> socketList;
    private List<User> userList;
    private HashMap chatMap;

    public Backend(Struct info) {

    }

    private void addConnection(Socket socket, String connectionRequest) {

    }

    public update(Object caller, Object arg) {

    }

    public void actionPerformed(ActionEvent e) {

    }

    private void showConnectionRequest(String connectionRequest) {

    }

    private void showFileRequest(String requestMessage) {

    }

    private void newConnection() {
        
    }

    private void disconnect(Socket socket) {
        
    }
}