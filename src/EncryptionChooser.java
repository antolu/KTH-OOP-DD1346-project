import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import java.util.List;
import java.util.Base64;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.lang.Integer;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.awt.Dimension;

/**
 * Displays a popup window allowing the user to select an encryption type, 
 * and a key if applicable.
 */
public class EncryptionChooser extends JPanel implements ActionListener {
    private ChatPane chatPane;
    private JFrame frame;
    private JTextField keyField;
    private JButton okButton;
    private List<JRadioButton> radioButtons;
    
    /**
     * Creates and displays the GUI 
     * @param chatPane The parent ChatPane object. Used to set encryption after
     * it has been selected.
     */
    public EncryptionChooser(ChatPane chatPane) {
        this.chatPane = chatPane;

        /* Set layout */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(150, Encrypter.SUPPORTED_ENCRYPTIONS.length * 50 + 100));

        /* Add some form of caption */
        JLabel label = new JLabel();
        label.setText("<html><br>Enter encryption key if Caesar is to be selected.<br></html>");

        /* Text field to enter keyf or caesar encryption */
        keyField = new JTextField();
        keyField.setPreferredSize(new Dimension(150, 30));

        add(label);
        add(keyField);

        /* Create selectable button */
        radioButtons = new ArrayList<>();
        
        for (String enc: Encrypter.SUPPORTED_ENCRYPTIONS) {
            radioButtons.add(new JRadioButton(enc));
        }

        ButtonGroup buttonGroup = new ButtonGroup();

        for (JRadioButton button: radioButtons) {
            buttonGroup.add(button);
            add(button);
        }

        /* Add a an ok button to set encryption */
        okButton = new JButton();
        okButton.setText("OK");
        okButton.setPreferredSize(new Dimension(60, 30));
        okButton.addActionListener(this);

        add(okButton);

        /* Display GUI */
        frame = new JFrame("Encryption chooser"); 

        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Standard actionPerformed. Sets encryption when OK button is pressed.
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            JButton source = (JButton) e.getSource();

            // frame.setVisible(false);

            for (JRadioButton button : radioButtons) {
                if (button.isSelected() && button.getText().equals("AES")) {
                    try {
                        /* Generate key */
                        KeyGenerator kgen = KeyGenerator.getInstance("AES");
                        kgen.init(128);
                        SecretKey aesKey = kgen.generateKey();
                        String encodedKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());
            
                        /* Set encryption in chatPane */
                        chatPane.updateEncryption("AES", encodedKey);
                    } catch (NoSuchAlgorithmException ex) {
                        // Do nothing / will not fail
                    }
                    frame.dispose();
                    return;
                }
                else if (button.isSelected() && button.getText().equals("Caesar")) {
                    String key = keyField.getText();

                    /* Check if there is actually a key entered */
                    if (key.equals("")) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid key.");
                        frame.setVisible(true);
                        return;
                    }

                    /* Check if actually an integer */
                    try {
                        Integer.parseInt(key);
                    } catch (NumberFormatException ex) {
                        /* Show error */
                        JOptionPane.showMessageDialog(frame, "The entered encryption key " + key + " is not an integer.");
                        frame.setVisible(true);
                        return;
                    }
        
                    /* Set encryption in chatPane */
                    chatPane.updateEncryption("caesar", key);
                    frame.dispose();
                    return;
                }
                // else if (button.isSelected() && button.getText().equals("RSA")) {
                //     System.err.println("RSA selected.");
                // }
                // else if (button.isSelected() && button.getText().equals("Blowfish")) {
                //     System.err.println("Blowfish selected.");
                // }
            }
        }
    }
}