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
import java.util.ArrayList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

import java.lang.Integer;
import java.security.NoSuchAlgorithmException;

/**
 * Displays a popup window allowing the user to ask about an encryption type,
 * and enter a message to send along with the key request.
 */
public class KeyRequestChooser extends JPanel implements ActionListener {
    private JFrame frame;
    private JTextField messageField;
    private JButton okButton;
    private List<JRadioButton> radioButtons;
    private User user;
    private boolean running;

    /**
     * Minimal constructor,
     * @param inUser the user that the request is later ent to
     */
    public KeyRequestChooser(User inUser) {
        user = inUser;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(150, Encrypter.SUPPORTED_ENCRYPTIONS.length * 50 + 100));

        JLabel label = new JLabel();
        label.setText("<html><br>Enter message to send along key request.<br></html>");

        /* Text field to enter message */
        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(150, 30));

        add(label);
        add(messageField);

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

        /* Add a an ok button to ask about encryption*/
        okButton = new JButton();
        okButton.setText("OK");
        okButton.setPreferredSize(new Dimension(60, 30));
        okButton.addActionListener(this);

        add(okButton);

        /* Display GUI */
        frame = new JFrame("Send key request");

        frame.add(this);
        frame.pack();

    }

    /**
     * Creates the GUI from witch the request is sent
     */
    public void createGUI(){
        running = false;
        /* Set layout */
        frame.setVisible(true);
    }

    /**
     * Standard actionPerformed. Composes a key request with parameters set
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {

            JButton source = (JButton) e.getSource();

            String type="";
            String sendMessage="";
            String composedRequest="";
            running = true;

            for (JRadioButton button : radioButtons) {
                if (button.isSelected()) {

                    type = button.getText();
                    sendMessage=messageField.getText();

                    frame.setVisible(false);

                    //Compose a key request and send
                    composedRequest=Composer.composeKeyRequest(sendMessage,type);
                    user.getClientSocket().send(composedRequest);

                    //Start thread and countdown
                    Thread t = new Thread(type){
                        public void run(){
                            int countDown = 60;
                            while(true){

                                if(countDown<=0){
                                    running = false;
                                    JFrame error = new JFrame("Error");

                                    JOptionPane.showMessageDialog(error, "Key request failed");
                                    return;
                                }

                                if(running==false){
                                    return;
                                }
                                countDown--;

                                try{
                                    sleep(1000);
                                }catch(InterruptedException e){
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    };

                    t.start();
                }
            }
        }
    }

    /**
     * Set the running status of the thread in the action performed
     * @param setRunning boolean
     */
    public void setRunningStatus(boolean inRunning){
        running = inRunning;
    }

    /**
     * Returns the status of the thread in the thread in the action performed
     * @return boolean of status
     */
    public boolean getRunningStatus(){
        return running;
    }

    public void answerReceived(){
        JFrame processed = new JFrame("Request processed");

        JOptionPane.showMessageDialog(processed, "Key request response received");
    }
}
