package main;

import data.DataModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import serial.SerialPortComm;
import visualization.VisualizationControl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Kai on 22.04.2017.
 */
public class MainWindowControl implements Initializable{

    //TODO put this Visualizations managment / visualizationsGridPane stuff elsewhere.
    private DataModel model ;
    private SerialPortComm serialPortComm;
    /**
     * To keep references to the VisualizationControl .
     * This way we can tell the corresponding Controller if a Node gets deleted (removed);
     */
    private Map<Node,VisualizationControl> visualizationsMap = new HashMap<>();

    @FXML
    private Button showConnectionWindowBtn;

    @FXML
    private GridPane visualizationsGridPane;

    @FXML
    private StatusAreaControl statusAreaController;

    @FXML
    private TeleCommandControl teleCommandController;

    private int numOfColumns = 1;
    private int numOfRows = 1;
    private static final int MAX_NUMBER_OF_COLUMNS = 3;
    private static final int MAX_NUMBER_OF_ROWS = 3;

    private static final String CONNECTION_FXML = "../gui/connection_window.fxml";
    private static final String VISUALIZATION_ELEMENT_FXML = "../gui/visualization.fxml";
    private static final String CONFIG_EDIT_FXML = "../gui/config_edit.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeContextMenu();
    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
        serialPortComm = new SerialPortComm(model);
        //Add one visualization Element
        visualizationsGridPane.add(createVisualizationElement(),0,0);
        //give model to sub controllers
        teleCommandController.initModel(model);
        teleCommandController.initSerialPortComm(serialPortComm);
        statusAreaController.initSerialPortComm(serialPortComm);
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

        visualizationsGridPane.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(visualizationsGridPane, event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void addRow() {
        if(numOfRows >= MAX_NUMBER_OF_ROWS) return;
        Node[] newElements = new Node[numOfColumns];
        for (int i = 0; i < numOfColumns; i++) {
            newElements[i] = createVisualizationElement();
        }
        visualizationsGridPane.addRow(numOfRows++,newElements);
    }

    private void addCol() {
        if(numOfColumns >= MAX_NUMBER_OF_COLUMNS) return;
        Node[] newElements = new Node[numOfRows];
        for (int i = 0; i < numOfRows; i++) {
            newElements[i] = createVisualizationElement();
        }
        visualizationsGridPane.addColumn(numOfColumns++,newElements);
    }

    /**
     * Removes a row from the visualizationsGridPane.
     * Credits: http://stackoverflow.com/questions/40516514/remove-a-row-from-a-gridpane
     * This answer could be interesting if you want to remove not the row with the biggest index
     * but instead a specific one.
     */
    private void removeRow() {
        if(numOfRows <= 1) return;                              //Minimum 1 element.
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : visualizationsGridPane.getChildren()) {
            // get index from child
            int rowIndex = GridPane.getRowIndex(child) == null ? 0 : GridPane.getRowIndex(child);

            if(rowIndex ==  (numOfRows-1)){
                deleteNodes.add(child);
                visualizationsMap.get(child).dispose();
            }
        }
        numOfRows--;
        visualizationsGridPane.getChildren().removeAll(deleteNodes);    // remove nodes from row
    }

    /**
     * Removes a column from the visualizationsGridPane.
     * Credits: http://stackoverflow.com/questions/40516514/remove-a-row-from-a-gridpane
     * This answer could be interesting if you want to remove not the column with the biggest index
     * but instead a specific one.
     */
    private void removeCol() {
        if(numOfColumns <= 1) return;                              //Minimum 1 element.
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : visualizationsGridPane.getChildren()) {
            // get index from child
            int columnIndex = GridPane.getColumnIndex(child) == null ? 0 : GridPane.getColumnIndex(child);

            if(columnIndex ==  (numOfColumns-1)){
                deleteNodes.add(child);
                visualizationsMap.get(child).dispose();
            }
        }
        numOfColumns--;
        visualizationsGridPane.getChildren().removeAll(deleteNodes);    // remove nodes from row
    }

    /**
     * Creates a visualization element, a gui element that lets you choose a data source and then the according data
     * as a graph or in another form.
     * @return
     */
    private Node createVisualizationElement() {
        try {
            FXMLLoader elementLoader = new FXMLLoader(getClass().getResource(VISUALIZATION_ELEMENT_FXML));
            Node newNode = elementLoader.load();
            VisualizationControl visualizationControl = elementLoader.getController();
            visualizationControl.initModel(model);
            visualizationsMap.put(newNode,visualizationControl);
            return newNode;
        } catch (IOException e) {
            Main.programLogger.log(Level.WARNING,"Failed to load visualization element");
            return new Label("Failed to load : " + VISUALIZATION_ELEMENT_FXML);
        }
    }

    /**
     * Opens a new Stage where COM-Port Connection is handled.
     * Closes this one.
     */
    @FXML
    private void btnShowConnectionWindow(){
        try{
            final Stage connectionStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CONNECTION_FXML));
            Scene scene = new Scene(loader.load(), 600, 300);

            ConnectionWindowControl connectionWindowControl = loader.getController();
            connectionWindowControl.initCommPortConnection(serialPortComm);

            scene.getStylesheets().add("gui/darkTheme.css");

            connectionStage.initModality(Modality.APPLICATION_MODAL);
            connectionStage.initOwner(showConnectionWindowBtn.getScene().getWindow());
            connectionStage.setTitle("COM-Port Selection");
            connectionStage.setScene(scene);
            connectionStage.setResizable(false);
            connectionStage.show();

        }catch (Exception ex){
            Main.programLogger.log(Level.WARNING, "Failed to load: " + CONNECTION_FXML);
        }
    }

    @FXML
    private void btnDataInterpretationClick(){
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(visualizationsGridPane.getScene().getWindow());
        if (file != null) {
            try {
                model.loadConfigData(file);
            } catch (IOException exc) {
                //TODO handle exception...
            }
        }
    }

    @FXML
    private void btnCreateConfigClick() {
        try{
            final Stage configEditStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CONFIG_EDIT_FXML));
            Scene scene = new Scene(loader.load(), 600, 600);

            ConfigEditControl configEditControl = loader.getController();
            scene.getStylesheets().add("gui/darkTheme.css");

            configEditStage.initModality(Modality.NONE);
            configEditStage.initOwner(showConnectionWindowBtn.getScene().getWindow());
            configEditStage.setTitle("Configuration Editor");
            configEditStage.setScene(scene);
            configEditStage.setMinWidth(600);
            configEditStage.setMinHeight(600);
            configEditStage.show();

        }catch (Exception ex){
            Main.programLogger.log(Level.WARNING, "Failed to load: " + CONFIG_EDIT_FXML);
        }
    }
}
