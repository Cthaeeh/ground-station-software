package main;

import command.TeleCommandControl;
import data.DataModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import serial.SerialPortComm;
import visualization.VisualizationControl;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Kai on 22.04.2017.
 */
public class MainWindowControl implements Initializable {

    //TODO put this Visualizations managment / visualizationsGridPane stuff elsewhere.
    private DataModel model;
    private SerialPortComm serialPortComm;
    /**
     * To keep references to the VisualizationControl .
     * This way we can tell the corresponding Controller if a Node gets deleted (removed);
     */
    private Map<Node, VisualizationControl> visualizationsMap = new HashMap<>();

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

    private static final String CONNECTION_FXML = "/gui/connection_window.fxml";
    private static final String VISUALIZATION_ELEMENT_FXML = "/gui/visualization.fxml";
    private static final String CONFIG_EDIT_FXML = "/gui/config_edit.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeContextMenu();
    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
        serialPortComm = new SerialPortComm(model);
        //Add one visualization Element
        visualizationsGridPane.add(createVisualizationElement(), 0, 0);
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
        if (numOfRows >= MAX_NUMBER_OF_ROWS) return;
        Node[] newElements = new Node[numOfColumns];
        for (int i = 0; i < numOfColumns; i++) {
            newElements[i] = createVisualizationElement();
        }
        visualizationsGridPane.addRow(numOfRows++, newElements);
    }

    private void addCol() {
        if (numOfColumns >= MAX_NUMBER_OF_COLUMNS) return;
        Node[] newElements = new Node[numOfRows];
        for (int i = 0; i < numOfRows; i++) {
            newElements[i] = createVisualizationElement();
        }
        visualizationsGridPane.addColumn(numOfColumns++, newElements);
    }

    /**
     * Removes a row from the visualizationsGridPane.
     * Credits: http://stackoverflow.com/questions/40516514/remove-a-row-from-a-gridpane
     * This answer could be interesting if you want to remove not the row with the biggest index
     * but instead a specific one.
     */
    private void removeRow() {
        if (numOfRows <= 1) return;                              //Minimum 1 element.
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : visualizationsGridPane.getChildren()) {
            // get index from child
            int rowIndex = GridPane.getRowIndex(child) == null ? 0 : GridPane.getRowIndex(child);

            if (rowIndex == (numOfRows - 1)) {
                deleteNodes.add(child);
                visualizationsMap.get(child).dispose();
                visualizationsMap.remove(child); //TODO is this really okay ?
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
        if (numOfColumns <= 1) return;                              //Minimum 1 element.
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : visualizationsGridPane.getChildren()) {
            // get index from child
            int columnIndex = GridPane.getColumnIndex(child) == null ? 0 : GridPane.getColumnIndex(child);

            if (columnIndex == (numOfColumns - 1)) {
                deleteNodes.add(child);
                visualizationsMap.get(child).dispose();
                visualizationsMap.remove(child);    //TODO is this really okay ?
            }
        }
        numOfColumns--;
        visualizationsGridPane.getChildren().removeAll(deleteNodes);    // remove nodes from row
    }

    /**
     * Creates a visualization element, a ressources element that lets you choose a data source and then the according data
     * as a graph or in another form.
     *
     * @return
     */
    private Node createVisualizationElement() {
        try {
            FXMLLoader elementLoader = new FXMLLoader(getClass().getResource(VISUALIZATION_ELEMENT_FXML));
            Node newNode = elementLoader.load();
            VisualizationControl visualizationControl = elementLoader.getController();
            visualizationControl.initModel(model);
            visualizationsMap.put(newNode, visualizationControl);
            return newNode;
        } catch (IOException e) {
            Main.programLogger.log(Level.WARNING, "Failed to load visualization element");
            return new Label("Failed to load : " + VISUALIZATION_ELEMENT_FXML);
        }
    }

    /**
     * Opens a new Stage where COM-Port Connection is handled.
     * Closes this one.
     */
    @FXML
    private void btnShowConnectionWindow() {
        try {
            final Stage connectionStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CONNECTION_FXML));
            Scene scene = new Scene(loader.load(), 600, 300);

            ConnectionWindowControl connectionWindowControl = loader.getController();
            connectionWindowControl.initCommPortConnection(serialPortComm);

            scene.getStylesheets().add("/gui/darkTheme.css");

            connectionStage.initModality(Modality.APPLICATION_MODAL);
            connectionStage.initOwner(showConnectionWindowBtn.getScene().getWindow());
            connectionStage.setTitle("COM-Port Selection");
            connectionStage.setScene(scene);
            connectionStage.setResizable(false);
            connectionStage.show();

        } catch (Exception ex) {
            Main.programLogger.log(Level.WARNING, "Failed to load: " + CONNECTION_FXML);
        }
    }

    //TODO split this up.
    @FXML
    private void btnLoadConfigClick() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(visualizationsGridPane.getScene().getWindow());
        if (file != null) {
            try {
                //TODO test it.
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will close all visualizations and stop the serial communication", ButtonType.OK, ButtonType.CANCEL);
                alert.getDialogPane().getStylesheets().add("/gui/darkTheme.css");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    serialPortComm.disconnect();
                    emptyAllVisualizations();
                    //TODO sorround with try catch
                    model.loadConfigData(file);
                }
            } catch (IOException exc) {
                Main.programLogger.log(Level.WARNING, () -> "Unable to load config: " + file.getName() + " Reason unknown");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to load config, (IOException)");
                alert.getDialogPane().getStylesheets().add("/gui/darkTheme.css");
                alert.showAndWait();
            } catch (com.google.gson.JsonSyntaxException ex) {
                Main.programLogger.log(Level.WARNING, () -> "JSON unreadable of: " + file.getName());
                Alert alert = new Alert(Alert.AlertType.WARNING, "Failed to load config, because of bad json!");
                alert.getDialogPane().getStylesheets().add("/gui/darkTheme.css");
                alert.showAndWait();
            }
        }
    }

    /**
     * Because all the visualizations are now outdated (they would not get data anyway), dispose them all.
     */
    private void emptyAllVisualizations() {
        for (Node node : visualizationsGridPane.getChildren()) {
            VisualizationControl visualization =  visualizationsMap.get(node);
            if(visualization != null){
                visualization.dispose();
            }
        }
    }

    @FXML
    private void btnCreateConfigClick() {
        try {
            final Stage configEditStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CONFIG_EDIT_FXML));
            Scene scene = new Scene(loader.load(), 600, 600);

            scene.getStylesheets().add("/gui/darkTheme.css");

            configEditStage.initModality(Modality.NONE);
            configEditStage.initOwner(showConnectionWindowBtn.getScene().getWindow());
            configEditStage.setTitle("Configuration Editor");
            configEditStage.setScene(scene);
            configEditStage.setMinWidth(900);
            configEditStage.setMinHeight(600);
            configEditStage.show();

        } catch (Exception ex) {
            Main.programLogger.log(Level.WARNING, "Failed to load: " + CONFIG_EDIT_FXML);
        }
    }

    /**
     * Just open the link to the github wiki.
     * @param mouseEvent
     */
    public void btnHelpClicked(MouseEvent mouseEvent) {
        String test = "https://github.com/Cthaeeh/ground-station-software/wiki";
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(URI.create(test));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
