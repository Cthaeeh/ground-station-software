import data.DataSource;
import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.HashMap;

/**
 * Created by Kai on 16.05.2017.
 * A live line chart that displays the data from the dataSources it got in the constructor.
 * I got the inspiration from : http://stackoverflow.com/questions/22089022/line-chart-live-update
 *
 */
public class LiveLineChart extends LineChart<Number, Number> {

    private final int MAX_DATA_POINTS = 20;
    HashMap<Series<Number, Number>, DataSource> seriesDataSourceMap = new HashMap<>();
    final NumberAxis xAxis;
    final NumberAxis yAxis;
    private int xSeriesData = 0;    //TODO later replace this with some accurate time measurement.

    public LiveLineChart(final NumberAxis xAxis, final NumberAxis yAxis, ObservableList<DataSource> dataSources) {
        super(xAxis, yAxis);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        this.setAnimated(false);
        this.setTitle("Animated Line Chart");
        this.setHorizontalGridLinesVisible(true);
        createSeries(dataSources);
        this.getData().addAll(seriesDataSourceMap.keySet());
        createAnimationTimer();
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
            seriesDataSourceMap.put(series,dataSource);
        }
    }

    private void createAnimationTimer() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateSeries();
            }
        }.start();
    }

    private void updateSeries() {
        for(Series<Number, Number> series : seriesDataSourceMap.keySet()){
            DataSource source = seriesDataSourceMap.get(series);
            for (int i = 0; i < MAX_DATA_POINTS; i++) { //-- add 20 numbers to the plot+
                if (source.isEmpty()) break;
                series.getData().add(new XYChart.Data<>(xSeriesData++, seriesDataSourceMap.get(series).getAndRemoveLastVal()));
            }
            // remove points to keep us at no more than MAX_DATA_POINTS
            if (series.getData().size() > MAX_DATA_POINTS) {
                series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
            }
        }
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);
    }
}
