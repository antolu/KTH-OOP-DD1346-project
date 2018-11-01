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

import layout.SpringUtilities;

public class OutConnectionPrompt extends JPanel implements ActionListener {
    private JButton okButton;
    private JFrame frame;
    private Backend backend;
    private JTextField IPField;
    private JTextField portField;
    private JTextField messageField;
    
    public OutConnectionPrompt(Backend backend) {
        this.backend = backend;

        JPanel fieldPanel = new JPanel(new SpringLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

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

        SpringUtilities.makeCompactGrid(fieldPanel,
                                3, 2, //rows, cols
                                6, 6,        //initX, initY
                                6, 6);       //xPad, yPad

        frame = new JFrame();
        frame.getContentPane().add(this);
        
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Boolean hasErrors = false; 
        String errorMsg = "";

        /* Get IP */
        if (IPField.getText().equals("")) {
            hasErrors = true;
            errorMsg += "The IP adress field cannot be left empty <br>";
        }
        InetAddress IP = null;
        try {
            IP = InetAddress.getByName(IPField.getText());
        }
        catch (UnknownHostException ex) {
            hasErrors = true;
            errorMsg += "The entered IP adress " + IPField.getText() + " is not a valid IP adress. <br>";
        }

        /* Get port */
        int port = 0;
        try {
            port = Integer.parseInt(portField.getText());
        } 
        catch (NumberFormatException ex) {
            hasErrors = true;
            errorMsg += "The entered port " + portField.getText() + " is not a valid port. <br>";
        }

        if (hasErrors) {
            errorMsg = "<html>" + errorMsg + "</html>";
            JOptionPane.showMessageDialog(frame, errorMsg);
            return;
        }

        String message = messageField.getText();

        frame.dispose();

        backend.newConnection(IP, port, message);
    }    
}