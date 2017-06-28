package configEdit;

import data.DataModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Created by Kai on 04.06.2017.
 * The stuff here is mostly independent from the Rest of the application.
 * The task of this class is to manipulate /create the JSON file that is used later for the Rest of the application.
 *
 */
public class ConfigEditControl implements Initializable {

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
        generalConfigController.initModel(model);
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
        //TODO check if model is a legit state. everything is consistent with the gui.
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
            generalConfigController.initModel(model);
        } catch (IOException e) {
            Main.programLogger.log(Level.WARNING, () -> "Failed to load data Model from json file :" + System.lineSeparator() + e.getMessage());
        }
    }

}
