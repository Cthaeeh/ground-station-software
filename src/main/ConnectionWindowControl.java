package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import serial.SerialPortComm;

import java.util.function.UnaryOperator;

/**
 * Created by Kai on 26.01.2017.
 */
public class ConnectionWindowControl
{
    private static final String colorRed = "#c63939";

    //TODO so the user can also reopen this window then u should also show the correct information.

    @FXML
    private TextField baudRateInput;
    @FXML
    private ChoiceBox COM_PortChoiceBox;
    @FXML
    private Label connectionStatusLabel;

    private SerialPortComm serialPortComm;

    /**
     * This method is called after the standard constructor from JavaFX (We do not need to call it our self, java does it for us)
     */
    @FXML
    public void initialize() {
        initializeBaudRateInput();
    }

    private void initializeCOM_PortChoiceBox() {
        ObservableList<String> obList = FXCollections.observableList(serialPortComm.getAvailablePorts());
        COM_PortChoiceBox.getItems().clear();
        COM_PortChoiceBox.setItems(obList);
        COM_PortChoiceBox.setTooltip(new Tooltip("Choose a port here!"));
    }

    /**
     * Will disallow any non integer Inputs on baudRateInput
     */
    private void initializeBaudRateInput(){
        UnaryOperator<TextFormatter.Change> filter = change -> {
            //TODO catch empty Strings without fucking up.
            String changedText = change.getText();
            if (changedText.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        baudRateInput.setTextFormatter(textFormatter);
        baudRateInput.setTooltip(new Tooltip("Enter valid baud rate here (for Example 38400"));
    }

    //TODO split this into more methods
    @FXML
    private void btnConnectClick(){
        //Check for legal baud rate
        int baudRate;
        try{
            baudRate = Integer.valueOf(baudRateInput.getText());
        }catch (Exception ex){
            connectionStatusLabel.setText("Illegal Baud Rate");
            connectionStatusLabel.setTextFill(Color.web(colorRed));
            return;
        }
        //Check for legal port name
        if(COM_PortChoiceBox.getValue()==null || !serialPortComm.getAvailablePorts().contains(COM_PortChoiceBox.getValue().toString())){
            connectionStatusLabel.setText("No Port selected");
            connectionStatusLabel.setTextFill(Color.web("#c63939"));
            return;
        }
        serialPortComm.connect(COM_PortChoiceBox.getValue().toString(),baudRate);
        if(serialPortComm.isConnected){
            connectionStatusLabel.setText("CONNECTED");
            connectionStatusLabel.setTextFill(Color.web(colorRed));
        }else {
            connectionStatusLabel.setText("Connection failed");
            connectionStatusLabel.setTextFill(Color.web(colorRed));
        }
    }

    @FXML
    private void btnShowDataClick(){
        //TODO only do this when we got a valid connection.
        Stage stage = (Stage) COM_PortChoiceBox.getScene().getWindow();
        stage.close();
    }

    public void initCommPortConnection(SerialPortComm serialPortComm) {
        this.serialPortComm = serialPortComm;
        initializeCOM_PortChoiceBox();
    }
}
