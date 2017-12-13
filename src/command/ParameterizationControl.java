package command;

import data.TeleCommand;
import data.params.Parameter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kai on 11.08.2017.
 */
public class ParameterizationControl implements Initializable {

    @FXML
    private Button sendBtn;
    @FXML
    private ListView<Parameter> parameterList;
    /**
     * The one who can send the command we parametrize here later.
     */
    private Sender sender;
    private TeleCommand command;

    private int focusIndex;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initShortcuts();
        parameterList.setFocusTraversable(false);
        sendBtn.setFocusTraversable(false);
    }

    private void initShortcuts() {
        parameterList.setOnKeyPressed(t -> {
            System.out.println("key pressed");
            if(t.getCode() == KeyCode.ENTER){
                btnSendClicked(null);
            }
            if(t.getCode() == KeyCode.TAB){
                parameterList.getFocusModel().focus(focusIndex);
                if(focusIndex==parameterList.getItems().size()-1){
                    focusIndex = 0;
                }else {
                    focusIndex++;
                }
            }
            System.out.println(focusIndex);
        });

    }

    public void init(TeleCommand command) {
        this.command = command;
        initList(command);
    }

    /**
     * Initializes the parameter List.
     *
     * @param command
     */
    private void initList(TeleCommand command) {
        parameterList.setCellFactory(arg0 -> new ParameterCell());
        ObservableList<Parameter> observableList = FXCollections.observableList(command.getParameters());
        parameterList.setItems(observableList);
    }

    public void register(Sender sender) {
        this.sender = sender;
    }

    public void btnSendClicked(MouseEvent mouseEvent) {
        //TODO assemble the command from params and stuff.
        sender.send(command.getBytes());
        Stage stage = (Stage) parameterList.getScene().getWindow();
        stage.close();
    }
}
