package command;

import data.TeleCommand;
import data.params.Parameter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kai on 11.08.2017.
 */
public class ParameterizationControl implements Initializable {


    @FXML
    private ListView<Parameter> parameterList;
    private Sender sender;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init(TeleCommand command){
        initList(command);
    }

    /**
     * Initializes the parameter List.
     * @param command
     */
    private void initList(TeleCommand command) {
        parameterList.setCellFactory(arg0-> new ParameterCell());
        ObservableList<Parameter> observableList = FXCollections.observableList(command.getParameters());
        parameterList.setItems(observableList);
    }

    public void register(Sender sender) {
        this.sender = sender;
    }

    public void btnSendClicked(MouseEvent mouseEvent) {
        //TODO assemble the command from params and stuff.

        sender.send(new byte[]{1,2,3});
        Stage stage = (Stage) parameterList.getScene().getWindow();
        stage.close();
    }
}
