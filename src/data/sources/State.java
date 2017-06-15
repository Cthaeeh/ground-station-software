package data.sources;

import data.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * One can think of a state like an enum.
 * For example someone wants to send the state of a state machine via telemetry.
 * Then he can use this class.
 * Created by Kai on 08.06.2017.
 */
public class State extends DataSource {

    /**
     * assigns every raw bit/byte encoded state a string.
     */
    Map<Byte, String> stateMap = new HashMap<>();

    /**
     * This allows for the thread safe data exchange between the gui Thread and the serial.SerialCommunicationThread.
     */
    private ConcurrentLinkedQueue<Point<String>> dataQueue = new ConcurrentLinkedQueue<>();

    public State(){
    }

    @Override
    public String toString(){
        return "STATE: " + name.getValue();
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
