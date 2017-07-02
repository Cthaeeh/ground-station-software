package configEdit.list_view_cells;

import data.sources.BitFlag;
import data.sources.DataSource;
import data.sources.SimpleSensor;
import data.sources.State;
import gui.NumberTextField;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import main.Encoder;

/**
 * Created by kai on 7/2/17.
 */
public class DataSourceCell extends ListCell<DataSource> {
    protected final GridPane grid = new GridPane();
    private final Label nameLabel = new Label("Name:");
    private final Label descriptionLabel = new Label("Description");
    private final Label startOfValueLabel = new Label("Start of Value:");
    private final Label lengthOfValueLabel = new Label("Length of Value:");
    private final Label messageIdLabel = new Label("Message ID");

    private final TextField nameTextField = new TextField("Enter Name here");
    private final TextField descriptionTextField = new TextField("Enter Description here");
    private final TextField startOfValueTextField = new NumberTextField();
    private final TextField lengthOfValueTextField = new NumberTextField();
    private final TextField messageIdTextField = new TextField();

    public DataSourceCell() {
        initGrid();
    }

    /**
     * Sets up the grid and adds the various nodes to it.
     */
    private void initGrid() {
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(nameLabel, 0, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(startOfValueLabel, 0, 2);
        grid.add(lengthOfValueLabel, 0, 3);
        grid.add(messageIdLabel, 0, 4);
        grid.add(nameTextField, 1, 0);
        grid.add(descriptionTextField, 1, 1);
        grid.add(startOfValueTextField, 1, 2);
        grid.add(lengthOfValueTextField, 1, 3);
        grid.add(messageIdTextField, 1, 4);
    }

    @Override
    public void updateItem(DataSource dataSource, boolean empty) {
        super.updateItem(dataSource, empty);
        if (empty) {
            clear();
        } else {
            updateTextFields(dataSource);
            if(dataSource instanceof SimpleSensor){
                //TODO implement
            }
            if(dataSource instanceof State){
                //TODO implement
            }
            if(dataSource instanceof BitFlag){
                //TODO implement
            }
            setGraphic(grid);
        }
    }

    private void updateTextFields(DataSource dataSource) {
        nameTextField.setText(dataSource.getName());
        descriptionTextField.setText(dataSource.getDescription());
        startOfValueTextField.setText(String.valueOf(dataSource.getStartOfValue()));
        lengthOfValueTextField.setText(String.valueOf(dataSource.getLengthOfValue()));
        messageIdTextField.setText(Encoder.encode(dataSource.getMessageId()));
    }

    /**
     * Empties all textfields so they only contain "";
     */
    private void clear() {
        nameTextField.setText("");
        descriptionTextField.setText("");
        startOfValueTextField.setText("");
        lengthOfValueTextField.setText("");
        messageIdTextField.setText("");
    }

}
