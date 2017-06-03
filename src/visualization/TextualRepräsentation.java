package visualization;

import data.DataSource;
import data.Point;
import data.UpdateDataListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.HashMap;

/**
 * Created by Kai on 03.06.2017.
 */
public class TextualRepräsentation extends ListView<Text> implements UpdateDataListener {

    HashMap<DataSource,Text> dataSourceStringMap = new HashMap<>();
    /**
     * DataSources as a text.
     */
    ObservableList<Text> texts = FXCollections.observableArrayList();

    public TextualRepräsentation(ObservableList<DataSource> dataSources) {
        this.setMinSize(10,10);
        this.setPrefSize(600,400);
        this.setItems(texts);
        populate(dataSources);
    }

    private void populate(ObservableList<DataSource> dataSources) {
        for(DataSource dataSource: dataSources){
            Text text = new Text("Name: " + dataSource.getName() + System.lineSeparator() + "Value: ");
            dataSourceStringMap.put(dataSource,text);
            texts.addAll(text);
            text.setFill(Color.web("#e8e8e8"));
            dataSource.addListener(this);
        }
    }

    @Override
    public void onUpdateData(DataSource dataSource, Point point) {
        dataSourceStringMap.get(dataSource).setText("Name: " + dataSource.getName() + System.lineSeparator() + "Value: " + point.y);
    }
}
