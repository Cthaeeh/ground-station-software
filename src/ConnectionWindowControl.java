

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.function.UnaryOperator;

/**
 * Created by Kai on 26.01.2017.
 */
public class ConnectionWindowControl
{
    @FXML
    private TextField baudRateInput;
    @FXML
    private ChoiceBox COM_PortChoiceBox;
    @FXML
    private Label connectionStatusLabel;

    private SerialPortComm serialPortComm = new SerialPortComm();

    @FXML
    public void initialize() {
        initializeBaudRateInput();
        initializeCOM_PortChoiceBox();
    }

    private void initializeCOM_PortChoiceBox() {
        ObservableList obList = FXCollections.observableList(serialPortComm.getAvailablePorts());
        COM_PortChoiceBox.getItems().clear();
        COM_PortChoiceBox.setItems(obList);
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

    @FXML
    private void btnConnectClick(){
        //TODO split this method up its too big ..
        int baud_rate;  //Check for legal baud rate
        try{
            baud_rate = Integer.valueOf(baudRateInput.getText());
        }catch (Exception ex){
            connectionStatusLabel.setText("Illegal Baud Rate");
            return;
        }   //Check for legal port name
        if(COM_PortChoiceBox.getValue()==null || !serialPortComm.getAvailablePorts().contains(COM_PortChoiceBox.getValue().toString())){
            connectionStatusLabel.setText("No Port selected");
            return;
        }
        serialPortComm.connect(COM_PortChoiceBox.getValue().toString(),baud_rate);
        if(serialPortComm.isConnected){
            connectionStatusLabel.setText("CONNECTED");
        }else {
            connectionStatusLabel.setText("Connection failed");
        }
    }
}
