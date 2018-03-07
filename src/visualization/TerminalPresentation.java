package visualization;

import data.Point;
import data.sources.*;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import main.Main;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by kai on 7/9/17.
 * Visualization element that "supports" dataSources that do not regularly/often publish data.
 * So maybe some remote device only sends it State of its statemachine if a telecommand
 * ecourages it to do so a TextualPresentation is overkill. Then you can use this.
 */
public class TerminalPresentation extends VBox implements VisualizationElement, GnssListener, StateListener, BitFlagListener, SimpleSensorListener, StringSourceListener {

    //TODO delete text after a while
    private List<DataSource> dataSources;
    // Use Code Area because of monospace font.
    private final CodeArea textArea;
    private final SearchBarControl searchBar;

    final KeyCombination searchKeyCombo = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
    final KeyCombination Esc = new KeyCodeCombination(KeyCode.ESCAPE);

    TerminalPresentation(List<DataSource> dataSources) {
        this.setMinSize(100, 100);
        this.setPrefSize(600, 400);
        textArea = new CodeArea();
        VirtualizedScrollPane v = new VirtualizedScrollPane(textArea);
        textArea.setEditable(false);
        textArea.setPrefHeight(3000);
        this.getChildren().add(v);

        searchBar = new SearchBarControl();
        this.dataSources = dataSources;

        addContextMenu();
        addSearch();
        subscribeTo(dataSources);
    }

    private void addSearch() {
        this.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (searchKeyCombo.match(event)) {
                if(!this.getChildren().contains(searchBar)){
                    this.getChildren().add(searchBar);
                    Platform.runLater(()->searchBar.getSearchField().requestFocus());
                }
            }
            if (Esc.match(event)) {
                this.getChildren().remove(searchBar);
                for(int i = 0 ; i< 100; i++)
                Platform.runLater(()->this.requestFocus());
            }
        });
        searchBar.getSearchField().textProperty().addListener((observableValue,oldValue,newValue)->{
            textArea.setStyleSpans(0, highlight(newValue));
        });
        textArea.setId("codeArea");
    }

    private StyleSpans<Collection<String>> highlight(String string) {
        String text = textArea.getText();
        Pattern pattern = Pattern.compile(string, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();

        // End of the last occurrence of the string we try to highlight.
        int lastEnd = 0;
        while(matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastEnd);
            spansBuilder.add(Collections.singleton("highlight"), matcher.end() - matcher.start());
            lastEnd = matcher.end();
        }
            spansBuilder.add(Collections.emptyList(), text.length()-lastEnd);
        return spansBuilder.create();
    }

    private void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem clear = new MenuItem("clear");
        contextMenu.getItems().add(clear);
        clear.setOnAction(e-> textArea.clear());
        textArea.setContextMenu(contextMenu);
    }

    private void subscribeTo(List<DataSource> dataSources) {
        textArea.appendText("TERMINAL is listening to following data-sources: " + System.lineSeparator());
        for (DataSource source : dataSources) {
            textArea.appendText(source.getName() + "," + System.lineSeparator());
            if (source instanceof SimpleSensor) {
                ((SimpleSensor) source).addListener(this);
            } else if (source instanceof BitFlag) {
                ((BitFlag) source).addListener(this);
            } else if (source instanceof State) {
                ((State) source).addListener(this);
            } else if (source instanceof StringSource) {
                ((StringSource) source).addListner(this);
            } else if (source instanceof Gnss) {
                ((Gnss) source).addListener(this);
            } else {
                Main.programLogger.log(Level.WARNING, () -> "Datasource:" + source.getName()
                        + " of type: " + source.getClass().getName() + " is not supported by TerminalPresentation");
            }
        }
        textArea.appendText("______________________________________________" + System.lineSeparator());
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
     * @param state State that is updated.
     * @param point the new data.
     */
    @Override
    public void onUpdateData(State state, Point<String> point) {
        String text = state.getName() + " : " + point.y + " " + point.x + " sec " + System.lineSeparator();
        textArea.appendText(text);
        textArea.appendText(System.lineSeparator());
    }

    /**
     * Gets called when some BitFlag gets new data.
     *
     * @param bitFlag BitFLag that is updated.
     * @param point   new data.
     */
    @Override
    public void onUpdateData(BitFlag bitFlag, Point<Boolean> point) {
        String text = bitFlag.getName() + " : " + (point.y ? "TRUE" : "FALSE") + " " + point.x + " sec " + System.lineSeparator();
        textArea.appendText(text);
        textArea.appendText(System.lineSeparator());
    }

    @Override
    public void onUpdateData(SimpleSensor sensor, Point<Number> point) {
        if (point.y instanceof Double) {
            textArea.appendText(sensor.getName() + " : " + String.format("%.2f", (Double) point.y) + " " + sensor.getUnit() + " " + point.x + " sec " + System.lineSeparator());
        } else if (point.y instanceof Integer) {
            textArea.appendText(sensor.getName() + " : " + point.y + " " + sensor.getUnit() + " " + point.x + " sec " + System.lineSeparator());
        }
        textArea.appendText(System.lineSeparator());
    }

    /**
     * Gets called if a Stringsource we subscribed to  gets new data.
     *
     * @param stringSource The string source that is updated.
     * @param point        the new data.
     */
    @Override
    public void onUpdateData(StringSource stringSource, Point<String> point) {
        textArea.appendText(stringSource.getName() + " : " + point.y);
        textArea.appendText(System.lineSeparator());
    }

    @Override
    public void onUpdateData(Gnss gnssSource, Point<GnssFrame> point) {
        textArea.appendText(gnssSource.getName() + " : " + point.y.toString());
        textArea.appendText(System.lineSeparator());
    }
}

