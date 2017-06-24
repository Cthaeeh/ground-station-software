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
 * Additionally this Thread makes its "Health" visible to other threads, although there is no Watchdog that would kill it :D (yet !!!)
 */
public class SerialCommunicationThread extends Thread implements MessageListener{

    private ConcurrentLinkedQueue<byte[]> commandQueue = new ConcurrentLinkedQueue<>();
    private final SerialEngine serialEngine;

    //Message configuration:
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
    private final static long BYTE_RATE_UPDATE_TIME_NS = 100_000_000;

    /**
     * Package visible constructor for a SerialCommunicationThread.
     * @param dataModel the thread needs to know stuff how to decode the data it receives from the serial port.
     * @param serialPort the port it should work on (send and receive stuff.)
     */
    SerialCommunicationThread(DataModel dataModel, SerialPort serialPort){
        idPosition = dataModel.getConfig().getIdPosition();
        idLength = dataModel.getConfig().getIdLength();
        byteEndianity = dataModel.getConfig().getByteEndianity();
        initMessageMap(dataModel);
        serialEngine = new SerialEngine(this,serialPort,dataModel.getConfig());
    }

    /**
     * This method is, in a way the core of the whole GSS program.
     */
    @Override
    public void run() {
        long timeCounter = System.nanoTime();/* counts up to 100 ms to calc the byte rate.*/
        while(isRunning) {
            //Read
            serialEngine.readByte();
            //Send
            byte[] command = commandQueue.poll();
            if(command!=null){
                boolean success = serialEngine.writeBytes(command);
                if(!success) Main.programLogger.log(Level.WARNING, () -> "Failed to send the command " + toString(command) + "to the serial Port.");
            }
            //Update Thread state /health.
            lastTimeSeen = System.currentTimeMillis();
            if(System.nanoTime() - timeCounter > BYTE_RATE_UPDATE_TIME_NS){ // some time has passed. Time to calculate the byte rate new.
                byteRate = serialEngine.getBytesRead()/(((double)(System.nanoTime() - timeCounter))/1000_000_000);
                serialEngine.resetBytesRead();
                timeCounter = System.nanoTime();
            }
        }
        serialEngine.stop();
        Main.programLogger.log(Level.INFO,"stopped a SerialCommunicationThread");
    }

    @Override
    public void processMessage(byte[] message) {
        decodeMessage(message);
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
                System.out.println("Start of Value: " + source.getStartOfValue() + "  " + toString(msgBuffer));
                if(byteEndianity == JsonSerializableConfig.ByteEndianity.BIG_ENDIAN) ArrayUtils.reverse(value);
                source.insertValue(value);
            }
        }
        //TODO add time information.
    }

    private String toString(byte[] msgBuffer) {
        StringBuilder sb = new StringBuilder();
        for(byte b: msgBuffer){
             sb.append("[" + b + "]");
        }
        return sb.toString();
    }

    private String toString(ByteBuffer buffer) {
        byte [] bytes = buffer.array();
        StringBuilder sb = new StringBuilder();
        for(byte b: bytes){
             sb.append("[" + b + "]");
        }
        return sb.toString();
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
    }
}
