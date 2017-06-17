package visualization;

import data.sources.DataSource;
import data.Point;
import data.sources.SimpleSensor;
import data.sources.SimpleSensorListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Simple visualization of one or more sources with Text.
 * Subscribes to the DataSources it gets in the Constructor.
 * Created by Kai on 03.06.2017.
 */
public class TextualPresentation extends ListView<Text> implements SimpleSensorListener, VisualizationElement {

    private List<SimpleSensor> sensors;

    //TODO add functionality to also handle bit flags.

    /**
     * Maps the sources we want to visualize to the corresponding texts, with which we visualize them.
     */
    HashMap<DataSource,Text> dataSourceStringMap = new HashMap<>();
    /**
     * DataSources as a text.
     */
    ObservableList<Text> texts = FXCollections.observableArrayList();

    public TextualPresentation(List<SimpleSensor> sensors) {
        this.setMinSize(10,10);
        this.setPrefSize(600,400);
        this.setItems(texts);
        this.sensors = sensors;
        populate(sensors);
    }

    private void populate(List<SimpleSensor> sensors) {
        for(SimpleSensor sensor: sensors){
            Text text = new Text(sensor.getName() + " : ");
            dataSourceStringMap.put(sensor,text);
            texts.addAll(text);
            text.setFill(Color.web("#e8e8e8"));
            sensor.addListener(this);
        }
    }

    @Override
    public void onUpdateData(SimpleSensor sensor, Point point) {
        dataSourceStringMap.get(sensor).setText(sensor.getName()+ " : " + point.y + " " +sensor.getUnit());
    }

    @Override
    public void unsubscibeDataSources() {
        for(SimpleSensor sensor:sensors){
            sensor.removeListeners(this);
        }
    }
}
