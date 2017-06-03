import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;
import data.DataSource;

import java.util.*;

/**
 * Created by Kai on 25.05.2017.
 */
public class SerialCommunicationThread extends Thread {

    private SerialPort serialPort;
    private DataModel dataModel;

    //Message configuration
    private byte startBytes[];
    private int messageLenth;
    private int idPosition;
    /**
     * maps message ids to corresponding lists dataSources, which can be found in this message.
     */
    private Map<Integer,List<DataSource>> messageMap = new HashMap<>();

    private boolean isRunning = true;

    /**
     * State-machine for decoding the incoming stream of bytes.
     */
    private enum MsgState {
        SEARCHING_START, READING_MSG
    }

    public SerialCommunicationThread(DataModel datamodel, SerialPort serialPort){
        this.serialPort = serialPort;
        this.dataModel = datamodel;
        initialize();
    }

    @Override public void run() {
        int messagePointer = startBytes.length; // it is unnecessary to read the startBytes AND msgLength is still lentgh of the message including the startBytes.
        int startByteCounter = 0;
        byte[] msgBuffer = new byte[messageLenth];

        MsgState state = MsgState.SEARCHING_START;
        while(isRunning) {
            byte[] readBuffer = new byte[1];
            serialPort.readBytes(readBuffer,1); //Read semi blocking 1 byte.
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
            //TODO make responsivity of this thread visible to the outside world. GUI thread wants to know if this thread is running smoothly or not.
        }
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
     * Initializes the MessageMap with all messageId's as keys and lists of corresponding dataSources as lists.
     */
    private void initMessageMap() {
        for (DataSource datasource:dataModel.getDataSources()) {
            if(messageMap.containsKey(datasource.getMessageId())){
                messageMap.get(datasource.getMessageId()).add(datasource);
            }else {     // if the map does not contain the key yet create a new List of datasources, and add it to the map.
                List<DataSource> dataSourceList = new ArrayList<>();
                dataSourceList.add(datasource);
                messageMap.put(datasource.getMessageId(),dataSourceList);
                System.out.println("got here");
            }
        }
        System.out.println("Message Map :" + messageMap);
    }

    /**
     * Decodes the message, without start Byte.
     * @param msgBuffer
     */
    private void decodeMessage(byte[] msgBuffer) {
        //TODO if crc16 is used decode it with CRC16 class.
        //TODO depending on id check for new data for the data sources.
        Integer messageId = Integer.valueOf(msgBuffer[idPosition]);
        if(messageMap.get(messageId)!= null){
            for(DataSource dataSource : messageMap.get(messageId)){
                System.out.println("got here");
                dataSource.insert(Arrays.copyOfRange(msgBuffer, dataSource.getStartOfValue(), dataSource.getStartOfValue()+dataSource.getLengthOfValue()));
            }
        }
        //TODO add time information.
    }
}
