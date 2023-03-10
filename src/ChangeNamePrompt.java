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

/* Prompt enabling the user to change name */
public class ChangeNamePrompt extends JPanel implements ActionListener {
    private JButton okButton;
    private JFrame frame;
    private Backend backend;
    private JTextField nameField;
    
    /**
     * Creates and displays GUI.
     * @param backend The backend. Used to change name once it has been entered.
     */
    public ChangeNamePrompt(Backend backend) {
        this.backend = backend;

        /* Some panels for pretty formatting */
        JPanel fieldPanel = new JPanel(new SpringLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        /* Create OK button */
        okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(this);

        /* Create some text fields and format them */ 
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 20));

        /* Create some labels for each text field */
        JLabel nameLabel = new JLabel();
        nameLabel.setText("<html>Your new name. Leave empty to use your local IP address as name.</html>");
        nameLabel.setLabelFor(nameField);
        nameLabel.setPreferredSize(new Dimension(200, 40));

        fieldPanel.add(nameLabel);
        fieldPanel.add(nameField);
        fieldPanel.add(okButton);
        buttonPanel.add(okButton);
        add(fieldPanel);
        add(buttonPanel);

        setPreferredSize(new Dimension(440, 110));

        /* Pretty grid layout */
        SpringUtilities.makeCompactGrid(fieldPanel,
                                1, 2, //rows, cols
                                6, 6,        //initX, initY
                                6, 6);       //xPad, yPad

        /* Actually display the GUI */
        frame = new JFrame();
        frame.getContentPane().add(this);
        
        frame.pack();
        frame.setVisible(true);
    }

    /** 
     * Gets the name when entered and sets it in backend 
     * @param e Generic ActionEvent.
     * */
    public void actionPerformed(ActionEvent e) {
        String newName = nameField.getText();

        frame.dispose();

        backend.updateName(newName);
    }    
}