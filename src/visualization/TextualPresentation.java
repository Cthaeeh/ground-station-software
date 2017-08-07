package visualization;

import data.sources.*;
import data.Point;
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
public class TextualPresentation extends ListView<Text> implements SimpleSensorListener, VisualizationElement, BitFlagListener, StateListener {

    private List<DataSource> dataSources;

    /**
     * Maps the sources we want to visualize to the corresponding texts, with which we visualize them.
     */
    HashMap<DataSource, Text> dataSourceStringMap = new HashMap<>();
    /**
     * DataSources as a text.
     */
    ObservableList<Text> texts = FXCollections.observableArrayList();

    public TextualPresentation(List<DataSource> dataSources) {
        this.setMinSize(10, 10);
        this.setPrefSize(600, 400);
        this.setItems(texts);
        this.dataSources = dataSources;
        populate(dataSources);
    }

    private void populate(List<DataSource> dataSources) {
        for (DataSource source : dataSources) {
            Text text = new Text(source.getName() + " : ");
            dataSourceStringMap.put(source, text);
            texts.addAll(text);
            text.setFill(Color.web("#e8e8e8"));
            if (source instanceof SimpleSensor) ((SimpleSensor) source).addListener(this);
            if (source instanceof BitFlag) ((BitFlag) source).addListener(this);
            if (source instanceof State) ((State) source).addListener(this);
        }
    }

    @Override
    public void unsubscibeDataSources() {
        for (DataSource source : dataSources) {
            if (dataSources instanceof SimpleSensor) ((SimpleSensor) source).removeListeners(this);
            if (dataSources instanceof BitFlag) ((BitFlag) source).removeListeners(this);
            if (dataSources instanceof State) ((State) source).removeListeners(this);
        }
    }

    @Override
    public void onUpdateData(SimpleSensor sensor, Point point) {
        if(point.y instanceof Double){
            dataSourceStringMap.get(sensor).setText(sensor.getName()+ " : " + String.format("%.2f",(Double)point.y) + " " +sensor.getUnit());
        }else if(point.y instanceof Integer){
            dataSourceStringMap.get(sensor).setText(sensor.getName()+ " : " + point.y + " " +sensor.getUnit());
        }else if(point.y instanceof String){
            dataSourceStringMap.get(sensor).setText(sensor.getName()+ " : " + point.y + " " +sensor.getUnit());
        }

    }

   @Override
    public void onUpdateData(BitFlag bitFlag, Point<Boolean> point) {
        dataSourceStringMap.get(bitFlag).setText(bitFlag.getName() + " : " + (point.y ? "TRUE" : "FALSE"));
    }

    @Override
    public void onUpdateData(State state, Point<String> point) {
        dataSourceStringMap.get(state).setText(state.getName() + " : " + point.y);
    }
}
