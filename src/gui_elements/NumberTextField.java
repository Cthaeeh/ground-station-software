package gui_elements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import main.Main;

import java.util.logging.Level;

/**
 * Custom TextField for Numbers only.
 * //TODO add heavy unit tests so that it never fails.
 */
public class NumberTextField extends TextField {

    public NumberTextField() {
        super();

        addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!isValid(getText())) {
                event.consume();
            }
        });

        textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!isValid(newValue)) {
                setText(oldValue);
            }
        });
    }

    private boolean isValid(final String value) {
        if (value.length() == 0 || value.equals("-")) {
            return true;
        }

        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public int getInteger() {
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            Main.programLogger.log(Level.WARNING,()->"Error parsing int (" + getText() + ") from field.");
            return 0;
        }
    }

}