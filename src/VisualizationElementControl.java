import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Created by Kai on 01.05.2017.
 */
public class VisualizationElementControl {

    @FXML
    Button selectDataSourceButton;

    private final String DATA_SOURCE_SELECTION_FXML = "gui/data_source_selection_dialog.fxml";

    @FXML
    private void btnSelectDataSourceClick(){
        try {
            final Stage dialog = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource(DATA_SOURCE_SELECTION_FXML));
            Scene scene = new Scene(root, 600, 600);
            scene.getStylesheets().add("gui/darkTheme.css");

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(selectDataSourceButton.getScene().getWindow());

            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
