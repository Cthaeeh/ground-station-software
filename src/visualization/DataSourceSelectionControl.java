package visualization;

import data.DataModel;
import data.DataSource;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kai on 27.04.2017.
 * Handles the Selection of one or more DataSources and the corresponding way how to display them.
 */
public class DataSourceSelectionControl implements Initializable{

    //Member
    private DataModel model ;
    private DataVisualization dataVisualization;

    //FXML elements
    @FXML
    private TableColumn<DataSource, String> nameColumn;
    @FXML
    private TableColumn<DataSource, String> descriptionColumn;

    @FXML
    private TableView dataSourceSelectionTable;

    /**
     * e.g. LineChart or other Chart or even google maps plugin later.
     */
    @FXML
    private ChoiceBox presentationMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeChoiceBox();
        presentationMode.getSelectionModel().selectedItemProperty().addListener(e -> System.out.println(e.toString()));
    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
        initializeSelectionTable();
    }

    /**
     * If you want to get informed about the dataVisualization the user has chosen, pass a lambda with this method.
     * @param dataVisualization
     */
    public void register(DataVisualization dataVisualization){
        this.dataVisualization = dataVisualization;
    }

    /**
     * Initializes the ChoiceBox where one can select how data is presented.
     */
    private void initializeChoiceBox() {
        presentationMode.getItems().setAll(VisualizationElementControl.PresentationMode.values());
        presentationMode.getSelectionModel().selectFirst(); //Select first item by default.
    }

    /**
     * In short this method fills the table with data.
     * It gets the data from the model.
     */
    private void initializeSelectionTable() {
        //TODO load available data sources from model that we no have access if initModel was called.
        dataSourceSelectionTable.setItems(model.getDataSources());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        dataSourceSelectionTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void btnOkayClick(){
        ObservableList<DataSource> selectedDataSources = dataSourceSelectionTable.getSelectionModel().getSelectedItems();
        if(selectedDataSources.size()==0){
            Alert alert = new Alert(Alert.AlertType.WARNING, "No data source selected");
            alert.showAndWait();
            //TODO style Alert.
            return;
        }
        dataVisualization.display((VisualizationElementControl.PresentationMode) presentationMode.getSelectionModel().getSelectedItem(),selectedDataSources);
        Stage stage = (Stage) presentationMode.getScene().getWindow();
        stage.close();
    }
}

//TODO think about moving this interface to its own file or to visualization.VisualizationElementControl
/**
 * To communicate which data is displayed how.
 * Example: Humidity Sensor 1 as Line Graph
 */
interface DataVisualization {
    void display(VisualizationElementControl.PresentationMode presentationMode, ObservableList<DataSource> selectedDataSources);
}
