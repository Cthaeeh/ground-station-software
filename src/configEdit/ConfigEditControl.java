package configEdit;

import configEdit.list_view_cells.DataSourceCell;
import configEdit.list_view_cells.TelecommandCell;
import data.Config;
import data.DataModel;
import data.TeleCommand;
import data.sources.DataSource;
import data.sources.SimpleSensor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;

import static data.sources.DataSource.Type.SIMPLE_SENSOR;

/**
 * Created by Kai on 04.06.2017.
 * The stuff here is mostly independent from the Rest of the application.
 * The task of this class is to manipulate /create the JSON file that is used later for the Rest of the application.
 *
 */
public class ConfigEditControl implements Initializable {

    @FXML
    private ListView<DataSource> dataSourcesListView;
    @FXML
    private ListView<TeleCommand> telecommandsListView;
    @FXML
    private GeneralConfigControl generalConfigController;
    private FileChooser fileChooser;

    /**
     * An Extra DataModel for the Config Editing.
     */
    private DataModel model;
    //TODO implement this.
    //TODO SHOW warning if user wants to have a dataSource we uneven number of bits.

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initFileChooser();
        model = new DataModel();
        model.loadEmptyConfig();
        generalConfigController.initModel(model.getConfig());
        initTmList(model.getConfig());
        initTcList(model.getConfig());
    }

    private void initTcList(Config config) {
        telecommandsListView.getItems().clear();
        telecommandsListView.getItems().addAll(config.getTeleCommands());
        telecommandsListView.setEditable(true);
        telecommandsListView.setCellFactory(e -> new TelecommandCell());
    }

    private void initTmList(Config config) {
        dataSourcesListView.getItems().clear();
        dataSourcesListView.getItems().addAll(config.getDataSources());
        dataSourcesListView.setEditable(true);
        dataSourcesListView.setCellFactory(e-> new DataSourceCell());
    }

    private void initFileChooser() {
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extentionFilter);
        File f = new File("interpretationFiles");
        if(!f.exists() && !f.isDirectory())
        {
            f.mkdir();                      //Create if not existing.
        }
        fileChooser.setInitialDirectory(f);

    }

    @FXML
    private void btnSaveClicked(){
        File file = fileChooser.showSaveDialog(null);
        //TODO check if model is a legit state. everything is consistent with the ressources.
        try {
            model.saveConfigData(file);
        } catch (IOException e) {
            Main.programLogger.log(Level.WARNING, () -> "Failed to save data Model to json file :" + System.lineSeparator() + e.getMessage());
        }

    }

    @FXML
    private void btnOpenExistingClicked(){
        File file = fileChooser.showOpenDialog(null);
        try {
            model.loadConfigData(file);
            generalConfigController.initModel(model.getConfig());
            initTcList(model.getConfig());
            initTmList(model.getConfig());
        } catch (IOException e) {
            Main.programLogger.log(Level.WARNING, () -> "Failed to load data Model from json file :" + System.lineSeparator() + e.getMessage());
        }
    }

    public void addCommandClicked(MouseEvent mouseEvent) {
        telecommandsListView.getItems().add(new TeleCommand());
    }

    public void addDataSourceClicked(MouseEvent mouseEvent) {
        ChoiceDialog<DataSource.Type> dialog = new ChoiceDialog<>(SIMPLE_SENSOR, DataSource.Type.values());
        dialog.setTitle("Choose pls:");
        dialog.setHeaderText("Look, a Choice Dialog");
        dialog.setContentText("Choose your kind of data source:");

        Optional<DataSource.Type> result = dialog.showAndWait();
        result.ifPresent(source -> {
            dataSourcesListView.getItems().add(source.getInstance());
        });
    }
}
