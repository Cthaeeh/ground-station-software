package configEdit;

import data.Config;
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
 *
 * This class gets an immutable "old" config object. So it can show some initial values in the GUI.
 * And a mutable ConfigBuilder object that it can change according to the user input.
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
    @FXML
    public TextField crc16posTcTextfield;
    @FXML
    public TextField crc16posTmTextfield;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Cant't do here much because we do not have the model yet.
    }

    public void initModel(Config oldConfig) {
        initCheckBoxes(oldConfig);
        initTextFields(oldConfig);
    }

    /**
     * Init the Textfields with values from the config.
     */
    private void initTextFields(Config config) {
        timePositionTextField.setText(String.valueOf(config.getTimePosition()));
        maxMessageLengthTextField.setText(String.valueOf(config.getMaxMessageLength()));
        messageLengthTextField.setText(String.valueOf(config.getMessageLength()));
        idLengthTextField.setText(String.valueOf(config.getIdLength()));
        idPositionTextField.setText(String.valueOf(config.getIdPosition()));
        stopBytesTextField.setText(Encoder.encode(config.getStopBytes()));
        startBytesTextField.setText(Encoder.encode(config.getStartBytes()));
        useCrcCheckbox.selectedProperty().setValue(config.isUsingCRC16());
        timeLengthTextField.setText(String.valueOf(config.getTimeLength()));
        crc16posTcTextfield.setText(String.valueOf(config.getCrc16positionTC()));
        crc16posTmTextfield.setText(String.valueOf(config.getCrc16positionTM()));
    }

    /**
     * Init the Checkboxes with values from the config.
     */
    private void initCheckBoxes(Config config) {
        byteEndianityCheckBox.getItems().setAll(Config.ByteEndianity.values());
        byteEndianityCheckBox.getSelectionModel().select(config.getByteEndianity());
        readModeCheckBox.getItems().setAll(Config.ReadMode.values());
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

    public void crc16PosTcKeyTyped(KeyEvent keyEvent) {
    }

    public void crc16PosTmKeyTyped(KeyEvent keyEvent) {

    }
}
