package command;

import data.DataModel;
import data.TeleCommand;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Encoder;
import main.Main;
import org.controlsfx.control.GridView;
import serial.SerialPortComm;
import serial.TmTcUtil;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Created by Kai on 09.06.2017.
 */
public class TeleCommandControl implements Initializable {

    private DataModel model;
    private SerialPortComm serialPortComm;
    @FXML
    private GridView<TeleCommand> gridView;

    @FXML
    private ChoiceBox<ENCODING> commandCoiceBox;

    @FXML
    private CheckBox addStartStopBytesCheckBox;

    @FXML
    private CheckBox addCrcCheckBox;

    @FXML
    private TextField inputField;

    private static final String PARAMETERIZATION_FXML = "/gui/parameterization.fxml";

    enum ENCODING {
        ASCII, DECIMAL_DIVIDED_BY_SPACE, HEX_DIVIDED_BY_SPACE
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initChoiceBox();
        initInputField();
        initCheckBoxes();
    }

    private void initCheckBoxes() {
        addStartStopBytesCheckBox.setTooltip(new Tooltip(
                "Add Start/Stop Bytes from the Config to the Command, this will only affect typed commands."));
        addCrcCheckBox.setTooltip(new Tooltip(
                "Add CRC Bytes to the Command, this will only affect typed commands."));
    }

    private void initInputField() {
        inputField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnSendCommand();
            }
        });
    }

    /**
     * Injects the global data Model into this controller.
     *
     * @param model
     */
    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
        initGridView();
    }

    public void initSerialPortComm(SerialPortComm serialPortComm) {
        this.serialPortComm = serialPortComm;
    }

    private void initGridView() {
        gridView.cellWidthProperty().set(200);
        gridView.cellHeightProperty().set(10);
        gridView.setHorizontalCellSpacing(0);
        gridView.setVerticalCellSpacing(8);
        gridView.setCellFactory(arg0 -> {
            TeleCommandGridCell cell = new TeleCommandGridCell();
            cell.setOnMouseClicked(e -> sendPredefinedCommand(cell.getCommand()));
            return cell;
        });
        gridView.setItems(model.getTeleCommands());
    }

    private void sendPredefinedCommand(TeleCommand command) {
        if (command.hasParameters()) openParameterization(command);
        else sendPredefinedCommand(command.getBytes());
    }

    @FXML
    private void btnSendCommand() {
        byte[] command;
        switch (commandCoiceBox.getSelectionModel().getSelectedItem()) {
            case ASCII:
                command = inputField.getText().getBytes(StandardCharsets.US_ASCII);
                sendTypedCommand(command);
                break;
            case DECIMAL_DIVIDED_BY_SPACE:
                command = Encoder.encode(inputField.getText());
                sendTypedCommand(command);
                break;
            case HEX_DIVIDED_BY_SPACE:
                command = Encoder.encodeFromHex(inputField.getText());
                sendTypedCommand(command);
                break;
            default:
                Main.programLogger.log(Level.WARNING, () -> "Could not send Command" + commandCoiceBox.getSelectionModel().getSelectedItem().name() + " because the Endocing is unsuported.");
        }
    }

    /**
     * Adds the CRC16 if needed and the start stop bytes if needed and forwards it to the serialPortCOmm.
     *
     * @param command
     */
    private void sendPredefinedCommand(byte[] command) {
        if (model.getConfig().isUsingCRC16()) {
            command = TmTcUtil.insertCRC(command, model.getConfig().getCrc16positionTC(), model.getConfig().getByteEndianity());
        }
        command = TmTcUtil.concatenate(model.getConfig().getStartBytes(), command, model.getConfig().getStopBytes());
        serialPortComm.send(command);
    }

    private void sendTypedCommand(byte[] command) {
        if (addCrcCheckBox.isSelected()) {
            command = TmTcUtil.insertCRC(command, model.getConfig().getCrc16positionTC(), model.getConfig().getByteEndianity());
        }
        if (addStartStopBytesCheckBox.isSelected()) {
            command = TmTcUtil.concatenate(model.getConfig().getStartBytes(), command, model.getConfig().getStopBytes());
        }
        serialPortComm.send(command);
    }

    private void initChoiceBox() {
        commandCoiceBox.getItems().setAll(ENCODING.values());  //
        commandCoiceBox.getSelectionModel().selectFirst(); //Select first item by default.
    }

    private void openParameterization(TeleCommand command) {
        try {
            final Stage parameterizationDialog = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PARAMETERIZATION_FXML));
            Scene scene = new Scene(loader.load(), 600, 600);
            ParameterizationControl parameterizationControl = loader.getController();
            parameterizationControl.init(command);
            parameterizationControl.register(this::sendPredefinedCommand);
            scene.getStylesheets().add("/gui/darkTheme.css");

            parameterizationDialog.initModality(Modality.APPLICATION_MODAL);
            parameterizationDialog.initOwner(inputField.getScene().getWindow());

            parameterizationDialog.setScene(scene);
            parameterizationDialog.show();

        } catch (IOException e) {
            Main.programLogger.log(Level.WARNING,()-> "Unable to find parameterization fxml. " + e.getMessage());
        }
    }
}
