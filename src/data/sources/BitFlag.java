package data.sources;

import data.Point;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Kai on 04.06.2017.
 */
public class BitFlag extends DataSource {

    /**
     * This allows for the thread safe data exchange between the gui Thread and the serial.SerialCommunicationThread.
     */
    private ConcurrentLinkedQueue<Point<Boolean>> dataQueue = new ConcurrentLinkedQueue<>();
    private List<BitFlagListener> listeners = new ArrayList<>();

    //TODO add bitflag specific stuff.

    private static final long START_TIME = System.nanoTime();


    public BitFlag(){
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
        BitFlag bitFlag = this;   //TODO remove this ugliness.
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
     * It can apply a proportional constant and an offset.
     * @param bytes the value encoded as bytes.
     */
    @Override
    public void insertValue(byte[] bytes) {
        //TODO implement
    }

    @Override
    public void insertTimedValue(byte[] bytes, long time) {
        //TODO implement
        insertValue(bytes);
    }
}
