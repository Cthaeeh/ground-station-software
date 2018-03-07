package visualization;

import data.DataModel;
import data.sources.DataSource;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Kai on 27.04.2017.
 * Handles the Selection of one or more DataSources and the corresponding way how to display them.
 */
public class DataSourceSelectionControl implements Initializable {

    @FXML
    private StackPane pane;

    //TODO warn users that not all sources can be displayed the same way.

    //Member
    private DataModel model;
    private DataVisualization dataVisualization;

    //FXML elements
    @FXML
    private TableColumn<DataSource, String> nameColumn;
    @FXML
    private TableColumn<DataSource, String> descriptionColumn;

    /**
     * There u can select the sources u want to visualize / present.
     */
    @FXML
    private TableView<DataSource> dataSourceSelectionTable;

    @FXML
    private TextField filteredText;

    /**
     * There u can select how the data is presented.
     * e.g. LineChart or other Chart or Textual or even google maps plugin later.
     */
    @FXML
    private ChoiceBox presentationMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeChoiceBox();
        initializeShortcuts();
    }

    private void initializeShortcuts() {
        pane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            System.out.println("got key event");
            if(event.getCode()== KeyCode.ENTER) btnOkayClick();
            if(event.getCode()==KeyCode.ESCAPE) {
                Stage stage = (Stage) presentationMode.getScene().getWindow();
                stage.close();
            }
        });
    }

    void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
        initializeSelectionTable();
    }

    /**
     * If you want to get informed about the dataVisualization the user has chosen, pass a lambda with this method.
     *
     * @param dataVisualization the one who requested which data should be shown in which way.
     */
    void register(DataVisualization dataVisualization) {
        this.dataVisualization = dataVisualization;
    }

    /**
     * Initializes the ChoiceBox where one can select how data is presented.
     */
    private void initializeChoiceBox() {
        presentationMode.getItems().setAll(VisualizationControl.PresentationMode.values());
        presentationMode.getSelectionModel().selectFirst(); //Select first item by default.
    }

    /**
     * In short this method fills the table with data.
     * It gets the data from the model.
     */
    private void initializeSelectionTable() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());

        FilteredList<DataSource> filteredSources =
                new FilteredList<>(model.getDataSources(), p -> true);

        filteredText.textProperty().addListener((observable, oldValue, newValue) -> {
                    dataSourceSelectionTable.getSelectionModel().clearSelection();
                    filteredSources.setPredicate(dataSource -> {
                        //Show everything if filteredText is empty
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        if (dataSource.getName().toLowerCase().contains(newValue.toLowerCase())) {
                            return true;
                        }
                        //Do not let already selected stuff disappear.
                        return false;
                    });
                }
        );


        dataSourceSelectionTable.setItems(filteredSources);
        dataSourceSelectionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        filteredText.requestFocus();
    }

    @FXML
    private void btnOkayClick() {
        ObservableList<DataSource> selectedDataSources = dataSourceSelectionTable.getSelectionModel().getSelectedItems();
        if (selectedDataSources.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No data source selected");
            alert.getDialogPane().getStylesheets().add("/gui/darkTheme.css");
            alert.showAndWait();
            return;
        }
        dataVisualization.display((VisualizationControl.PresentationMode) presentationMode.getSelectionModel().getSelectedItem(), selectedDataSources);
        Stage stage = (Stage) presentationMode.getScene().getWindow();
        stage.close();
    }

    /**
     * Select all dataSources.
     *
     * @param mouseEvent
     */
    public void selectAll(MouseEvent mouseEvent) {
        dataSourceSelectionTable.getSelectionModel().selectAll();
    }
}

//TODO think about moving this interface to its own file or to visualization.VisualizationControl

/**
 * To communicate which data is displayed how.
 * Example: Humidity Sensor 1 as Line Graph
 */
interface DataVisualization {
    void display(VisualizationControl.PresentationMode presentationMode, ObservableList<DataSource> selectedDataSources);
}
