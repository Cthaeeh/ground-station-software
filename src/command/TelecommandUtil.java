package command;

import data.Config;
import main.Main;
import org.apache.commons.lang3.ArrayUtils;
import serial.CRC16;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by kai on 6/27/17.
 * Some Utility for the Telecommand stuff.
 */
public class TelecommandUtil {

    private static CRC16 crc16 = new CRC16();

    private TelecommandUtil(){}

    /**
     * Inserts a CRC16 checksum of the message at the specified position into the message
     * if the position is bigger than the length of the message we insert it at the end.
     * @param message   the message to calc the crc16 for and insert it to.
     * @param position  where to insert the crc16
     * @return the message with the crc inserted.
     */
    public static byte[] insertCRC(byte[] message, int position, Config.ByteEndianity byteEndianity) {
        crc16.reset();
        crc16.update(message);
        int crc16Int = crc16.getValue();
        byte[] crc16Bytes = {(byte) (crc16Int>>>8),(byte) (crc16Int)};
        if(byteEndianity == Config.ByteEndianity.BIG_ENDIAN) ArrayUtils.reverse(crc16Bytes);

        if(position == 0) return concatenate(crc16Bytes,message);
        if(position >= message.length)return concatenate(message,crc16Bytes);
        if(position < 0) return message;    // Do nothing .
        return concatenate(Arrays.copyOfRange(message,0,position),crc16Bytes,Arrays.copyOfRange(message,position,message.length));
    }

    public static byte[] concatenate(byte[]... arrays) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for(byte [] array : arrays){
            try {
                if(array != null) outputStream.write(array);
            } catch (IOException e) {
                Main.programLogger.log(Level.WARNING, ()-> "Failed to concat bytes ");
            }
        }
        return outputStream.toByteArray();
    }
}
