package data;

import com.google.gson.*;
import data.sources.DataSource;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Holds all data needed for the application. The configuration, etc.
 * My Approach to the MVC-Pattern.
 */
public class DataModel {

    private Config config;
    private final ObservableList<DataSource> dataSources = FXCollections.observableArrayList();
    private final ObservableList<TeleCommand> teleCommands = FXCollections.observableArrayList();;

    public ObservableList<DataSource> getDataSources() {
        return dataSources;
    }

    public ObservableList<TeleCommand> getTeleCommands() {
        return teleCommands;
    }

    //TODO is this really the right way ?
    public Config getConfig(){
        return config;
    }

    public void loadConfigData(File file) throws IOException, InvalidConfig {
        //http://www.java-forum.org/thema/gson-propleme-bei-stringproperty.174049/
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyAdapter());
        Gson gson = gsonBuilder.create();
        config =  gson.fromJson(IOUtility.readFile(file), Config.class);
        ConfigChecker.verification(config);
        dataSources.clear();
        dataSources.addAll(config.getDataSources());
        teleCommands.clear();
        if(config.getTeleCommands()!=null)teleCommands.addAll(config.getTeleCommands());
        Main.programLogger.log(Level.INFO,"Loaded configuration file: " + file.getName() + " into the DataModel.");
    }

    //TODO implement saving of DataModel
    public void saveConfigData(File file) throws IOException {
        // save contents of model to file ...
        Main.programLogger.log(Level.SEVERE, ()-> "Save config not implemented yet.");
    }

    /**
     * In order to create a new config.
     */
    public void loadEmptyConfig() {
        config = new Config();
        dataSources.clear();
        teleCommands.clear();
    }
}


