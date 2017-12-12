package data.sources;

import data.Point;
import data.TimeUtility;
import javafx.animation.AnimationTimer;
import org.apache.commons.lang3.ArrayUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Kai on 15.08.2017.
 */
public class StringSource extends DataSource{

    @Override
    public String toString() {
        return "STRING: " + name.getValue();
    }

    /**
     * @param bytes the value encoded as bytes.
     */
    @Override
    public void insertValue(byte[] bytes) {
        String value;
        try {
            value = new String(bytes,"US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            value = "FAILED TO DECODE BYTES TO STRING";
        }
        addDataPoint(new Point<>(TimeUtility.getUptimeSec(),value));
        dataLogger.write("UPTIME_SEC;"+TimeUtility.getUptimeSec() + ";" + getName() + value);

    }

    @Override
    public void insertTimedValue(byte[] bytes, long time) {
        //TODO implement
        insertValue(bytes);
    }
}
