import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.lang.String;

public class ColorChooser extends JPanel implements ChangeListener {
    private JColorChooser colorChooser;
    private ChatPane chatPane;

    public ColorChooser(ChatPane chatPane) {
        this.chatPane = chatPane;

        colorChooser = new JColorChooser();
        colorChooser.getSelectionModel().addChangeListener(this);

        add(colorChooser);
        
        JOptionPane optionpane = new JOptionPane(this);
        JDialog dialog = optionpane.createDialog("Color Picker");

        dialog.setVisible(true);
    }

    public void stateChanged(ChangeEvent e) {
        Color newColor = colorChooser.getColor();
        String currentColor = String.format("%02X%02X%02X", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
        chatPane.updateColor(currentColor);
    }
}