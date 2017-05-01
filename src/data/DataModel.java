package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by Kai on 01.05.2017.
 */
public class DataModel {

    /**
     * Contains all available Data Sources like a Temperature-Sensor etc.
     */
    private ObservableList<DataSource> dataSourceObservableList = FXCollections.observableArrayList();

    public ObservableList<DataSource> getDataSources() {
        return dataSourceObservableList ;
    }

    //TODO implement loading and saving of DataModel
    public void loadData(File file) throws IOException {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<DataSource>>(){}.getType();
        dataSourceObservableList = FXCollections.observableArrayList((Collection<? extends DataSource>) gson.fromJson(readFile(file), collectionType));
        System.out.println("Loaded data from " + file);
        dataSourceObservableList.forEach( element -> System.out.println(element.toString()));
    }

    public void saveData(File file) throws IOException {

        // save contents of personList to file ...
    }

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
