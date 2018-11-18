import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;

/**
 * The menu bar in the chat client.
 */
public class MenuBar extends JPanel {
    private JButton changeNameButton;
    private JButton disconnectButton;
    private JButton newConnectionButton;

    private Backend backend;

    /**
     * Constructor.
     * @param backend The backend. Used to process all button presses.
     */
    public MenuBar(Backend backend) {
        super(new FlowLayout());

        this.backend = backend;

        createGUI();
        addActionListeners();
    }

    /**
     * Creates the GUi
     */
    private void createGUI() {
        /* Creates some buttons for format them */
        changeNameButton = new JButton();
        disconnectButton = new JButton();
        newConnectionButton = new JButton();

        changeNameButton.setText("Change name");
        disconnectButton.setText("Disconnect all connections");
        newConnectionButton.setText("New connection");

        changeNameButton.setPreferredSize(new Dimension(200, 30));
        disconnectButton.setPreferredSize(new Dimension(200, 30));
        newConnectionButton.setPreferredSize(new Dimension(200, 30));

        disconnectButton.setEnabled(false); // Nothing to disconnect

        /* Set the layout of the menubar */
        setLayout(new FlowLayout(FlowLayout.LEFT));

        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        setPreferredSize(new Dimension(760, 44));
        add(newConnectionButton, BorderLayout.WEST);
        add(changeNameButton, BorderLayout.WEST);
        add(disconnectButton, BorderLayout.WEST);
    }

    /**
     * Creates action listeners for every button and
     * multithreads a popup window for each.
     */
    private void addActionListeners() {
        changeNameButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new ChangeNamePrompt(backend);
                    }
                });
            }
        });

        newConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new OutConnectionPrompt(backend);
                    }
                });
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* Show confirmation before closing all open connections */
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are You Sure to disconnect all sockets?", 
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    backend.disconnectAll();
                }
            }
        });
    }

    /**
     * Enables disconnect button (if there actually exist connections that can be disconnected)
     */
    public void enableDisconnectButton() {
        disconnectButton.setEnabled(true);
    }

    /** Disables the disconnect butotn */
    public void disableDisconnectButton() {
        disconnectButton.setEnabled(false);
    }
}