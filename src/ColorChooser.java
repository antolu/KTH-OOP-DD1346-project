import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.lang.String;

/**
 * Popup window allowing the user to choose a color for their chat messages.
 */
public class ColorChooser extends JPanel implements ChangeListener {
    private JColorChooser colorChooser;
    private ChatPane chatPane;

    /**
     * Creates and displays the GUI for color selection
     * @param chatPane The parent ChatPane. Used to set color after if has
     * been selected.
     */
    public ColorChooser(ChatPane chatPane) {
        this.chatPane = chatPane;

        /* Create swing color picker object */
        colorChooser = new JColorChooser();
        colorChooser.getSelectionModel().addChangeListener(this);

        add(colorChooser);
        
        /* Display color picker ina JDialog */
        JOptionPane optionpane = new JOptionPane(this);
        JDialog dialog = optionpane.createDialog("Color Picker");

        dialog.setVisible(true);
    }

    /**
     * Generic stateChanged. Sets color of text when it has been selected.
     * @param e Standard ChangeEvent.
     */
    public void stateChanged(ChangeEvent e) {
        Color newColor = colorChooser.getColor();

        /* Convert RGB to hex */
        String currentColor = String.format("%02X%02X%02X", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
        
        /* Update color in chat pane */
        chatPane.updateColor(currentColor);
    }
}