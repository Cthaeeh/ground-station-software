package main;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import serial.SerialPortComm;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kai on 23.04.2017.
 * The control class for the status area, where textually
 * the status of the connection to ComPort
 */
public class StatusAreaControl implements Initializable {

    //TODO move the color definitions to some other place. So they can be changed at one place ez pz.
    private static final String colorWhite = "#e8e8e8";
    private static final String colorRed = "#c63939";
    private static final String colorGreen = "#1d9141";

    private SerialPortComm serialPortComm;

    @FXML
    private Label threadStatusLabel;

    @FXML
    private Label byteRateLabel;

    @FXML
    private Label comPortLabel;

    private static final long UPDATE_PERIOD_NS = 100_000_000;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void initSerialPortComm(SerialPortComm serialPortComm) {
        this.serialPortComm = serialPortComm;

        startTimer();
    }

    private void startTimer() {
        AnimationTimer timer =new AnimationTimer(){

            private long lastUpdate = 0 ;
            @Override
            public void handle(long now) {
                    if (now - lastUpdate >= UPDATE_PERIOD_NS) {
                        updateStatus();
                        lastUpdate = now ;
                    }
            }
        };
        timer.start();
    }

    private void updateStatus() {
        updateByteRateLabel();
        updateComPortLabel();
        updateThreadStatusLabel();
    }

    private void updateThreadStatusLabel(){
        if(serialPortComm.isRunningSmooth()){
            threadStatusLabel.setText("SMOOTH");
        }
        else{
            threadStatusLabel.setText("NOT SMOOTH");
        }

    }

    /**
     * Update the Com Port Label either with the Name of the Port we are currently connected to
     * Or "Not CONNECTED"
     */
    private void updateComPortLabel() {
        if(serialPortComm.isConnected){
            comPortLabel.setText(serialPortComm.getPort());
            comPortLabel.setTextFill(Color.web(colorWhite));
        }else {
            comPortLabel.setText("NOT CONNECTED");
            comPortLabel.setTextFill(Color.web(colorRed));
        }
    }

    /**
     * Updates the ByteRate Label according to the Information the serialPortComm supplies.
     */
    private void updateByteRateLabel() {
        double byteRate = serialPortComm.getByteRate();
        if(byteRate<1000.0){
            byteRateLabel.setText((int)byteRate + " byte/s");
            return;
        }
        if(byteRate<1000000.0){
            byteRateLabel.setText((int)byteRate/1000.0 + " Kilobyte/s");
            return;
        }
        if(byteRate<1000000000.0){
            byteRateLabel.setText((int)byteRate/1000000.0 + " Megabyte/s");
            return;
        }
        byteRateLabel.setText((int)byteRate/1000000000.0 + " Gigabyte/s");
    }

}
