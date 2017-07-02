package command;

import data.TeleCommand;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.controlsfx.control.GridCell;

/**
 * Created by Kai on 09.06.2017.
 */
public class TeleCommandGridCell extends GridCell<TeleCommand> {

    //TODO make this fancy.
    // TODO do not create new objects when update is called.

    private Label label;
    private TeleCommand command;

    @Override
    protected void updateItem(TeleCommand item, boolean empty) {
        super.updateItem(item, empty);
        // create the view;
        HBox box = new HBox();

        label = new Label();
        if (empty) {
            label.setText(" ");
        } else {
            command = item;
            label.setText(item.getName());
        }
        box.getChildren().addAll(label);
        label.setStyle("-fx-border-color: white;");
        setGraphic(box);
    }

    public TeleCommand getCommand(){
        return command;
    }
}
