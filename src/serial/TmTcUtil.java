package serial;

import data.Config;
import main.Main;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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
        Main.programLogger.log(Level.INFO, ()-> "The CRC should be: " +crc16Int + " as bytes:  [" + crc16Bytes[0] + "][" + crc16Bytes[1] + "]");
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
        Main.programLogger.log(Level.INFO,()->"CRC was" + new BigInteger(new byte[]{message[pos],message[pos+1]}) + " ,as bytes: [" + message[pos] + "][" + message[pos+1] + "]");
        for(int i = 0; i < 2; i++){
            if(message[i+pos]!=crc16Bytes[i]) return false; // NON valid crc16
        }
        return true;    //Valid CRC16.
    }

    /**
     * Inserts byte arr2 into byte arr1 at start. with
     * @param arr1
     * @param arr2
     * @param start
     * @param length
     */
    public static void insertBytes(byte[] arr1, byte[] arr2, int start, int length) {   //TODO make this a lot safer.
        if(start+length > arr1.length || start < 0 || start + length < 0){
            Main.programLogger.log(Level.WARNING,"unable to insert bytes Length:" +length + "Start: "+ start + " Arr1 length:"+ arr1.length + " Arr2 length:" + arr2.length ); //TODO longer error message how to handle this.
            return;
        }
        if(length>arr2.length){
            Main.programLogger.log(Level.WARNING,"arr2 smaller than length");
        }

        int arr2Index = 0;
        for(int i = start; i < start + length; i++ ){
            arr1[i] = arr2[arr2Index];
            arr2Index ++;
        }
    }

}
