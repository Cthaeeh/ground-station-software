package data;

import data.sources.DataSource;
import data.sources.SimpleSensor;
import serial.SerialEngine;

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

    /**
     * The read mode determines how the end of a message is found.
     */
    public enum ReadMode {
        FIXED_MSG_LENGTH, STOP_BYTES, HEADER_INFORMATION;
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
     * where is the position of the id. The Position is relative to the first msg byte (not including start stop bytes.)
     */
    private int idPosition;
    /**
     * The length of the id's in bytes.
     */
    private int idLength;
    /**
     * The length of a single complete message, in bytes. Only used for the FIXED_MSG_LENGTH ReadMode.
     */
    private int messageLength;
    /**
     * The maximum length of a message including everything, but start and stop bytes. THis is only used for all ReadMode s.
     */
    private int maxMessageLength;
    /**
     * If the program uses CRC64 to decode its data, enables error detection and correction.
     * If CRC64 is used than it is found at the end of the message with 2 bytes.
     */
    private boolean isUsingCRC16;
    /**
     * the position of the byte(s) that indicate when the measurement was made.
     * The Position is relative to the first msg byte (not including start stop bytes.)
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

    /**
     *  How the End of a message is found:
     *  FIXED MSG LENGTH: just read a previously set number of bytes after u found a start Byte.
     *      This is somewhat error prone because what if in between the message is skipped because of bad connection
     *      So it should be used with CRC - 16 only.
     *  STOP BYTES: just wait for the stop bytes to appear. But if more bytes are read than in maxMessageLength is specified the message is discarded.
     */
    private ReadMode readMode = ReadMode.FIXED_MSG_LENGTH;

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

    public ReadMode getReadMode() {
        return readMode;
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
