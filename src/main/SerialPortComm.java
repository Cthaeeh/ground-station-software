package main;

import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import main.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_SEMI_BLOCKING;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_SEMI_BLOCKING;

/**
 * Created by Kai on 26.01.2017.
 * Handles connection to the serial port.
 */
public class SerialPortComm {

    private SerialPort serialPort;
    public boolean isConnected = false;
    private DataModel datamodel;

    public SerialPortComm(DataModel datamodel){
        this.datamodel = datamodel;
    }

    /**
     *
     * @return A list of descriptive available Portnames
     */
    public List<String> getAvailablePorts(){
        List<String> portList = new ArrayList<>();
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort port: ports){
            portList.add(port.getSystemPortName());
        }
        return portList;
    }

    public void connect(String portName, int baud_rate){
        setupSerialPort(portName,baud_rate);
        if(isConnected) {
            //Reading from COM-Port in other Thread so other code doesn´t lag.
            SerialCommunicationThread thread = new SerialCommunicationThread(datamodel,serialPort);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void setupSerialPort(String portName, int baud_rate) {
        Main.programLogger.log(Level.INFO,"trying to initialize " + portName +" with baud rate of: " + baud_rate);
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baud_rate);
        //The newReadTimeout parameter affects (in TIMEOUT_READ_BLOCKING-Mode) how long we will wait for a certain amount of bytes to arrive before we say "Fuck it"
        //Credits: https://github.com/Fazecast/jSerialComm/wiki/Blocking-and-Semiblocking-Reading-Usage-Example
        serialPort.setComPortTimeouts( TIMEOUT_READ_SEMI_BLOCKING | TIMEOUT_WRITE_SEMI_BLOCKING, 100, 100);   //TODO make it a least a final static variable or move it to the config.
        if (serialPort.openPort()) {
            Main.programLogger.log(Level.INFO,"Connected to: "+portName);
            isConnected = true;
        }else {
            Main.programLogger.log(Level.INFO,"Failed to connect to: "+portName);
        }
    }

}
