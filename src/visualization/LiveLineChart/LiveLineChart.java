package visualization.LiveLineChart;

import data.sources.DataSource;
import data.Point;
import data.sources.SimpleSensor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import visualization.VisualizationElement;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * A live line chart that displays the data from the stuff it got in the constructor.
 * Has an Dialog (that opens on right click), where
 * you can select the Bounds of the graph ( y lower , y upper , x interval )
 * I got the inspiration from : http://stackoverflow.com/questions/22089022/line-chart-live-update
 * Created by Kai on 16.05.2017.
 */
public class LiveLineChart extends LineChart<Number, Number> implements SimpleSensorListener, VisualizationElement {

    private List<SimpleSensor> sensors;
    private HashMap<DataSource,Series<Number, Number>> seriesDataSourceMap = new HashMap<>();
    final NumberAxis xAxis;
    final NumberAxis yAxis;

    /**
     * Highest x Value found so far (for all series)
     */
    private double maxXVal;

    //default bounds.
    private Bounds bounds = new Bounds.Builder(true).xTimeIntervalSec(5).build();

    /**
     * Maximum datapoints the graph holds for each series ( variable it tracks ).
     * This could be made dependant on the x inteval (which the user can decide).
     * But the thing is that this variable can affect the overall performance of the programm.
     * So for now it is just hard coded to a rather low value here.
     */
    private static final int MAX_DATA_POINTS = 100;

    /**
     * Create a new LiveLineChart that will visualize the sensors passed as an parameter.
     * @param xAxis
     * @param yAxis
     * @param sensors
     */
    public LiveLineChart(final NumberAxis xAxis, final NumberAxis yAxis, List<SimpleSensor> sensors) {
        super(xAxis, yAxis);
        this.xAxis = xAxis;
        initializeXAxis();
        this.yAxis = yAxis;
        initializeYAxis();

        this.setMinSize(10,10);
        this.setPrefSize(600,400);

        this.setCreateSymbols(false);
        this.setAnimated(false);
        this.setHorizontalGridLinesVisible(false);
        this.setVerticalGridLinesVisible(false);
        this.sensors = sensors;
        createSeries(sensors);
        initCustomRangingDialog();
        this.getData().addAll(seriesDataSourceMap.values());
    }

    private void initCustomRangingDialog() {
        // TODO overlapping with the contextMenu tht is shown in the whole visualization area.
        this.setOnMouseClicked(e->{
            System.out.println(e.getButton().name());
            if(e.getButton() == MouseButton.SECONDARY){
                BoundsDialog dialog = new BoundsDialog();
                Optional<Bounds> boundsOptional = dialog.showAndWait();
                boundsOptional.ifPresent((Bounds bounds) -> {
                     this.bounds = bounds;
                     initializeYAxis();
                });
            }
        });
    }

    private void initializeXAxis() {
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
    }

    private void initializeYAxis() {
        yAxis.setForceZeroInRange(false);
        yAxis.setAutoRanging(bounds.yAutoRange());
        if(!bounds.yAutoRange()){
            yAxis.setLowerBound(bounds.getyLowerBound());
            yAxis.setUpperBound(bounds.getyUpperBound());
        }
        yAxis.setTickLabelsVisible(true);
        yAxis.setTickMarkVisible(true);
        yAxis.setMinorTickVisible(false);
    }

    /**
     * Creates a Map with Series as Keys and Datasources as values.
     * That enables us to quickly find the corresponding dataSource to a series which,
     * holds the displayed data.
     * @param sensors
     * @return
     */
    private void createSeries(List<SimpleSensor> sensors) {
        for(SimpleSensor sensor:sensors){
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(sensor.getName() + " in " + sensor.getUnit());
            seriesDataSourceMap.put(sensor,series);
            sensor.addListener(this);
        }
    }

    @Override
    public void onUpdateData(SimpleSensor sensor, Point<Number> point) {
        Series<Number,Number> series = seriesDataSourceMap.get(sensor);
        series.getData().add(new XYChart.Data<>(point.x,point.y));

        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }

        if(point.x.doubleValue()>maxXVal){
            maxXVal = point.x.doubleValue();
            xAxis.setUpperBound(maxXVal + (bounds.getxTimeIntervalSec())/20);
        }

        xAxis.setLowerBound(maxXVal - bounds.getxTimeIntervalSec());
    }

    /**
     * Unsubscribe from the dataSources this class gets its data from.
     */
    @Override
    public void unsubscibeDataSources() {
        for(SimpleSensor sensor : sensors){
            sensor.removeListeners(this);
        }
    }
}
