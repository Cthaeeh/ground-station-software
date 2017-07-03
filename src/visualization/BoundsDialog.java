package visualization;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import main.Main;

import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Created by Kai on 03.07.2017.
 * Custom Dialog for asking the user for Bounds for the LiveLineChart.
 * Since this is for the LiveLiveChart the user can define the time interval,
 * the y axis bounds and if y is auto ranged.
 */
public class BoundsDialog extends Dialog<Bounds> {

    private final static String TITLE = "Set user defined bounds";
    private final static String HEADER = "Please choose y bounds and x axis interval.";

    //UI Elements
    private final GridPane grid = new GridPane();

    private final TextField yLowerBoundField = new TextField();
    private final TextField yUpperBoundField = new TextField();
    private final TextField xIntervalField = new TextField();

    private final Label yLowerLabel = new Label("y lower bound:");
    private final Label yUpperLabel = new Label("y upper bound:");
    private final Label xIntervalLabel = new Label("x interval in sec:");

    private final CheckBox yAutoRanging = new CheckBox("auto range y axis ?");

    public BoundsDialog() {
        this.setTitle(TITLE);
        this.setHeaderText(HEADER);
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getStylesheets().add("gui/darkTheme.css");
        dialogPane.getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
        initTextFields();
        initCheckBox();
        dialogPane.setContent(initGrid());
        Platform.runLater(yLowerBoundField::requestFocus);
        this.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.APPLY) {
                return getInput();

            }
            return null;
        });
    }

    private Bounds getInput() {
        //TODO check for "" inputs.
        try {
            return new Bounds(Double.parseDouble(yLowerBoundField.getText()),
                    Double.parseDouble(yUpperBoundField.getText()),
                    Double.parseDouble(xIntervalField.getText()), yAutoRanging.isSelected());
        } catch (NumberFormatException ex) {
            Main.programLogger.log(Level.WARNING, () -> {
                return "Could not parse :" + yUpperBoundField.getText() +
                        " and: " + yLowerBoundField.getText() +
                        " and: " + xIntervalField.getText() + " to doubles";
            });
            return null;
        }
    }

    private void initCheckBox() {
        yAutoRanging.selectedProperty().addListener(e -> {
            if (yAutoRanging.isSelected()) {
                yLowerBoundField.setDisable(true);
                yUpperBoundField.setDisable(true);
            } else {
                yLowerBoundField.setDisable(false);
                yUpperBoundField.setDisable(false);
            }
        });
    }

    /**
     * Inits a GridPane with all the needed UI elements Labels and Textfields
     */
    private GridPane initGrid() {
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.addRow(0, yLowerLabel, yLowerBoundField);
        grid.addRow(1, yUpperLabel, yUpperBoundField);
        grid.addRow(2, xIntervalLabel, xIntervalField);
        grid.addRow(3, yAutoRanging);
        return grid;
    }

    /**
     * Allow only doubles on all the text fields.
     */
    private void initTextFields() {
        //TODO make this beautiful. But how ?
        Pattern pattern = Pattern.compile("\\d*|\\d+\\,\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        TextFormatter formatter1 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        TextFormatter formatter2 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        yLowerBoundField.setTextFormatter(formatter);
        yUpperBoundField.setTextFormatter(formatter1);
        xIntervalField.setTextFormatter(formatter2);
    }

}

