package serial;

import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import data.sources.DataSource;
import main.Main;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Created by Kai on 25.05.2017.
 */
public class SerialCommunicationThread extends Thread {

    //TODO split clearly the code that is running in an extra thread from the rest.

    private SerialPort serialPort;
    private DataModel dataModel;

    private ConcurrentLinkedQueue<byte[]> commandQueue = new ConcurrentLinkedQueue<>();

    //Message configuration
    private byte[] startBytes;
    private int messageLenth;
    private int idPosition;
    /**
     * Maps message id's to corresponding lists of sources, that can be found in this kind of message.
     * For example messages with the id 1 always contain temp 1 gyro x,y,z and temp2.
     */
    private Map<Integer,List<DataSource>> messageMap = new HashMap<>();

    private boolean isRunning = true;

    /**
     * State-machine for decoding the incoming stream of bytes.
     */
    private enum MsgState {
        SEARCHING_START, READING_MSG
    }

    public void stopThread(){
        isRunning = false;
        serialPort.closePort();
    }

    /**
     * Package visible constructor for a SerialCommunicationThread.
     * @param datamodel the thread needs to know stuff how to decode the data it receives from the serial port.
     * @param serialPort the port it should work on (send and receive stuff.)
     */
    SerialCommunicationThread(DataModel datamodel, SerialPort serialPort){
        this.serialPort = serialPort;
        this.dataModel = datamodel;
        initialize();
    }


    @Override
    public void run() {
        int messagePointer = startBytes.length; // it is unnecessary to read the startBytes AND msgLength is still lentgh of the message including the startBytes.
        int startByteCounter = 0;
        byte[] msgBuffer = new byte[messageLenth];

        MsgState state = MsgState.SEARCHING_START;
        while(isRunning) {
            byte[] readBuffer = new byte[1];
            int numRead = serialPort.readBytes(readBuffer,1); //Read semi blocking 1 byte.
            if(numRead==1){
                switch (state){
                case SEARCHING_START:
                    if(readBuffer[0]==startBytes[startByteCounter]){
                        startByteCounter++;
                        if(startByteCounter>=startBytes.length){
                            state = MsgState.READING_MSG;
                            startByteCounter = 0;
                        }
                    }
                    break;
                case READING_MSG:
                    msgBuffer[messagePointer]=readBuffer[0];
                    messagePointer++;
                    if(messagePointer>=messageLenth){
                        state  = MsgState.SEARCHING_START;
                        messagePointer = startBytes.length;
                        decodeMessage(msgBuffer);
                    }
                    break;
                }
            }

            byte[] command = commandQueue.poll();
            if(command!=null){
                int numWritten = serialPort.writeBytes(command,command.length);
            }

            //TODO make responsivity of this thread visible to the outside world. GUI thread wants to know if this thread is running smoothly or not.
        }
        Main.programLogger.log(Level.INFO,"stopped a SerialCommunicationThread");
        //TODO clean up.
    }

    /**
     * Sets everything up so we can start to listen to incoming Messages.
     */
    private void initialize(){
        startBytes = dataModel.getConfig().getStartBytes();
        messageLenth = dataModel.getConfig().getMessageLenth();
        idPosition = dataModel.getConfig().getIdPosition();
        initMessageMap();
    }

    /**
     * Initializes the MessageMap with all messageId's as keys and lists of corresponding sources as lists.
     */
    private void initMessageMap() {
        for (DataSource datasource:dataModel.getDataSources()) {
            if(messageMap.containsKey(datasource.getMessageId())){
                messageMap.get(datasource.getMessageId()).add(datasource);
            }else {     // if the map does not contain the key yet create a new List of datasources, and add it to the map.
                List<DataSource> dataSourceList = new ArrayList<>();
                dataSourceList.add(datasource);
                messageMap.put(datasource.getMessageId(),dataSourceList);
            }
        }
        Main.programLogger.log(Level.INFO,()->"Message Map :" + messageMap);
    }

    /**
     * Decodes the message, without start Byte.
     * @param msgBuffer
     */
    private void decodeMessage(byte[] msgBuffer) {
        //TODO if crc16 is used decode it with CRC16 class.
        Integer messageId = Integer.valueOf(msgBuffer[idPosition]);
        if(messageMap.get(messageId)!= null){
            for(DataSource dataSource : messageMap.get(messageId)){
                dataSource.insertValue(Arrays.copyOfRange(msgBuffer, dataSource.getStartOfValue(), dataSource.getStartOfValue()+dataSource.getLengthOfValue()));
            }
        }
        //TODO add time information.
    }

    /**
     * Send these Bytes to the Serial Port.
     * @param command the bytes to send.
     */
    public void send(byte[] command) {
        commandQueue.add(command);
        Main.programLogger.log(Level.INFO,()->"Commands in Queue: "+commandQueue.size());
    }

    public int getByteRate() {
        //TODO implement
        return  1000000;
    }

    public boolean isRunningSmooth() {
        return true;
        //TODO implement
    }


}
