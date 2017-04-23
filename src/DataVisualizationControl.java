import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Created by Kai on 22.04.2017.
 */
public class DataVisualizationControl implements Initializable{

    @FXML
    private Button ShowConnectionWindowButton;

    @FXML
    private GridPane ChartsGridPane;

    private static final String CONNECTION_FXML = "gui/connection_window.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeChartGrid();
    }

    private void initializeChartGrid() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addRow = new MenuItem("Add row");
        MenuItem addCol = new MenuItem("Add column");
        MenuItem removeRow = new MenuItem("Remove row");
        MenuItem removeCol = new MenuItem("Remove column");
        contextMenu.getItems().addAll(addRow, addCol, removeRow, removeCol);

        //TODO implement
        addRow.setOnAction(event -> System.out.println(" cut "));
        addCol.setOnAction(event -> System.out.println(" cut "));
        removeRow.setOnAction(event -> System.out.println(" cut "));
        removeCol.setOnAction(event -> System.out.println(" cut "));

        ChartsGridPane.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(ChartsGridPane, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * Opens a new Stage where COM-Port Connection is handled.
     * Closes this one.
     */
    @FXML
    private void onShowConnectionWindow(){
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
