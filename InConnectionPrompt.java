import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays a popup window when a new incoming connection is detected. 
 * Asks user if the new connection is to be accepted or not.
 */
public class InConnectionPrompt extends JPanel implements ActionListener {
    private JFrame frame;
    private JButton acceptButton;
    private JButton denyButton;

    private Backend backend;
    private User user;

    /**
     * Creates and displays the GUI
     * @param message The message the other user sent with the request.
     * @param user The user who send the request.
     * @param backend The backend. Used to process the answer of the user.
     */
    public InConnectionPrompt(String message, User user, Backend backend) {
        this.backend = backend;
        this.user = user;

        /* Create and format pretty buttons */
        acceptButton = new JButton();
        denyButton = new JButton();

        acceptButton.setText("Accept");
        denyButton.setText("Deny");

        acceptButton.setPreferredSize(new Dimension(80, 30));
        denyButton.setPreferredSize(new Dimension(80, 30));

        acceptButton.addActionListener(this);
        denyButton.addActionListener(this);

        /* Format the layout */
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setPreferredSize(new Dimension(200, 50));
        buttonPanel.add(acceptButton);
        buttonPanel.add(denyButton);

        /* Display some text and the message */
        JLabel text = new JLabel();
        text.setText("<html>" + user.getName() + " wants to connect to you. Hen says: <br> " + message + " <br> Do you accept? </html>");
        text.setPreferredSize(new Dimension(220, 100));

        /*Actually display the GUI */
        frame = new JFrame("New incoming connection");

        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(280, 160));
        add(text);
        add(buttonPanel);

        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Called when a button is pressed. Adds a connection if
     * accept is pressed, else removes it
     * @param e Generic ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        /* Hide the popup */
        frame.setVisible(false);
        frame.dispose();

        /* Process button presses */
        if (e.getSource() == acceptButton) {
            backend.addConnectionAsServer(user);
        } else if (e.getSource() == denyButton) {
            /* Send a response to the other user */
            user.getClientSocket().send(Composer.composeRequestReply(backend.getMyName(), "no"));
            
            /* Close the socket */
            user.getClientSocket().close();
        }

    }
}