import javax.swing.*;   

/**
 * Basic toggleable button
 */
public class ToggleableButton extends JButton
{
    private boolean isPressed = false;

    private String notPressedString;
    private String isPressedString;

    /**
     * Creates a toggleable button with some text.
     * @param text1 Text to display when button is not pressed.
     * @param text2 Text to display when button is pressed.
     */
    public ToggleableButton(String text1, String text2)
    {
        notPressedString = text1;
        isPressedString = text2;
        setText(text1);
    }

    /**
     * Toggles the state of the button
     */
    public void toggleState()
    {
        if (!isPressed)
        {
            setText(isPressedString);
        }
        else
        {
            setText(notPressedString);
        }

        isPressed = !isPressed;
    }

    /**
     * Gets the state of the button
     * @return Returns true if button is in a pressed state, else false. 
     */
    public boolean getState() {
        return isPressed;
    }
}