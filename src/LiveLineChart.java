import data.DataSource;
import data.UpdateDataListener;
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
public class LiveLineChart extends LineChart<Number, Number> implements UpdateDataListener {

    private final int MAX_DATA_POINTS = 20;
    HashMap<DataSource,Series<Number, Number>> seriesDataSourceMap = new HashMap<>();
    final NumberAxis xAxis;
    final NumberAxis yAxis;
    private int xSeriesData = 0;    //TODO later replace this with some accurate time measurement.

    /**
     *
     * @param xAxis
     * @param yAxis
     * @param dataSources the dataSources this LiveLineChart should display.
     */
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
        this.setHorizontalGridLinesVisible(true);
        createSeries(dataSources);
        this.getData().addAll(seriesDataSourceMap.values());
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
            dataSource.addListener(this);       //TODO update metod name or move this elsewhere.
        }
    }

    /*
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
    */

    @Override
    public void onUpdateData(DataSource dataSource, Number x, Number y) {
        Series<Number,Number> series = seriesDataSourceMap.get(dataSource);
        series.getData().add(new XYChart.Data<>(x, y));

        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }
        //TODO set bounds.
    }
}
