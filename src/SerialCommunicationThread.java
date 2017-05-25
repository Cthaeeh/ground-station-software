import com.fazecast.jSerialComm.SerialPort;
import data.DataModel;

import java.util.logging.Level;

/**
 * Created by Kai on 25.05.2017.
 */
public class SerialCommunicationThread extends Thread {

    private SerialPort serialPort;
    private DataModel dataModel;

    //Message configuration
    private byte startBytes[];
    private int messageLenth;

    private boolean isRunning = true;

    private enum MsgState {
        SEARCHING_START, READING_MSG;
    }

    public SerialCommunicationThread(DataModel datamodel, SerialPort serialPort){
        this.serialPort = serialPort;
        this.dataModel = datamodel;
        initialize();
    }

    @Override public void run() {
        int messagePointer = 0;
        int startByteCounter = 0;
        byte[] msgBuffer = new byte[messageLenth];

        MsgState state = MsgState.SEARCHING_START;
        while(isRunning) {
            byte[] readBuffer = new byte[1];
            serialPort.readBytes(readBuffer,1); //Read semi blocking 1 byte.
            switch (state){
                case SEARCHING_START:
                    if(readBuffer[0]==startBytes[startByteCounter++]){
                        if(startByteCounter>=messageLenth){
                            state = MsgState.READING_MSG;
                            startByteCounter = 0;
                        }
                    }
                    break;
                case READING_MSG:
                    msgBuffer[messagePointer]=readBuffer[0];
                    messagePointer++;
                    if(messagePointer>messageLenth){
                        state  = MsgState.SEARCHING_START;
                        messagePointer = 0;
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


    private void decodeMessage(byte[] msgBuffer) {
        //TODO if crc16 is used decode it with CRC16 class.
        //TODO depending on id check for new data for the data sources.
    }

}
