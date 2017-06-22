package main;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by kai on 6/22/17.
 */
public class Encoder {
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
                Main.programLogger.log(Level.WARNING,()-> "unable to encode:  " + text + "  into byte array. Number format exception");
            }
        }
        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[bytes.size()]));
    }
}
