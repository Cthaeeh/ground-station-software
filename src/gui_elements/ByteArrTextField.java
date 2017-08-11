package gui_elements;

import javafx.scene.control.TextField;

/**
 * Created by kai on 7/2/17.
 *
 * //TODO implement this and use it...
 */
public class ByteArrTextField extends TextField{

    @Override
    public void replaceText(int start, int end, String text)
    {
        if (validate(text))
        {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text)
    {
        if (validate(text))
        {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text)
    {
        return text.matches("[0-9 ]+");
    }
}
