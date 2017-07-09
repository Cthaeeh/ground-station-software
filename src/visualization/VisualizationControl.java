package visualization;

import data.DataModel;
import data.sources.DataSource;
import data.sources.SimpleSensor;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import visualization.LiveLineChart.LiveLineChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


/**
 * Created by Kai on 01.05.2017.
 * This is some kind of wrapper for different Visualization Elements.
 * It can hold a LiveLineChart for example.
 */
public class VisualizationControl {

    private DataModel model;
    /**
     * The current visualization Element that is displayed.
     * Why the interface ?
     * We need a way to tell it to unsubscribe from its dataSources, so that GBC will find it.
     */
    private VisualizationElement visualizationElement;

    public enum PresentationMode {
        LINE_CHART("Line-chart"),
        TEXTUAL_MODE("Textual"),
        TERMINAL_MODE("Terminal");

        private String name;

        PresentationMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Some empty pane to add stuff to
     * Here we can show for example a LiveLineChart.
     */
    @FXML
    private StackPane pane;

    @FXML
    private Button selectDataSourceButton;

    private static final String DATA_SOURCE_SELECTION_FXML = "../gui/data_source_selection_dialog.fxml";

    /**
     * Injects the global data Model into this controller.
     *
     * @param model the model to inject.
     */
    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
    }

    /**
     * Call this method if this Visualization will no longer be used.
     * This allows us to unsubscribe from dataSources we might listen to.
     * This way GBC will delete us then.
     */
    public void dispose() {
        if (visualizationElement != null) {
            visualizationElement.unsubscibeDataSources();
        }
    }

    /**
     *
     */
    @FXML
    private void btnSelectDataSourceClick() {
        try {
            final Stage dialog = new Stage();
            FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource(DATA_SOURCE_SELECTION_FXML));
            Scene scene = new Scene(dialogLoader.load(), 600, 600);

            DataSourceSelectionControl dataSourceSelectionControl = dialogLoader.getController();
            dataSourceSelectionControl.initModel(model);
            dataSourceSelectionControl.register(this::displayData);
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

        //Clean up eventually existing visualizationElements.
        if (visualizationElement != null) visualizationElement.unsubscibeDataSources();
        pane.getChildren().clear();

        switch (mode) {
            case LINE_CHART:
                visualizationElement = createLiveLineChart(dataSources);
                break;
            case TEXTUAL_MODE:
                visualizationElement = createTextualPresentation(dataSources);
                break;
            case TERMINAL_MODE:
                visualizationElement = createTerminalPresentation(dataSources);
                break;
            default:
                Main.programLogger.log(Level.WARNING,()->mode.name + "is not supported yet.");
        }
    }

    /**
     * Creates a new LiveLineChart and adds it to the pane.
     * Returns it as an generic Visualization Element.
     *
     * @param dataSources the dataSources you want to visualize with the LiveLineChart.
     * @return the LiveLineChart as an Visualization Element.
     */
    private VisualizationElement createLiveLineChart(ObservableList<DataSource> dataSources) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        List<SimpleSensor> simpleSensors = new ArrayList<>();
        for (DataSource dataSource : dataSources) {                                               //Add dataSources that can be displayed by this type of visualization Element to a list.
            if (dataSource instanceof SimpleSensor) simpleSensors.add((SimpleSensor) dataSource);
        }
        LiveLineChart newChart = new LiveLineChart(xAxis, yAxis, simpleSensors);
        pane.getChildren().add(newChart);
        return newChart;
    }

    /**
     * Creates a new TextualPresentation and adds it to the pane.
     * Returns it as an generic Visualization Element.
     *
     * @param dataSources the dataSources you want to visualize with the TextualPresentation.
     * @return the TextualPresentation as an Visualization Element.
     */
    private VisualizationElement createTextualPresentation(ObservableList<DataSource> dataSources) {
        TextualPresentation newTextual = new TextualPresentation(dataSources);
        pane.getChildren().add(newTextual);
        return newTextual;
    }

    /**
     * Creates a new TerminalPresentation and adds it to the pane.
     * Returns it as an generic Visualization Element.
     *
     * @param dataSources the dataSources you want to visualize with the TerminalPresentation.
     * @return the TerminalPresentation as an Visualization Element.
     */
    private VisualizationElement createTerminalPresentation(ObservableList<DataSource> dataSources) {
        TerminalPresentation newTerminal = new TerminalPresentation(dataSources);
        pane.getChildren().add(newTerminal);
        return newTerminal;
    }
}
