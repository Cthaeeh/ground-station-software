package data;

import java.util.ArrayList;

/**
 * Created by Kai on 25.05.2017.
 * This class can hold all the data, that is needed to decode incoming data from the serial Port and how to decode it.
 */
public class JsonSerializableConfig {
    /**
     * Contains all available Data Sources like a Temperature-Sensor etc.
     */
    private ArrayList<DataSource> dataSources;
    /**
     * The start byte of a message send through Serial Communication.
     */
    private byte[] startBytes;
    /**
     * where is the position of the id (must be one byte)
     */
    private int idPosition;
    /**
     * The length of a single complete message, in bytes.
     */
    private int messageLenth;
    /**
     * If the program uses CRC64 to decode its data, enables error detection and correction.
     * If CRC64 is used than it is found at the end of the message with 2 bytes.
     */
    private boolean isUsingCRC16;
    /**
     * the position of the byte(s) that indicate when the measurement was made.
     * Default -1 -> means that no time info is send.
     */
    private int timePosition = -1;
    /**
     * If time info is send the number of bytes that are used to do that.
     */
    private int timeLenth = 0;

    //TODO comment this.

    public ArrayList<DataSource> getDataSources() {
        return dataSources;
    }

    public byte[] getStartBytes() {
        return startBytes;
    }

    public int getMessageLenth() {
        return messageLenth;
    }

    public boolean isUsingCRC16() {
        return isUsingCRC16;
    }

    public int getTimePosition() {
        return timePosition;
    }

    public int getTimeLenth() {
        return timeLenth;
    }

    public int getIdPosition() {
        return idPosition;
    }
}
