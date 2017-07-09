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
    private static final String colorGreen = "#1d9141";
    private static final String defaultStatus = "NOT CONNECTED";

    @FXML
    private TextField baudRateInput;
    @FXML
    private ChoiceBox COM_PortChoiceBox;
    @FXML
    private Label connectionStatusLabel;
    @FXML
    private Button disconnectButton;

    private SerialPortComm serialPortComm;

    /**
     * This method is called after the standard constructor from JavaFX (We do not need to call it our self, java does it for us)
     */
    @FXML
    public void initialize() {
        initializeBaudRateInput();
    }

    private void initLabel(){
        if(serialPortComm.isConnected){
            connectionStatusLabel.setText("CONNECTED");
            connectionStatusLabel.setTextFill(Color.web(colorGreen));
            return;
        }else {
            connectionStatusLabel.setText(defaultStatus);
            connectionStatusLabel.setTextFill(Color.web(colorRed));
        }
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
            connectionStatusLabel.setTextFill(Color.web(colorRed));
            return;
        }
        serialPortComm.connect(COM_PortChoiceBox.getValue().toString(),baudRate);
        if(serialPortComm.isConnected){
            connectionStatusLabel.setText("CONNECTED");

            connectionStatusLabel.setTextFill(Color.web(colorGreen));
        }else {
            connectionStatusLabel.setText("Connection failed");
            connectionStatusLabel.setTextFill(Color.web(colorRed));
        }
    }

    @FXML
    private void btnShowDataClick(){
        Stage stage = (Stage) COM_PortChoiceBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void btnDisconnectClick(){
        if(serialPortComm.isConnected){
            serialPortComm.disconnect();
            connectionStatusLabel.setText(defaultStatus);
            connectionStatusLabel.setTextFill(Color.web(colorRed));
        }
    }

    public void initCommPortConnection(SerialPortComm serialPortComm) {
        this.serialPortComm = serialPortComm;
        initializeCOM_PortChoiceBox();
        initLabel();
    }
}
