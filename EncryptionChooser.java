import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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
    JTextField textField;
    
    public EncryptionChooser(ChatPane chatPane) {
        this.chatPane = chatPane;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(150, 80));

        JLabel label = new JLabel();
        label.setText("<html>Enter encryption key if Caesar is to be selected.</html>");

        textField = new JTextField();

        add(label);
        add(textField);

        List<JRadioButton> radioButtons = new ArrayList<>();
        
        for (String enc: Encrypter.SUPPORTED_ENCRYPTIONS) {
            radioButtons.add(new JRadioButton(enc));
        }

        ButtonGroup buttonGroup = new ButtonGroup();

        for (JRadioButton button: radioButtons) {
            buttonGroup.add(button);
            add(button);
            button.addActionListener(this);
        }

        JOptionPane optionpane = new JOptionPane(this);
        JDialog dialog = optionpane.createDialog("Set encryption options");

        dialog.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        JRadioButton source = (JRadioButton) e.getSource();

        if (source.getText().equals("AES")) {
            /* Generate key */
            try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            SecretKey aesKey = kgen.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());

            textField.setText(encodedKey);

            chatPane.updateEncryption("AES", encodedKey);

            return;
            } catch (NoSuchAlgorithmException ex) {
                return;
            }
        }
        else if (source.getText().equals("Caesar")) {
            System.err.println("Caesar selected.");
            
            String key = textField.getText();
            try { // Check if key is actually an integer
                Integer.parseInt(key);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The entered encryption key " + key + " is not an integer.");
                return;
            }
            if (key.equals("")) { // Check if there is actually a key entered.
                JOptionPane.showMessageDialog(this, "Please enter a valid key.");
                return;
            }

            chatPane.updateEncryption("caesar", key);
            return;
        }
        else if (source.getText().equals("RSA")) {
            System.err.println("RSA selected.");
        }
        else if (source.getText().equals("Blowfish")) {
            System.err.println("Blowfish selected.");
        }
        else {
            System.out.println(source.getText());
        }
    }
}