import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.util.logging.Level;

/**
 * Created by Kai on 22.04.2017.
 */
public class DataVisualizationControl {

    @FXML
    private Button ShowConnectionWindowButton;

    private static final String CONNECTION_FXML = "gui/connection_window.fxml";

    /**
     * Opens a new Stage where COM-Port Connection is handled.
     * Closes this one.
     */
    @FXML
    private void btnShowConnectionWindow(){
        try{
            Stage connectionStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource(CONNECTION_FXML));
            Scene scene = new Scene(root, 600, 600);
            scene.getStylesheets().add("darkTheme.css");
            connectionStage.setTitle("COM-Port Selection");
            connectionStage.setScene(scene);
            connectionStage.setMinWidth(600);
            connectionStage.setMinHeight(600);
            connectionStage.show();

            //CLOSE this one.
            Stage thisStage = (Stage) ShowConnectionWindowButton.getScene().getWindow();
            thisStage.close();
        }catch (Exception ex){
            Main.logger.log(Level.WARNING, "Failed to load: " + CONNECTION_FXML);
        }
    }
}
