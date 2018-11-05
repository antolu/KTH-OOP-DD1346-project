import javax.swing.JTextField;

import java.util.Observable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Displays a GUI prompting the user for name and port to start server on.
 */
public class StartFrame extends Observable implements ActionListener {
    private JFrame frame;
    private JTextField portField;
    private JButton okButton;
    private JTextField nameField;

    private String name;
    private int port;
    private ServerSocket serverSocket;

    /**
     * Creates and displays the GUI
     */
    public StartFrame() {
        /* Create some text fields the user can write text to */
        portField = new JTextField();
        portField.setPreferredSize(new Dimension(100, 25));
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(100, 25));

        /* Create OK button */
        okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(this);

        /* Add some descriptive text to the popup  */
        JLabel nameLabel = new JLabel("<html>Please enter your name.</html>");
        JLabel portLabel = new JLabel("<html>Please enter the port number where a server socket should be opened</html>");

        /* Actually display the GUI (and some formatting) */
        frame = new JFrame();
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.setPreferredSize(new Dimension(200, 150));

        contentPane.add(nameLabel);
        contentPane.add(nameField);
        contentPane.add(portLabel);
        contentPane.add(portField);
        contentPane.add(okButton);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Called when the OK butotn is pressed. If all fields are valid
     * the chat will be started using Main.
     * @param e Generic ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        name = nameField.getText();

        /* Check if entered port is actually an integer */
        try {
            port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "The specified port " + port + " is not an integer.");
            return;
        }

        /* Try to bind a to a port */
        try {
            serverSocket = new ServerSocket(port);
        } catch(IOException ex) {
            ex.printStackTrace();
            showError(port);
            return;
        }

        frame.dispose();

        /* Actually start the chat */
        Main.startMainFrame(port, name, serverSocket);
    }

    /**
     * Displays an error if could not bind to port.
     * @param port The port that could not be bound to.
     */
    private void showError(int port) {
        JOptionPane.showMessageDialog(frame, "The specified port " + port + " is already in use. Please select another and try again.");
    }
}