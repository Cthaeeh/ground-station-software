package data.sources;

import data.Point;
import data.TimeUtility;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Kai on 04.06.2017.
 * For sending flags as telemetry.
 */
public class BitFlag extends DataSource<Point<Boolean>> {

    /**
     * The position of the bit for the flag in the bytes that are speccified in the abstract datasource.
     */
    private int bitPosition;

    @Override
    public String toString(){
        return "FLAG: " + name.getValue();
    }

    /**
     * Inserts a new value into this dataSource. Because no time is provided the program up time is used.
     * The reason it gets the raw bytes is that the dataSource itself will know best how to decode it.
     * @param bytes the value encoded as bytes.
     */
    @Override
    public void insertValue(byte[] bytes) {
        boolean rawValue = isBitSet(bytes,bitPosition);
        addDataPoint(new Point<>(TimeUtility.getUptimeSec(),rawValue));
        dataLogger.write("UPTIME_SEC;"+ TimeUtility.getUptimeSec() + ";" + getName() + ";" + rawValue);
    }

    /**
     * @param bytes
     * @param pos position of the bit we are interested in.
     * @return the pos at the position as boolean
     */
    private static boolean isBitSet(byte[] bytes, int pos)
    {
        byte b = (byte) (((byte) 1) << 7-(pos%8));
        return (bytes[pos/8] & b) != 0;
    }

    @Override
    public void insertTimedValue(byte[] bytes, long time) {
        //TODO implement
        insertValue(bytes);
    }

}
