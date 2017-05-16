package data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by Kai on 01.05.2017.
 * My Approach to the MVC-Pattern. Maybe this could be implemented as a Singleton ...
 */
public class DataModel {

    /**
     * Contains all available Data Sources like a Temperature-Sensor etc.
     */
    private ObservableList<DataSource> dataSourceObservableList = FXCollections.observableArrayList();

    public ObservableList<DataSource> getDataSources() {
        return dataSourceObservableList ;
    }

    //TODO implement saving of DataModel
    public void loadData(File file) throws IOException {
        //http://www.java-forum.org/thema/gson-propleme-bei-stringproperty.174049/
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyAdapter());
        Gson gson = gsonBuilder.create();

        Type collectionType = new TypeToken<Collection<DataSource>>(){}.getType();
        dataSourceObservableList = FXCollections.observableArrayList((Collection<? extends DataSource>) gson.fromJson(readFile(file), collectionType));
    }

    public void saveData(File file) throws IOException {
        // save contents of personList to file ...
    }

    //TODO move this into some helper class.
    public String readFile(File file) throws IOException {
        String content = null;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return content;
    }
}

//TODO move this to some IO-Helper class
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