package config_edit;

import data.DataModel;
import data.JsonSerializableConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import main.Encoder;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kai on 25.06.2017.
 * The task of this class is to bind the GUI-Elements to the corresponding fields in the model.
 */
public class GeneralConfigControl implements Initializable{

    @FXML
    private ChoiceBox byteEndianityCheckBox;
    @FXML
    private TextField timePositionTextField;
    @FXML
    private TextField maxMessageLengthTextField;
    @FXML
    private TextField messageLengthTextField;
    @FXML
    private TextField idLengthTextField;
    @FXML
    private TextField idPositionTextField;
    @FXML
    private TextField stopBytesTextField;
    @FXML
    private TextField startBytesTextField;
    @FXML
    private CheckBox useCrcCheckbox;
    @FXML
    private ChoiceBox readModeCheckBox;
    @FXML
    private TextField timeLengthTextField;

    private JsonSerializableConfig config;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Cant't do here much because we do not have the model yet.
    }

    public void initModel(DataModel model) {
        this.config = model.getConfig();
        initCheckBoxes();
        initTextFields();
    }


    private void initTextFields() {
        timePositionTextField.setText(String.valueOf(config.getTimePosition()));
        maxMessageLengthTextField.setText(String.valueOf(config.getMaxMessageLength()));
        messageLengthTextField.setText(String.valueOf(config.getMessageLength()));
        idLengthTextField.setText(String.valueOf(config.getIdLength()));
        idPositionTextField.setText(String.valueOf(config.getIdPosition()));
        stopBytesTextField.setText(Encoder.encode(config.getStopBytes()));
        startBytesTextField.setText(Encoder.encode(config.getStartBytes()));
        useCrcCheckbox.selectedProperty().setValue(config.isUsingCRC16());
        timeLengthTextField.setText(String.valueOf(config.getTimeLength()));
    }

    private void initCheckBoxes() {
        byteEndianityCheckBox.getItems().setAll(JsonSerializableConfig.ByteEndianity.values());
        byteEndianityCheckBox.getSelectionModel().select(config.getByteEndianity());
        readModeCheckBox.getItems().setAll(JsonSerializableConfig.ReadMode.values());
        readModeCheckBox.getSelectionModel().select(config.getReadMode());
    }

    public void useCrcClicked(MouseEvent mouseEvent) {

    }

    public void startBytesKeyTyped(KeyEvent keyEvent) {

    }

    public void stopBytesKeyTyped(KeyEvent keyEvent) {

    }

    public void IdPositionKeyTyped(KeyEvent keyEvent) {

    }

    public void IdLengthKeyTyped(KeyEvent keyEvent) {

    }

    public void MessageLengthKeyTyped(KeyEvent keyEvent) {

    }

    public void MaxMessageLengthKeyTyped(KeyEvent keyEvent) {

    }

    public void timePositionKeyTyped(KeyEvent keyEvent) {

    }

    public void timeLengthKeyTyped(KeyEvent keyEvent) {

    }
}
