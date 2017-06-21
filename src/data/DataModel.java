package data;

import com.google.gson.*;
import data.sources.DataSource;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.logging.Level;

/**
 * Holds all data needed for the application. The configuration, etc.
 * My Approach to the MVC-Pattern. Maybe this could be implemented as a Singleton ...
 */
public class DataModel {

    private JsonSerializableConfig config;
    private final ObservableList<DataSource> dataSources = FXCollections.observableArrayList();
    private final ObservableList<TeleCommand> teleCommands = FXCollections.observableArrayList();;

    public ObservableList<DataSource> getDataSources() {
        return dataSources;
    }

    public ObservableList<TeleCommand> getTeleCommands() {
        return teleCommands;
    }

    //TODO is this really the right way ?
    public JsonSerializableConfig getConfig(){
        return config;
    }

    public void loadConfigData(File file) throws IOException {
        //http://www.java-forum.org/thema/gson-propleme-bei-stringproperty.174049/
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyAdapter());
        Gson gson = gsonBuilder.create();
        config =  gson.fromJson(IOUtility.readFile(file),JsonSerializableConfig.class);
        dataSources.clear();
        dataSources.addAll(config.getDataSources());
        teleCommands.clear();
        if(config.getTeleCommands()!=null)teleCommands.addAll(config.getTeleCommands());
        Main.programLogger.log(Level.INFO,"Loaded configuration file: " + file.getName() + " into the DataModel.");
        System.out.println("ID_legth" + config.getIdLength());
    }

    //TODO implement saving of DataModel
    public void saveConfigData(File file) throws IOException {
        // save contents of model to file ...
    }

}


/**
 * Allows to serialize/deserialize strings to string-properties and back.
 */
final class StringPropertyAdapter implements JsonSerializer<StringProperty>, JsonDeserializer<StringProperty> {

    @Override
    public StringProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return new SimpleStringProperty(json.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement serialize(StringProperty src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getValue());
    }
}