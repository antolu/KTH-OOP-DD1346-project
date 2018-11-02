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

public class EncryptionChooser extends JPanel implements ActionListener {
    private ChatPane chatPane;
    private JFrame frame;
    private JTextField keyField;
    private JButton okButton;
    private List<JRadioButton> radioButtons;
    
    public EncryptionChooser(ChatPane chatPane) {
        this.chatPane = chatPane;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // setLayout(new BorderLayout());
        setPreferredSize(new Dimension(150, Encrypter.SUPPORTED_ENCRYPTIONS.length * 50 + 100));

        /* ADd some form of caption */
        JLabel label = new JLabel();
        label.setText("<html><br>Enter encryption key if Caesar is to be selected.<br></html>");

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
            // button.addActionListener(this);
        }

        okButton = new JButton();
        okButton.setText("OK");
        okButton.setPreferredSize(new Dimension(60, 30));
        okButton.addActionListener(this);

        add(okButton);

        frame = new JFrame("Encryption chooser"); 

        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            JButton source = (JButton) e.getSource();

            // frame.setVisible(false);

            for (JRadioButton button : radioButtons) {
                if (button.isSelected() && button.getText().equals("AES")) {
                    /* Generate key */
                    try {
                        KeyGenerator kgen = KeyGenerator.getInstance("AES");
                        kgen.init(128);
                        SecretKey aesKey = kgen.generateKey();
                        String encodedKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());
            
                        keyField.setText(encodedKey);
            
                        chatPane.updateEncryption("AES", encodedKey);
                    } catch (NoSuchAlgorithmException ex) {
                    }
                    frame.dispose();
                    return;
                }
                else if (button.isSelected() && button.getText().equals("Caesar")) {
                    String key = keyField.getText();
                    try { // Check if key is actually an integer
                        Integer.parseInt(key);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "The entered encryption key " + key + " is not an integer.");
                        frame.setVisible(true);
                        return;
                    }
                    if (key.equals("")) { // Check if there is actually a key entered.
                        JOptionPane.showMessageDialog(frame, "Please enter a valid key.");
                        frame.setVisible(true);
                        return;
                    }
        
                    chatPane.updateEncryption("caesar", key);
                    frame.dispose();
                    return;
                }
                else if (button.isSelected() && button.getText().equals("RSA")) {
                    System.err.println("RSA selected.");
                }
                else if (button.isSelected() && button.getText().equals("Blowfish")) {
                    System.err.println("Blowfish selected.");
                }
            }
        }
    }
}