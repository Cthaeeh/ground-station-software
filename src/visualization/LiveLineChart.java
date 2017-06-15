package visualization;

import data.sources.DataSource;
import data.Point;
import data.sources.SimpleSensorListener;
import data.sources.SimpleSensor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.HashMap;
import java.util.List;

/**
 * A live line chart that displays the data from the sources it got in the constructor.
 * I got the inspiration from : http://stackoverflow.com/questions/22089022/line-chart-live-update
 * Created by Kai on 16.05.2017.
 */
public class LiveLineChart extends LineChart<Number, Number> implements SimpleSensorListener {

    private static final int MAX_DATA_POINTS = 200;
    HashMap<DataSource,Series<Number, Number>> seriesDataSourceMap = new HashMap<>();
    final NumberAxis xAxis;
    final NumberAxis yAxis;
    private double maxXVal;
    private static final double xIntervalInSec = 60.0;

    /**
     *
     * @param dataSources the sources this visualization.LiveLineChart should display.
     * @param xAxis
     * @param yAxis
     * @param sensors
     */
    public LiveLineChart(final NumberAxis xAxis, final NumberAxis yAxis, List<SimpleSensor> sensors) {
        super(xAxis, yAxis);
        this.xAxis = xAxis;
        initializeX_Axis();
        this.yAxis = yAxis;
        initializeY_Axis();

        this.setMinSize(10,10);
        this.setPrefSize(600,400);

        this.setCreateSymbols(false);
        this.setAnimated(false);
        this.setHorizontalGridLinesVisible(false);
        this.setVerticalGridLinesVisible(false);
        createSeries(sensors);
        this.getData().addAll(seriesDataSourceMap.values());
    }

    private void initializeX_Axis() {
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
    }

    private void initializeY_Axis() {
        yAxis.setForceZeroInRange(false);
        yAxis.setAutoRanging(true);
        yAxis.setTickLabelsVisible(true);
        yAxis.setTickMarkVisible(true);
        yAxis.setMinorTickVisible(false);
    }

    /**
     * Creates a Map with Series as Keys and Datasources as values.
     * That enables us to quickly find the corresponding dataSource to a series which,
     * holds the displayed data.
     * @param dataSources
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
            xAxis.setUpperBound(maxXVal + (xIntervalInSec)/20);
        }

        xAxis.setLowerBound(maxXVal - xIntervalInSec);
    }
}
