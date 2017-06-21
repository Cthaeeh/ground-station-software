package serial;

import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import data.JsonSerializableConfig;
import data.sources.DataSource;
import main.Main;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

    //TODO split clearly the code that is running in an extra thread from the rest.
    //TODO make sure that all concurrency stuff is implemented correctly.


/**
 * Created by Kai on 25.05.2017.
 * An extra Thread for handling the serial Port Communication.
 * It can receive and send data.
 * The received data is inserted into the dataModel, as specified in the config file.
 * Additionally this Thread makes its "Health" visible to other threads.
 */
public class SerialCommunicationThread extends Thread {

    private SerialPort serialPort;
    private ConcurrentLinkedQueue<byte[]> commandQueue = new ConcurrentLinkedQueue<>();

    //Message configuration:
    private final byte[] startBytes;
    private final byte[] stopBytes;
    private final int messageLenth;
    private final int maxLenth;
    private final int idPosition;
    private final int idLength;
    private final JsonSerializableConfig.ByteEndianity byteEndianity;
    /**
     * Maps message id's to corresponding lists of sources, that can be found in this kind of message.
     * For example messages with the id 1 always contain temp 1 gyro x,y,z and temp2.
     * In order to be able to use byte arrays as keys and still have meaningful comparision I use the Byte Wrapper that implements exactly that.
     */
    private Map<ByteBuffer, List<DataSource>> messageMap = new HashMap<ByteBuffer, List<DataSource>>();

    //State of this Thread:
    private volatile boolean isRunning = true;
    /**
     * Tells you the last time this Thread was definitively alive.
     */
    private volatile long lastTimeSeen = 0;
    /**
     * The byte rate in bytes per second.
      */
    private volatile double byteRate;
    private final static long BYTE_RATE_UPDATE_TIME = 100000000;
    /**
     * State-machine for decoding the incoming stream of bytes.
     */
    private enum MsgState {
        SEARCHING_START, READING_MSG
    }


    /**
     * Package visible constructor for a SerialCommunicationThread.
     * @param dataModel the thread needs to know stuff how to decode the data it receives from the serial port.
     * @param serialPort the port it should work on (send and receive stuff.)
     */
    SerialCommunicationThread(DataModel dataModel, SerialPort serialPort){
        this.serialPort = serialPort;
        startBytes = dataModel.getConfig().getStartBytes();
        stopBytes = dataModel.getConfig().getStopBytes();
        for(byte b:stopBytes) System.out.print("["+b+"]");
        messageLenth = dataModel.getConfig().getMessageLength();
        maxLenth = dataModel.getConfig().getMaxMessageLength();
        idPosition = dataModel.getConfig().getIdPosition();
        idLength = dataModel.getConfig().getIdLength();
        byteEndianity = dataModel.getConfig().getByteEndianity();
        initMessageMap(dataModel);
    }


    /**
     * This method is, in a way the core of the whole GSS program.
     */
    @Override
    public void run() {
        int messagePointer = startBytes.length; //start reading after the startBytes. I don't need to safe the StartBytes .
        int startByteCounter = 0;               //How many start bytes did I already find.
        int stopByteCounter = 0;
        byte[] msgBuffer = new byte[maxLenth];

        int bytesRead = 0;
        long timeCounter = System.nanoTime();/* counts up to 100 ms to calc the byte rate.*/

        MsgState state = MsgState.SEARCHING_START;
        while(isRunning) {
            byte[] readBuffer = new byte[1];
            int numRead = serialPort.readBytes(readBuffer,1); //Read semi blocking 1 byte.
            if(numRead==1){
                bytesRead++;
                switch (state){
                case SEARCHING_START:
                    if(readBuffer[0]==startBytes[startByteCounter]){
                        startByteCounter++;
                        if(startByteCounter>=startBytes.length){
                            state = MsgState.READING_MSG;
                            startByteCounter = 0;
                        }
                    }else {                                                 //We found no start ;(
                        startByteCounter = 0;
                    }
                    break;
                case READING_MSG:
                    msgBuffer[messagePointer]=readBuffer[0];
                    messagePointer++;
                    if(stopBytes!=null && stopBytes.length > 0){            //stop byte(s) , ofc this could also be done with length dependant on id or msg length somewhere at the start of the message.
                        if(readBuffer[0]==stopBytes[stopByteCounter]) {
                            stopByteCounter++;
                            if (stopByteCounter >= stopBytes.length) {
                                stopByteCounter = 0;
                                state = MsgState.SEARCHING_START;
                                messagePointer = startBytes.length;
                                decodeMessage(msgBuffer);
                            }
                        }
                        else{                                               //We found no end ;(
                            stopByteCounter = 0;
                        }
                    }else {                                                 //Fixed length message.
                        if(messagePointer>=messageLenth){
                            state  = MsgState.SEARCHING_START;
                            messagePointer = startBytes.length;
                            decodeMessage(msgBuffer);
                        }
                    }
                    break;
                }
            }

            byte[] command = commandQueue.poll();
            if(command!=null){
                int numWritten = serialPort.writeBytes(command,command.length);
            }
            lastTimeSeen = System.currentTimeMillis();
            if(System.nanoTime() - timeCounter > BYTE_RATE_UPDATE_TIME){ // some time has passed. Time to calculate the byte rate new.
                byteRate = bytesRead/(((double)(System.nanoTime() - timeCounter))/1000_000_000);
                bytesRead = 0;
                timeCounter = System.nanoTime();
            }
        }
        Main.programLogger.log(Level.INFO,"stopped a SerialCommunicationThread");
        //TODO clean up. THIS IS A MESS.
    }

    /**
     * Initializes the MessageMap with all messageId's as keys and lists of corresponding sources as lists.
     */
    private void initMessageMap(DataModel dataModel) {
        for (DataSource datasource:dataModel.getDataSources()) {
            if(messageMap.containsKey(ByteBuffer.wrap(datasource.getMessageId()))){
                messageMap.get(ByteBuffer.wrap(datasource.getMessageId())).add(datasource);
            }else {     // if the map does not contain the key yet create a new List of datasources, and add it to the map.
                List<DataSource> dataSourceList = new ArrayList<>();
                dataSourceList.add(datasource);
                messageMap.put(ByteBuffer.wrap(datasource.getMessageId()),dataSourceList);
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
        ByteBuffer messageId = ByteBuffer.wrap(Arrays.copyOfRange(msgBuffer, idPosition, idPosition+idLength));
        if(messageMap.get(messageId)!= null){
            for(DataSource source : messageMap.get(messageId)){
                byte[] value = Arrays.copyOfRange(msgBuffer, source.getStartOfValue(), source.getStartOfValue()+source.getLengthOfValue());
                if(byteEndianity == JsonSerializableConfig.ByteEndianity.BIG_ENDIAN) ArrayUtils.reverse(value);
                source.insertValue(value);
                System.out.println(source.getName());
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

    public double getByteRate() {
        return byteRate;
    }

    public boolean isRunningSmooth() {
        if(System.currentTimeMillis() - lastTimeSeen > 1000){
            return false;
        }
        return true;
    }

    public void stopThread(){
        isRunning = false;
        serialPort.closePort();
    }

}
