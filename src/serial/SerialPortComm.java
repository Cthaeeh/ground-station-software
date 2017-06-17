package serial;

import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import main.Main;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    private SerialCommunicationThread communicationThread;
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

    public void connect(String portName, int baudRate){
        disconnect();
        setupSerialPort(portName,baudRate);
        if(isConnected) {
            //Reading/writing from/to COM-Port in other Thread so other code doesn´t lag.
            communicationThread = new SerialCommunicationThread(datamodel,serialPort);
            communicationThread.setDaemon(true);    //so it gets killed if the application is closed.
            communicationThread.start();
        }
    }

    /**
     * kill Threads that might listen.
     */
    public void disconnect(){
        if(isConnected){
            communicationThread.stopThread();
        }
    }
    private void setupSerialPort(String portName, int baudRate) {
        Main.programLogger.log(Level.INFO,()->"trying to initialize " + portName +" with baud rate of: " + baudRate);
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);
        //The newReadTimeout parameter affects (in TIMEOUT_READ_BLOCKING-Mode) how long we will wait for a certain amount of bytes to arrive before we say "Fuck it"
        //Credits: https://github.com/Fazecast/jSerialComm/wiki/Blocking-and-Semiblocking-Reading-Usage-Example
        serialPort.setComPortTimeouts( TIMEOUT_READ_SEMI_BLOCKING | TIMEOUT_WRITE_SEMI_BLOCKING, 100, 100);   //TODO make it a least a final static variable or move it to the config.
        if (serialPort.openPort()) {
            Main.programLogger.log(Level.INFO,()->"Connected to: "+portName);
            isConnected = true;
        }else {
            Main.programLogger.log(Level.INFO,()->"Failed to connect to: "+portName);
        }
    }

    public void send(byte[] command) {
        if(communicationThread!=null && communicationThread.isAlive()){
            communicationThread.send(command);
        }
    }

    public int getByteRate() {
        if(communicationThread!=null){
            return communicationThread.getByteRate();
        }
        return 0;
    }

    public String getPort() {
        if(isConnected){
            return serialPort.getDescriptivePortName();
        }else {
            return "no port ";
        }
    }

    public boolean isRunningSmooth() {
        if(communicationThread != null && isConnected){
            return communicationThread.isRunningSmooth();
        }
        return false;
    }
}
