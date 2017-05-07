import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Kai on 26.01.2017.
 * Handles connection to the serial port
 */
public class SerialPortComm {

    private SerialPort serialPort;
    public boolean isConnected = false;

    public SerialPortComm(){

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
            //Actually not needed here but important for later use.
            Thread thread = new Thread(){
                @Override public void run() {
                    boolean isRunning = true;
                    while(isRunning) {
                        byte[] readBuffer = new byte[10];
                        int numRead = serialPort.readBytes(readBuffer,readBuffer.length);
                        Main.logger.log(Level.INFO, "Read " + numRead + " bytes!");
                        Main.logger.log(Level.INFO, "Decoded msg:" + decodeMsg(readBuffer));
                    }
                    Main.logger.log(Level.INFO,"Stopped transmitting");
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void setupSerialPort(String portName, int baud_rate) {
        Main.logger.log(Level.INFO,"trying to initialize " + portName +" with baud rate of: " + baud_rate);
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baud_rate);
        //The newReadTimeout parameter affects (in TIMEOUT_READ_BLOCKING-Mode) how long we will wait for a certain amount of bytes to arrive before we say "Fuck it"
        //Credits: https://github.com/Fazecast/jSerialComm/wiki/Blocking-and-Semiblocking-Reading-Usage-Example
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1100,0);
        if (serialPort.openPort()) {
            Main.logger.log(Level.INFO,"Connected to"+portName);
            isConnected = true;
        }else {
            Main.logger.log(Level.INFO,"Failed to connect to "+portName);
        }
    }

    private String decodeMsg(byte[] readBuffer) {
        if(readBuffer.length<10){
            return "Unreadable";
        }
        //Temp 1 byte 0-1
        //Temp 2 byte 2-3
        //Humidity 1 byte 4-5
        //Humidity 2 byte 6-7
        int temp1 = ((readBuffer[0] & 0xff) << 8) | (readBuffer[1] & 0xff);
        int temp2 = ((readBuffer[2] & 0xff) << 8) | (readBuffer[3] & 0xff);
        int humid1 = ((readBuffer[4] & 0xff) << 8) | (readBuffer[5] & 0xff);
        int humid2 = ((readBuffer[6] & 0xff) << 8) | (readBuffer[7] & 0xff);
        return "T1: " + temp1 +"°C   T2: " + temp2 + "°C   H1: " + humid1 + "%   H2: " + humid2+"%";
    }
}
