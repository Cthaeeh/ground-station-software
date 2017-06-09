package main;

import data.DataModel;
import data.TeleCommand;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.controlsfx.control.GridView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kai on 09.06.2017.
 */
public class TeleCommandControl implements Initializable {

    private DataModel model ;

    @FXML
    private GridView<TeleCommand> gridView;

    @FXML
    private TextField inputField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Injects the global data Model into this controller.
     * @param model
     */
    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
        initGridView();
    }

    private void initGridView() {
        gridView.cellWidthProperty().set(100);
        gridView.cellHeightProperty().set(50);
        gridView.setCellFactory(arg0 -> {
            TeleCommandGridCell cell = new TeleCommandGridCell();
            cell.setOnMouseClicked( e -> sendCommand(cell.getCommand().getBytes()));
            return cell;});
        gridView.setItems(model.getTeleCommands());
    }

    @FXML
    private void btnSendCommand(){
        //TODO send the message to the SerialCommunicationThread somehow.
    }

    private void sendCommand(byte[] command){
        System.out.println("try to send command");
    }
}
