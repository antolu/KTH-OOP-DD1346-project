import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.lang.NumberFormatException;
import java.io.IOException;

import layout.SpringUtilities;

/**
 * Creates and displays a GUI allowing the user to open up a new connection. 
 * Prompts for IP adress, port and an optional message.
 */
public class OutConnectionPrompt extends JPanel implements ActionListener {
    private JButton okButton;
    private JFrame frame;
    private Backend backend;
    private JTextField IPField;
    private JTextField portField;
    private JTextField messageField;
    
    /**
     * Creates and displays the GUI
     * @param backend The backend. Used to actually open up a new connection 
     * with.
     */
    public OutConnectionPrompt(Backend backend) {
        this.backend = backend;

        /* Create and format JPanels */
        JPanel fieldPanel = new JPanel(new SpringLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        /* OK button */
        okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(this);

        /* Create some text fields and format them */ 
        IPField = new JTextField();
        portField = new JTextField();
        messageField = new JTextField();

        IPField.setPreferredSize(new Dimension(200, 30));
        portField.setPreferredSize(new Dimension(200, 30));
        messageField.setPreferredSize(new Dimension(200, 50));

        /* Create some labels for each text field */
        JLabel IPLabel = new JLabel();
        JLabel portLabel = new JLabel();
        JLabel messageLabel = new JLabel();

        IPLabel.setText("IP adress of the host");
        portLabel.setText("Port of the host");
        messageLabel.setText("Send a greeting");

        IPLabel.setLabelFor(IPField);
        portLabel.setLabelFor(portField);
        messageLabel.setLabelFor(messageField);

        /* Add all objects and format them nicely */
        fieldPanel.add(IPLabel);
        fieldPanel.add(IPField);
        fieldPanel.add(portLabel);
        fieldPanel.add(portField);
        fieldPanel.add(messageLabel);
        fieldPanel.add(messageField);
        fieldPanel.add(okButton);
        buttonPanel.add(okButton);
        add(fieldPanel);
        add(buttonPanel);

        setPreferredSize(new Dimension(400, 200));

        /* Format layout into a grid laayout */
        SpringUtilities.makeCompactGrid(fieldPanel,
                                3, 2, //rows, cols
                                6, 6,        //initX, initY
                                6, 6);       //xPad, yPad

        /* ACtually display everything */
        frame = new JFrame();
        frame.getContentPane().add(this);
        
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Generic actionPerformed. Parses all the entered information after the
     * ok button has been pressed. Checks for errors and displays them if 
     * required.
     * @param e Generic ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        /* Initialize error flags */
        Boolean hasErrors = false; 
        String errorMsg = "";

        /* Get IP */
        String IPString = IPField.getText();
        if (IPString.equals("")) {
            hasErrors = true;
            errorMsg += "The IP adress field cannot be left empty <br>";
        }

        /* Check if IP is actually a valid IP */
        InetAddress IP = null;
        try {
            IP = InetAddress.getByName(IPString);
        }
        catch (UnknownHostException ex) {
            hasErrors = true;
            errorMsg += "The entered IP adress " + IPString + " is not a valid IP adress. <br>";
        }

        /* Get port */
        int port = 0;
        String portString = portField.getText();

        /* Check if port is a valid port / integer */
        try {
            port = Integer.parseInt(portString);
        } 
        catch (NumberFormatException ex) {
            hasErrors = true;
            errorMsg += "The entered port " + portString + " is not a valid port. <br>";
        }

        /* Do not allow to connect to oneself */
        if ((IPString.equals("127.0.0.1") || IPString.equals("localhost")) && port == backend.getPort()) {
            hasErrors = true;
            errorMsg += "You cannot connect to yourself!";
        }

        /* Display errors if they exist. */
        if (hasErrors) {
            errorMsg = "<html>" + errorMsg + "</html>";
            JOptionPane.showMessageDialog(frame, errorMsg);
            return;
        }

        String message = messageField.getText();

        frame.setVisible(false);

        /* Try and open up a new connection */
        try {
            backend.newClientConnection(IP, port, message);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "<html>A connection to " + IPField.getText() + ":" + portField.getText() + " could not be established. Try again later</html>");
            frame.setVisible(true);
            return;
        }

        frame.dispose();
    }    
}