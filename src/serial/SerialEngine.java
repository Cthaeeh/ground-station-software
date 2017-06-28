package serial;

import com.fazecast.jSerialComm.SerialPort;
import data.JsonSerializableConfig;
import main.Main;

import java.util.logging.Level;

/**
 * The SerialEngines task is to directly read and write to and from the Serial Port.
 * If it finds a Message it will call back to the Listener passed in the constructor to it.
 * There are different ways to find a Message in the incoming bytes.
 * How the reading works is specified in the config file. And can be changed by the user.
 */
public class SerialEngine {

    /**
     * States while Reading the Messages.
     * First we search for Start-Bytes. Then we start Reading the Message.
     * THe we search again for the Start Bytes.
     */
    private enum ReadState {
        SEARCHING_START, READING_MSG
    }

    private final SerialPort serialPort;

    private ReadState readState;
    private JsonSerializableConfig.ReadMode readMode;
    /**
     * Where are we inside the Message ? At the first Byte or where ?
     */
    private int messagePointer = 0;
    /**
     * How many start Bytes did we find yet ?
     */
    private int startByteCounter = 0;
    /**
     * How many stop Bytes did we find yet.
     */
    private int stopByteCounter = 0;
    /**
     * Number of Bytes read.
     */
    private int numBytesRead = 0;
    /**
     * The Listener that wants to get informed about new messages.
     */
    private final MessageListener msgListener;
    private final byte[] startBytes;
    private final byte[] stopBytes;
    private final int fixedMsgLength;
    /**
     * Buffer where the message is stored.
     */
    private final byte[] msgBuffer;

    SerialEngine(MessageListener listener, SerialPort serialPort, JsonSerializableConfig config) {
        this.serialPort = serialPort;
        this.msgListener = listener;
        startBytes = config.getStartBytes();
        stopBytes = config.getStopBytes();
        fixedMsgLength = config.getMessageLength();
        msgBuffer = new byte[config.getMaxMessageLength() + stopBytes.length];
        readState = ReadState.SEARCHING_START;
        readMode = config.getReadMode();
    }

    /**
     * Read one byte from the serial Port .
     * And react appropriate:
     * This means first search for the start Bytes. If they were found read the message.
     */
    void readByte() {
        byte[] readBuffer = new byte[1];
        int numRead = serialPort.readBytes(readBuffer, 1);       //Read semi blocking 1 byte.
        if (numRead == 1) {
            numBytesRead++;
            switch (readState) {
                case SEARCHING_START:
                    searchStartBytes(readBuffer[0]);
                    break;
                case READING_MSG:
                    readMessageByte(readBuffer[0]);
                    break;
            }
        }

    }

    /**
     * Write the bytes to the serial Port
     *
     * @param message the message to write.
     * @return if the method succeeded to send this to the serial port.
     */
    boolean writeBytes(byte[] message) {
        int numWritten = serialPort.writeBytes(message, message.length);
        return numWritten == message.length;
    }

    public void stop() {
        Main.programLogger.log(Level.INFO,"Closed Serial Port.");
        serialPort.closePort();
    }

    /**
     * @return the bytes read by this engine since its creation or if you call reset since last reset.
     */
    int getBytesRead() {
        return numBytesRead;
    }

    void resetBytesRead() {
        numBytesRead = 0;
    }

    /**
     * Method to be called when we are searching for Start Bytes.
     * Will look if we found the start / all start Byte and set Msg State accordingly.
     */
    private void searchStartBytes(byte byteRead) {
        if (byteRead == startBytes[startByteCounter]) {                     // We found the start Byte we are currently looking for.
            startByteCounter++;
            if (startByteCounter >= startBytes.length) {                    // Found all the start bytes.
                readState = ReadState.READING_MSG;
                startByteCounter = 0;
            }
        } else {                                                            //We found no start byte ;(
            startByteCounter = 0;
        }
    }

    private void readMessageByte(byte byteRead) {
        switch (readMode) {
            case FIXED_MSG_LENGTH:
                fixedMsgLengthMode(byteRead);
                break;
            case STOP_BYTES:
                stopByteMode(byteRead);
                break;
            default:
                Main.programLogger.log(Level.WARNING, "This read Mode is not supported yet. We will fail to read from the Serial Port.");
        }

    }

    private void fixedMsgLengthMode(byte byteRead) {
        msgBuffer[messagePointer] = byteRead;
        messagePointer++;
        if (messagePointer >= fixedMsgLength) {
            readState = ReadState.SEARCHING_START;
            messagePointer = 0;
            msgListener.processMessage(msgBuffer);
        }
    }

    private void stopByteMode(byte byteRead) {
        msgBuffer[messagePointer] = byteRead;
        messagePointer++;
        if (messagePointer >= msgBuffer.length) {
            Main.programLogger.log(Level.INFO, "Did not found stop Bytes in time. ");
            messagePointer = 0;
            return;
        } else {
            if (byteRead == stopBytes[stopByteCounter]) {
                stopByteCounter++;
                if (stopByteCounter >= stopBytes.length) {
                    stopByteCounter = 0;
                    readState = ReadState.SEARCHING_START;
                    messagePointer = startBytes.length;
                    msgListener.processMessage(msgBuffer);
                }
            } else {                                               //We found no end ;(
                stopByteCounter = 0;
            }
        }
    }
}
