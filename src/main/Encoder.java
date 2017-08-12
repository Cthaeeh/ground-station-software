package main;

import org.apache.commons.lang3.ArrayUtils;
import serial.TmTcUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by kai on 6/22/17.
 */
public class Encoder {

    //TODO think about how exceptions should be handled.

    /**
     * Encodes a String that contains integer numbers separated by spaces, to a byte array.
     * So for example :
     * "5 15 23 56" will turn into [5][15][23][56]
     * @param text
     * @return
     */
    public static byte[] encode(String text) {
        List<Byte> bytes = new ArrayList();
        String[] numbers = text.split(" ");
        if(numbers == null || numbers.length < 1){
            Main.programLogger.log(Level.WARNING,()->"unable to encode " + text + " into byte array");
        }
        for(String number: numbers){
            try {
                bytes.add(Byte.valueOf(number));
            }catch (NumberFormatException ex){
                Main.programLogger.log(Level.WARNING,()-> "unable to encode:  " + number + "  into byte. Number format exception");
            }
        }
        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[bytes.size()]));
    }

    /**
     * TODO comment this
     * @param bytes
     * @return
     */
    public static String encode(byte[] bytes) {
        if(bytes == null || bytes.length < 1) return "";
        StringBuilder sb = new StringBuilder() ;

        for(byte b: bytes){
            sb.append(String.valueOf(b)+" ");
        }
        return sb.toString();
    }

    /**
     * Turns a string with hex numbers divided by space into a corresponding byte array.
     *
     * @param message
     * @return
     */
    public static byte[] encodeFromHex(String message) {
        List<Byte> bytes = new ArrayList<>();
        String[] numbers = message.split(" ");
        if(numbers == null || numbers.length < 1){
            Main.programLogger.log(Level.WARNING,()-> "Unable to to encode " + message + " to byte array");
        }
        for(String num : numbers){
            try {
                bytes.add((byte)(Integer.parseInt(num,16) & 0xff));
            }catch (NumberFormatException ex){
                Main.programLogger.log(Level.WARNING,()->"Unable to encode: " + num + " byte");
            }
        }

        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[bytes.size()]));
    }

    /**
     * Turns an integer into a byte array.
     * @param message
     * @param length
     * @return
     * @throws NumberFormatException
     */
    public static byte[] encodeInt(int message, int length) {
        byte[] intAsBytes = BigInteger.valueOf(message).toByteArray();
        if(intAsBytes.length > length) throw new NumberFormatException("converting int:" + message + "to byte array will lead to information loss");
        if(intAsBytes.length == length) return intAsBytes;

        //If length is bigger than the amount of bytes needed to represent the int add just zeros bytes.
        byte[] bytes = new byte[length];
        int counter = 0;
        for(int i = length - intAsBytes.length ; i < length ; i++){
            bytes[i] = intAsBytes[counter];
            counter ++;
        }
        return bytes;
    }
}
