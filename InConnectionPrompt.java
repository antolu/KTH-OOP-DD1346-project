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

public class InConnectionPrompt extends JPanel implements ActionListener {
    private JFrame frame;
    private JButton acceptButton;
    private JButton denyButton;

    private Backend backend;
    private User user;

    public InConnectionPrompt(String message, User user, Backend backend) {
        this.backend = backend;
        this.user = user;

        acceptButton = new JButton();
        denyButton = new JButton();

        acceptButton.setText("Accept");
        denyButton.setText("Deny");

        acceptButton.setPreferredSize(new Dimension(80, 30));
        denyButton.setPreferredSize(new Dimension(80, 30));

        acceptButton.addActionListener(this);
        denyButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setPreferredSize(new Dimension(200, 50));
        buttonPanel.add(acceptButton);
        buttonPanel.add(denyButton);

        JLabel text = new JLabel();
        text.setText("<html>" + user.getName() + " wants to connect to you. he says: <br> " + message + " <br> Do you accept? </html>");
        text.setMaximumSize(new Dimension(200, 100));
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        frame = new JFrame("New incoming connection");

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(220, 160));
        add(text);
        add(buttonPanel);

        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        frame.setVisible(false);
        frame.dispose();
        if (e.getSource() == acceptButton) {
            backend.addConnectionAsServer(user);
        } else if (e.getSource() == denyButton) {
            user.getClientSocket().send(Composer.composeRequestReply(backend.getMyName(), "no"));
            user.getClientSocket().close();
        }

    }
}