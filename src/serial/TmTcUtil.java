package serial;

import data.Config;
import main.Main;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by kai on 6/27/17.
 * Some Utility for the Telecommand stuff.
 */
public class TmTcUtil {

    private static CRC16 crc16 = new CRC16();

    private TmTcUtil(){}

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

    /**
     * Checks if the CRC 16 is correct for this message.
     * crc16PosTM 0 means the crc is in the first 2 bytes.
     *            1 means the crc is in the 1 and 2 byte etc.
     *            if the position is greater than the message length - 2 the last 2 bytes of the message are used.
     *
     * @param message
     * @param crc16PosTM
     * @return
     */
    public static boolean isCrcValid(byte[] message, int crc16PosTM) {
        int realPos;
        if(crc16PosTM > message.length - 2){    //The crc is found at the last 2 bytes.
            realPos = message.length -2;
        }else {
            realPos = crc16PosTM;
        }
        crc16.reset();
        crc16.update(concatenate(Arrays.copyOfRange(message,0,realPos),Arrays.copyOfRange(message,realPos+2,message.length)));
        int crc16Int = crc16.getValue();                                //Calculate the CRC
        byte[] crc16Bytes = {(byte) (crc16Int>>>8),(byte) (crc16Int)};
        return checkCRC(crc16Bytes,realPos,message);
    }

    /**
     * Checks if the crc16Bytes equal those in the message at the given position.
     * @param crc16Bytes
     * @param pos where the crc16Bytes are found.
     * @param message
     * @return
     */
    private static boolean checkCRC(byte[] crc16Bytes, int pos , byte[] message){
        for(int i = 0; i < 2; i++){
            if(message[i+pos]!=crc16Bytes[i]) return false; // NON valid crc16
        }
        return true;    //Valid CRC16.
    }

}