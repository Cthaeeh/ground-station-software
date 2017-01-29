import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

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

    /**
     * This method is called after the standard constructor from JavaFX (We do not need to call it our self, java does it for us)
     */
    @FXML
    public void initialize() {
        initializeBaudRateInput();
        initializeCOM_PortChoiceBox();
    }

    private void initializeCOM_PortChoiceBox() {
        ObservableList obList = FXCollections.observableList(serialPortComm.getAvailablePorts());
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
        int baud_rate;
        try{
            baud_rate = Integer.valueOf(baudRateInput.getText());
        }catch (Exception ex){
            connectionStatusLabel.setText("Illegal Baud Rate");
            connectionStatusLabel.setTextFill(Color.web("#c63939"));
            return;
        }
        //Check for legal port name
        if(COM_PortChoiceBox.getValue()==null || !serialPortComm.getAvailablePorts().contains(COM_PortChoiceBox.getValue().toString())){
            connectionStatusLabel.setText("No Port selected");
            connectionStatusLabel.setTextFill(Color.web("#c63939"));
            return;
        }
        serialPortComm.connect(COM_PortChoiceBox.getValue().toString(),baud_rate);
        if(serialPortComm.isConnected){
            connectionStatusLabel.setText("CONNECTED");
            connectionStatusLabel.setTextFill(Color.web("#39c66b"));
        }else {
            connectionStatusLabel.setText("Connection failed");
            connectionStatusLabel.setTextFill(Color.web("#c63939"));
        }
    }

    @FXML
    private void btnShowDataClick(){
        //TODO open new Window here when connection is established
    }
}
