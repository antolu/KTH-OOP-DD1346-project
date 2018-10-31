import javax.swing.*;   

public class ToggleableButton extends JButton
{
    private boolean isPressed = false;

    private String notPressedString;
    private String isPressedString;

    public ToggleableButton(String text1, String text2)
    {
        notPressedString = text1;
        isPressedString = text2;
        setText(text1);
    }

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

    public boolean getState() {
        return isPressed;
    }
}