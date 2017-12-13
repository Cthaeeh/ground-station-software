package command;

import data.TeleCommand;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import org.controlsfx.control.GridCell;

import java.io.IOException;
import java.util.logging.Level;

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
            label.setTooltip(new Tooltip(item.getDescription()));
            label.setStyle("-fx-border-color: " + command.getColorString() +"; -fx-text-fill: " + command.getColorString() +";");
        }
        box.getChildren().addAll(label);
        setGraphic(box);
    }

    public TeleCommand getCommand(){
        return command;
    }

}


