import data.DataModel;
import data.SerialPortComm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;
import java.util.logging.Level;

/**
 * Created by Kai on 26.01.2017.
 */
public class ConnectionWindowControl
{
    private DataModel model;

    @FXML
    private TextField baudRateInput;
    @FXML
    private ChoiceBox COM_PortChoiceBox;
    @FXML
    private Label connectionStatusLabel;

    private SerialPortComm serialPortComm = new SerialPortComm();

    private static final String DATA_VISUALIZATION_FXML = "gui/main_window.fxml";

    /**
     * This method is called after the standard constructor from JavaFX (We do not need to call it our self, java does it for us)
     */
    @FXML
    public void initialize() {
        initializeBaudRateInput();
        initializeCOM_PortChoiceBox();
    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
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
        //TODO only do this when we got a valid connection.
        try{
            Stage dataStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DATA_VISUALIZATION_FXML));
            Scene scene = new Scene(loader.load(), 600, 600);
            MainWindowControl dataVisualizationControl = loader.getController();
            dataVisualizationControl.initModel(model);


            scene.getStylesheets().add("gui/darkTheme.css");
            dataStage.setTitle("groundstation-software 0.1");
            dataStage.setScene(scene);
            dataStage.setMinWidth(1000);
            dataStage.setMinHeight(800);
            dataStage.setMaximized(true);
            dataStage.show();

            //CLOSE this one.
            Stage connectionWindowStage = (Stage) baudRateInput.getScene().getWindow();
            connectionWindowStage.close();
        }catch (Exception ex){
            Main.logger.log(Level.WARNING, "Failed to load : " + DATA_VISUALIZATION_FXML);
            System.out.println(ex);
        }
    }
}
