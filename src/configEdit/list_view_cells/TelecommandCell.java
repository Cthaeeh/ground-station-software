package configEdit.list_view_cells;

import data.TeleCommand;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import main.Encoder;

/**
 * Created by kai on 7/2/17.
 * For visualizing and editing a telecommand in a ListView this Cell class can be used.
 */
public class TelecommandCell extends ListCell<TeleCommand> {

    //TODO implement saving off data with Builder pattern ???
    private final GridPane grid = new GridPane();
    private final Label nameLabel = new Label("Name:");
    private final Label bytesLabel = new Label("Bytes:");
    private final TextField nameTextField = new TextField("Enter Name here");
    private final TextField bytesTextField = new TextField();   //TODO Later use a byte array text field.

    public TelecommandCell (){
        initGrid();
    }

    /**
     * Sets up the grid and adds the various nodes to it.
     */
    private void initGrid() {
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5,5,5,5));
        grid.add(nameLabel,0,0);
        grid.add(bytesLabel,0,1);
        grid.add(nameTextField,1,0);
        grid.add(bytesTextField,1,1);
    }

    @Override
    public void updateItem(TeleCommand command, boolean empty){
        super.updateItem(command,empty);
        if(empty){
            nameTextField.setText("Enter Name here!");
            bytesTextField.setText("");
        }else {
            nameTextField.setText(command.getName());
            bytesTextField.setText(Encoder.encode(command.getBytes()));
            setGraphic(grid);
        }
    }

}
