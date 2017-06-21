package data;

import data.sources.DataSource;
import data.sources.SimpleSensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kai on 25.05.2017.
 * This class can hold all the data, that is needed to decode incoming data from the serial Port and how to decode it.
 */
public class JsonSerializableConfig {

    public enum ByteEndianity{
        BIG_ENDIAN, LITTLE_ENDIAN
    }

    private ArrayList<TeleCommand> teleCommands;
    /**
     * Contains all available Data Sources like a Temperature-Sensor etc.
     */
    private ArrayList<SimpleSensor> simpleSensors;
    /**
     * The start bytes of a message send through Serial Communication.
     */
    private byte[] startBytes;
    /**
     * The stop bytes of a message send through Serial Port. This will override existing messageLength settings.
     */
    private byte[] stopBytes;
    /**
     * where is the position of the id.
     */
    private int idPosition;
    /**
     * The length of the id's in bytes.
     */
    private int idLength;
    /**
     * The length of a single complete message, in bytes.
     */
    private int messageLength;
    /**
     * The maximum length of a message including everything.
     */
    private int maxMessageLength;
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
    private int timeLength = 0;

    /**
     * Applies to the Endianity of the data.
     */
    private ByteEndianity byteEndianity = ByteEndianity.LITTLE_ENDIAN;

    public List<DataSource> getDataSources() {
        ArrayList<DataSource> dataSources = new ArrayList<>();
        for (SimpleSensor sensor: simpleSensors) {
            dataSources.add(sensor);
        }
        return dataSources;
    }

    /**
     * @return the start Bytes of the Messages we want to receive.
     */
    public byte[] getStartBytes() {
        return startBytes;
    }

    /**
     *
     * @return the stop Bytes of the Message we want to receive. This will override the message Length if the stop Bytes are specified.
     */
    public byte[] getStopBytes() {
        return stopBytes;
    }

    /**
     * All msg must have the same length.
     * @return
     */
    public int getMessageLength() {
        return messageLength;
    }

    /**
     * Gets the maximum message Length.
     * If one uses fixed message length, then max = fixed message length.
     * @return
     */
    public int getMaxMessageLength() {
        return maxMessageLength;
    }

    /**
     * @return Is CRC16 used for the messages. This allows for error correction and detection.
     */
    public boolean isUsingCRC16() {
        return isUsingCRC16;
    }

    public int getTimePosition() {
        return timePosition;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public int getIdPosition() {
        return idPosition;
    }

    public int getIdLength() {
        return idLength;
    }

    public ByteEndianity getByteEndianity(){
        return byteEndianity;
    }

    public List<TeleCommand> getTeleCommands() {
        return teleCommands;
    }
}
