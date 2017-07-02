package configEdit.list_view_cells;

import data.sources.DataSource;
import data.sources.SimpleSensor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.Encoder;

/**
 * Created by kai on 7/2/17.
 */
public class SimpleSensorCell extends DataSourceCell{
    private final Label unitLabel = new Label("Unit:");
    private final Label offsetLabel = new Label("Offset:");
    private final Label proportionalFactorLabel = new Label("Proportional Factor:");

    private final TextField unitTextField = new TextField();
    private final TextField offsetTextField = new TextField();
    private final TextField proportionalFactorTextField = new TextField();

    public SimpleSensorCell (){
        super();
        initGrid();
    }

    /**
     * Sets up the grid and adds the various nodes to it.
     */
    private void initGrid() {
        int numOfRows = 4;      //TODO make this smoother. THis comes from the super class .
        grid.add(unitLabel,0,numOfRows+1);
        grid.add(offsetLabel,0,numOfRows+2);
        grid.add(proportionalFactorLabel,0,numOfRows+3);
        grid.add(unitTextField,1,numOfRows+1);
        grid.add(offsetTextField,1,numOfRows+2);
        grid.add(proportionalFactorTextField,1,numOfRows+3);
    }

    @Override
    public void updateItem(DataSource dataSource, boolean empty){
        super.updateItem(dataSource , empty);
        if(empty){
            clear();
        }else {
            if(dataSource instanceof SimpleSensor){
                SimpleSensor sensor = (SimpleSensor) dataSource;
                unitTextField.setText(sensor.getUnit());
                offsetTextField.setText(String.valueOf(sensor.getOffset()));
                proportionalFactorTextField.setText(String.valueOf(sensor.getPropotionalFactor()));
            }else {
                throw new IllegalStateException("SimpleSensorCell can only display a Simple Sensor");
            }
        }
        setGraphic(grid);
    }

    /**
     * Empties all textfields so they only contain "";
     */
    private void clear() {
        unitTextField.setText("");
        offsetTextField.setText("");
        proportionalFactorTextField.setText("");
    }

}
