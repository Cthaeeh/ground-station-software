import data.DataModel;
import data.DataSource;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Created by Kai on 01.05.2017.
 */
public class VisualizationElementControl {

    private DataModel model ;

    //TODO add more.
    public enum PresentationMode {
        LINE_CHART("Line-chart");

        private String name;

        PresentationMode(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    /**
     * Some empty pane to add stuff to
     */
    @FXML
    Pane pane;

    @FXML
    Button selectDataSourceButton;

    private final String DATA_SOURCE_SELECTION_FXML = "gui/data_source_selection_dialog.fxml";

    /**
     * Injects the global data Model into this controller.
     * @param model
     */
    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
    }

    /**
     *
     */
    @FXML
    private void btnSelectDataSourceClick(){
        try {
            final Stage dialog = new Stage();
            FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource(DATA_SOURCE_SELECTION_FXML));
            Scene scene = new Scene(dialogLoader.load(), 600, 600);

            DataSourceSelectionControl dataSourceSelectionControl = dialogLoader.getController();
            dataSourceSelectionControl.initModel(model);
            dataSourceSelectionControl.register((mode,dataSources) -> displayData(mode,dataSources));
            scene.getStylesheets().add("gui/darkTheme.css");

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(selectDataSourceButton.getScene().getWindow());
            dialog.setMinWidth(400);
            dialog.setMinHeight(400);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void displayData(PresentationMode mode, ObservableList<DataSource> dataSources) {
        switch (mode){
            case LINE_CHART:
                NumberAxis xAxis = new NumberAxis();
                NumberAxis yAxis = new NumberAxis();
                pane.getChildren().add(new LiveLineChart(xAxis, yAxis,dataSources));
                break;
        }
    }
}
