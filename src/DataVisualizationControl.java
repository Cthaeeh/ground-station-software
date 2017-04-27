import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by Kai on 22.04.2017.
 */
public class DataVisualizationControl implements Initializable{

    @FXML
    private Button ShowConnectionWindowButton;

    @FXML
    private GridPane ChartsGridPane;

    private int numOfColumns = 1;
    private int numOfRows = 1;
    private final int maxNumberOfColumns = 3;
    private final int maxNumberOfRows = 3;

    private static final String CONNECTION_FXML = "gui/connection_window.fxml";
    private static final String VISUALIZATION_ELEMENT_FXML = "gui/visualization_element.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeContextMenu();
    }

    private void initializeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();        //Create a ContextMenu for the ChartGrid, so that you can add and remove columns.

        MenuItem addRow = new MenuItem("Add row");
        MenuItem addCol = new MenuItem("Add column");
        MenuItem removeRow = new MenuItem("Remove row");
        MenuItem removeCol = new MenuItem("Remove column");

        addRow.setOnAction(e -> addRow());
        addCol.setOnAction(e -> addCol());
        removeRow.setOnAction(e -> removeRow());
        removeCol.setOnAction(e -> removeCol());

        contextMenu.getItems().addAll(addRow, addCol, removeRow, removeCol);

        ChartsGridPane.add(createVisualizationElement(),0,0);

        ChartsGridPane.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(ChartsGridPane, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * Adds a Row to the ChartsGridPane
     */
    private void addRow() {
        if(numOfRows >= maxNumberOfRows) return;
        Node[] newElements = new Node[numOfColumns];
        for (int i = 0; i < numOfColumns; i++) {
            newElements[i] = createVisualizationElement();
        }
        ChartsGridPane.addRow(numOfRows++,newElements);
    }

    /**
     * Adds a Column to the ChartsGridPane
     */
    private void addCol() {
        if(numOfColumns >= maxNumberOfColumns) return;
        Node[] newElements = new Node[numOfRows];
        for (int i = 0; i < numOfRows; i++) {
            newElements[i] = createVisualizationElement();         //TODO replace dummy elements by real elements.
        }
        ChartsGridPane.addColumn(numOfColumns++,newElements);
    }

    /**
     * Removes a row from the ChartsGridPane.
     * Credits: http://stackoverflow.com/questions/40516514/remove-a-row-from-a-gridpane
     * This answer could be interesting if you want to remove not the row with the biggest index
     * but instead a specific one.
     */
    private void removeRow() {
        if(numOfRows <= 1) return;                              //Minimum 1 element.
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : ChartsGridPane.getChildren()) {
            // get index from child
            int rowIndex = GridPane.getRowIndex(child) == null ? 0 : GridPane.getRowIndex(child);

            if(rowIndex ==  (numOfRows-1)){
                deleteNodes.add(child);
            }
        }
        numOfRows--;
        ChartsGridPane.getChildren().removeAll(deleteNodes);    // remove nodes from row
    }

    /**
     * Removes a column from the ChartsGridPane.
     * Credits: http://stackoverflow.com/questions/40516514/remove-a-row-from-a-gridpane
     * This answer could be interesting if you want to remove not the column with the biggest index
     * but instead a specific one.
     */
    private void removeCol() {
        if(numOfColumns <= 1) return;                              //Minimum 1 element.
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : ChartsGridPane.getChildren()) {
            // get index from child
            int columnIndex = GridPane.getColumnIndex(child) == null ? 0 : GridPane.getColumnIndex(child);

            if(columnIndex ==  (numOfColumns-1)){
                deleteNodes.add(child);
            }
        }
        numOfColumns--;
        ChartsGridPane.getChildren().removeAll(deleteNodes);    // remove nodes from row
    }

    /**
     * Creates a visualization element, a gui element that lets you choose a data source and then the according data
     * as a graph or in another form.
     * @return
     */
    private Node createVisualizationElement() {
        try {
            return FXMLLoader.load(getClass().getResource(VISUALIZATION_ELEMENT_FXML));
        } catch (IOException e) {
            Main.logger.log(Level.WARNING,"Failed to load visualization element");
            return new Label("Failed to load : " + VISUALIZATION_ELEMENT_FXML);
        }
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
            scene.getStylesheets().add("gui/darkTheme.css");
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
