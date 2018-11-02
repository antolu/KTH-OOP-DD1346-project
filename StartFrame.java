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

public class StartFrame extends Observable implements ActionListener {
    private JFrame frame;
    private JTextField portField;
    private JButton okButton;
    private JTextField nameField;

    private String name;
    private int port;
    private ServerSocket serverSocket;

    public StartFrame() {
        
        portField = new JTextField();
        portField.setPreferredSize(new Dimension(100, 25));
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(100, 25));
        okButton = new JButton();
        
        okButton.setText("OK");

        /* Add some descriptive text to the popup  */
        JLabel nameLabel = new JLabel("<html>Please enter your name.</html>");
        JLabel portLabel = new JLabel("<html>Please enter the port number where a server socket should be opened</html>");

        frame = new JFrame();
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.setPreferredSize(new Dimension(200, 150));

        okButton.addActionListener(this);

        contentPane.add(nameLabel);
        contentPane.add(nameField);
        contentPane.add(portLabel);
        contentPane.add(portField);
        contentPane.add(okButton);

        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        name = nameField.getText();

        try {
            port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "The specified port " + port + " is not an integer.");
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
        } catch(IOException ex) {
            ex.printStackTrace();
            showError(port);
            return;
        }

        frame.dispose();

        System.out.println("Name: " + name);
        System.out.println("Port: " + port);

        // Return socket to parent
        Main.startMainFrame(port, name, serverSocket);
    }

    private void showError(int port) {
        JOptionPane.showMessageDialog(frame, "The specified port " + port + " is already in use. Please select another and try again.");
    }
}