import data.DataSource;
import data.UpdateDataListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.HashMap;

/**
 * A live line chart that displays the data from the dataSources it got in the constructor.
 * I got the inspiration from : http://stackoverflow.com/questions/22089022/line-chart-live-update
 * Created by Kai on 16.05.2017.
 */
public class LiveLineChart extends LineChart<Number, Number> implements UpdateDataListener {

    private final int MAX_DATA_POINTS = 200;
    HashMap<DataSource,Series<Number, Number>> seriesDataSourceMap = new HashMap<>();
    final NumberAxis xAxis;
    final NumberAxis yAxis;
    private double maxXVal;
    private double minXVal;
    private final double xIntervalInSec = 10.0;

    /**
     *
     * @param xAxis
     * @param yAxis
     * @param dataSources the dataSources this LiveLineChart should display.
     */
    public LiveLineChart(final NumberAxis xAxis, final NumberAxis yAxis, ObservableList<DataSource> dataSources) {
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
        createSeries(dataSources);
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
     * @return
     */
    private void createSeries(ObservableList<DataSource> dataSources) {
        for(DataSource dataSource:dataSources){
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(dataSource.getName());
            seriesDataSourceMap.put(dataSource,series);
            dataSource.addListener(this);       //TODO update method name or move this elsewhere.
        }
    }

    @Override
    public void onUpdateData(DataSource dataSource, Pair<Number,Number> point) {
        Series<Number,Number> series = seriesDataSourceMap.get(dataSource);
        series.getData().add(new XYChart.Data<>(point.getKey(),point.getValue()));

        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }

        if(point.getKey().doubleValue()>maxXVal){
            maxXVal = point.getKey().doubleValue();
            xAxis.setUpperBound(maxXVal + (xIntervalInSec)/20);
        }

        xAxis.setLowerBound(maxXVal - xIntervalInSec);
    }
}
