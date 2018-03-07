package visualization;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ToolBar;

import java.io.IOException;

/**
 * Created by kai on 3/7/18.
 */
public class SearchBarControl extends ToolBar {

    @FXML
    private TextField searchField;

    public SearchBarControl(){
         FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                 .getResource("/gui/search_bar.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void show(){
        searchField.requestFocus();
    }

    public TextField getSearchField() {
        return searchField;
    }
}
