package serial;

import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import data.Config;
import data.sources.DataSource;
import data.sources.SimpleSensor;
import data.sources.StringSource;
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
    private final Config.ByteEndianity byteEndianity;
    private final boolean useCRC16TM ;
    private final int CRC16PosTM ;
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
        //TODO make this smoother becauuse it is not clear why the pos must be greater 0;
        useCRC16TM = (dataModel.getConfig().isUsingCRC16() && dataModel.getConfig().getCrc16positionTM() > 0);
        CRC16PosTM = dataModel.getConfig().getCrc16positionTM();
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

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
        sb.append(String.format("%02x", b));
        return sb.toString();
    }
    /**
     * If CRC 16 is used checks it and if its okay decodes the message.
     * @param message
     */
    @Override
    public void processMessage(byte[] message) {
        System.out.println("message" + toString(message));
        System.out.println(byteArrayToHex(message));
        if(useCRC16TM){
            if(TmTcUtil.isCrcValid(message,CRC16PosTM)){
                decodeMessage(message);
            }else {
                Main.programLogger.log(Level.WARNING,()->"Got a corrupted message (CRC not valid)");
            }
        }else {
            decodeMessage(message);
        }
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
        ByteBuffer messageId = ByteBuffer.wrap(Arrays.copyOfRange(msgBuffer, idPosition, idPosition+idLength));
        System.out.println("Whole message: " + toString(msgBuffer));
        if(messageMap.get(messageId)!= null){
            for(DataSource source : messageMap.get(messageId)){
                byte[] value = Arrays.copyOfRange(msgBuffer, source.getStartOfValue(), source.getStartOfValue()+source.getLengthOfValue());

                if(byteEndianity == Config.ByteEndianity.LITTLE_ENDIAN && (source instanceof SimpleSensor)) ArrayUtils.reverse(value);
                System.out.println(source.getName() + " " + toString(value));
                source.insertValue(value);
            }
        }
        //TODO add time information.
    }

    public static String toString(byte[] msgBuffer) {
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
        Main.programLogger.log(Level.INFO,()->"Message: "+toString(command));
    }

    public double getByteRate() {
        return byteRate;
    }

    public boolean isRunningSmooth() {
        return System.currentTimeMillis() - lastTimeSeen <= 1000;
    }

    public void stopThread(){
        isRunning = false;
    }
}
