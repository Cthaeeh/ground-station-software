import data.DataModel;
import data.DataSource;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by Kai on 27.04.2017.
 */
public class DataSourceSelectionControl implements Initializable{

    private DataModel model ;

    public enum PresentationMode {
        LINE_CHART("Line-chart");

        private String name;

        PresentationMode(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

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
    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
        initializeSelectionTable();
    }

    /**
     * Initializes the ChoiceBox where one can select how data is presented.
     */
    private void initializeChoiceBox() {
        presentationMode.getItems().setAll(PresentationMode.values());
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
        //TODO implement close this window and communicate somehow back to the visualizationElement.
        ObservableList<DataSource> selectedItems = dataSourceSelectionTable.getSelectionModel().getSelectedItems();
        selectedItems.forEach(item -> System.out.println(item.toString()));
    }
}