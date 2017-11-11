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
public class BitFlag extends DataSource {

    /**
     * This allows for the thread safe data exchange between the ressources Thread and the serial.SerialCommunicationThread.
     */
    private ConcurrentLinkedQueue<Point<Boolean>> dataQueue = new ConcurrentLinkedQueue<>();
    private List<BitFlagListener> listeners = new ArrayList<>();

    /**
     * The position of the bit for the flag in the bytes that are speccified in the abstract datasource.
     */
    private int bitPosition;

    BitFlag(){
        informListeners();
    }

    @Override
    public String toString(){
        return "FLAG: " + name.getValue();
    }

    public void addListener (BitFlagListener listener) {
        listeners.add(listener);
    }

    public void removeListeners(BitFlagListener toBeRemoved) {
        listeners.remove(toBeRemoved);
    }

    /**
     * gets called in the JavaFX Main thread.
     * Iterates over all Listeners and informs them.
     */
    private void informListeners() {
        BitFlag bitFlag = this;
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (!dataQueue.isEmpty()) {
                    Point pt = dataQueue.remove();
                    for(BitFlagListener listener : listeners){
                        listener.onUpdateData(bitFlag,pt);
                    }
                }
            }
        }.start();
    }

    /**
     * Inserts a new value into this dataSource. Because no time is provided the program up time is used.
     * The reason it gets the raw bytes is that the dataSource itself will know best how to decode it.
     * @param bytes the value encoded as bytes.
     */
    @Override
    public void insertValue(byte[] bytes) {
        boolean rawValue = isBitSet(bytes,bitPosition);
        dataQueue.add(new Point<Boolean>(TimeUtility.getUptimeSec(),rawValue));
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

    /**
     * For testing
     * @return
     */
    public Point<Boolean> pollLastValue(){
        return dataQueue.poll();
    }

}
