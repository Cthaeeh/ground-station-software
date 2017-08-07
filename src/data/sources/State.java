package data.sources;

import data.Point;
import data.TimeUtility;
import javafx.animation.AnimationTimer;
import main.Main;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * One can think of a state like an enum.
 * For example someone wants to send the state of a state machine via telemetry.
 * Then he can use this class.
 * Created by Kai on 08.06.2017.
 */
public class State extends DataSource {

    //TODO think about having more than one byte as key ( but on the other hand who needs an enum with more than 256 entries ?)
    /**
     * assigns every raw bit/byte encoded state a string.
     */
    private Map<Byte, String> stateMap = new HashMap<>();

    /**
     * This allows for the thread safe data exchange between the ressources Thread and the serial.SerialCommunicationThread.
     */
    private ConcurrentLinkedQueue<Point<String>> dataQueue = new ConcurrentLinkedQueue<>();

    private List<StateListener> listeners = new ArrayList<>();

    /**
     * Added to the queque if the byte is not known to this class that i receives.
     */
    private final static String DEFAULT_STATE ="UNDEFINED";
    public State(){
        informListeners();
    }

    @Override
    public String toString(){
        return "STATE: " + name.getValue();
    }

    /**
     * Inserts a new value into this State. Because no time is provided the program up time is used.
     * Takes out the first byte and looks if we got that one in the stateMap, where the
     * corresponding strings are stored.
     * @param bytes the value encoded as bytes.
     */
    @Override
    public void insertValue(byte[] bytes) {
        Byte rawValue = bytes[0];
        if(stateMap.containsKey(rawValue)){
            String stateString = stateMap.get(rawValue);
            dataQueue.add(new Point<String>(TimeUtility.getUptimeSec(),stateString));
            dataLogger.write("UPTIME_SEC;"+ TimeUtility.getUptimeSec() + ";" + getName() + ";" + stateString);
        }else {
            dataQueue.add(new Point<String>(TimeUtility.getUptimeSec(),DEFAULT_STATE));
            Main.programLogger.log(Level.WARNING,()->"Unable to decode byte :" +  rawValue + "to a State.");
        }
    }


    @Override
    public void insertTimedValue(byte[] bytes, long time) {
        //TODO implement
        insertValue(bytes);
        Main.programLogger.log(Level.SEVERE,"INSERT TIMED VALUE NOT IMPLEMENTED YET");
    }

    public void addListener(StateListener listener) {
        listeners.add(listener);
    }


    public void removeListeners(StateListener listener) {
        listeners.remove(listener);
    }

    private void informListeners() {
        State state = this;
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (!dataQueue.isEmpty()) {
                    Point<String> pt = dataQueue.remove();
                    for(StateListener listener : listeners){
                        System.out.println("UPDATE");
                        listener.onUpdateData(state,pt);
                    }
                }
            }
        }.start();
    }

}
