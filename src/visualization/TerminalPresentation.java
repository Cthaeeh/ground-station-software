package visualization;

import data.Point;
import data.sources.*;
import javafx.scene.control.TextArea;
import main.Main;

import java.util.List;
import java.util.logging.Level;


/**
 * Created by kai on 7/9/17.
 * Visualization element that "supports" dataSources that do not regularly/often publish data.
 * So maybe some remote device only sends it State of its statemachine if a telecommand
 * ecourages it to do so a TextualPresentation is overkill. Then you can use this.
 */
public class TerminalPresentation extends TextArea implements VisualizationElement, StateListener, BitFlagListener, SimpleSensorListener, StringSourceListener {

    private List<DataSource> dataSources;

    public TerminalPresentation(List<DataSource> dataSources) {
        this.setMinSize(100, 100);
        this.setPrefSize(600, 400);
        this.setEditable(false);
        this.dataSources = dataSources;
        subscribeTo(dataSources);
    }

    private void subscribeTo(List<DataSource> dataSources) {
        for (DataSource source : dataSources) {
            if (source instanceof SimpleSensor) {
                ((SimpleSensor) source).addListener(this);
                continue;
            }
            if (source instanceof BitFlag) {
                ((BitFlag) source).addListener(this);
                continue;
            }
            if (source instanceof State) {
                ((State) source).addListener(this);
                continue;
            }
            if (source instanceof StringSource) {
                ((StringSource) source).addListner(this);
                continue;
            }
            Main.programLogger.log(Level.WARNING, () -> "Datasource:" + source.getName() + " of type: " + source.getClass().getName() + " is not supported by TerminalPresentation");
        }
    }

    @Override
    public void unsubscibeDataSources() {
        for (DataSource source : dataSources) {
            if (source instanceof SimpleSensor) ((SimpleSensor) source).removeListeners(this);
            if (source instanceof BitFlag) ((BitFlag) source).removeListeners(this);
            if (source instanceof State) ((State) source).removeListeners(this);
            if (source instanceof StringSource) ((StringSource) source).removeListener(this);
        }
    }

    /**
     * Gets called when some state gets new data.
     *
     * @param state
     * @param point
     */
    @Override
    public void onUpdateData(State state, Point<String> point) {
        String text = state.getName() + " : " + point.y + " " + point.x + " t " + System.lineSeparator();
        this.appendText(text);
        this.appendText(System.lineSeparator());
    }

    /**
     * Gets called when some BitFlag gets new data.
     *
     * @param bitFlag
     * @param point
     */
    @Override
    public void onUpdateData(BitFlag bitFlag, Point<Boolean> point) {
        String text = bitFlag.getName() + " : " + (point.y ? "TRUE" : "FALSE") + " " + point.x + " t " + System.lineSeparator();
        this.appendText(text);
        this.appendText(System.lineSeparator());
    }

    @Override
    public void onUpdateData(SimpleSensor sensor, Point<Number> point) {
        if(point.y instanceof Double){
            this.appendText(sensor.getName()+ " : " + String.format("%.2f",(Double)point.y) + " " +sensor.getUnit()+ point.x + " t " + System.lineSeparator());
        }else if(point.y instanceof Integer){
            this.appendText(sensor.getName()+ " : " + point.y + " " +sensor.getUnit()+ point.x + " t " + System.lineSeparator());
        }
        this.appendText(System.lineSeparator());
    }

    /**
     * Gets called if a Stringsource we subscribed to  gets new data.
     * @param stringSource
     * @param point
     */
    @Override
    public void onUpdateData(StringSource stringSource, Point<String> point) {
        this.appendText(stringSource.getName() + " : " + point.y);
        this.appendText(System.lineSeparator());
    }
}

