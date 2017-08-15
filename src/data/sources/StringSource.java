package data.sources;

import data.Point;
import data.TimeUtility;
import javafx.animation.AnimationTimer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Kai on 15.08.2017.
 */
public class StringSource extends DataSource{

    private ConcurrentLinkedQueue<Point<String>> dataQueue = new ConcurrentLinkedQueue<>();

    private List<StringSourceListener> listeners = new ArrayList<>();

    public StringSource(){
        informListeners();
    }

    private void informListeners() {
        StringSource stringSource = this;
        new AnimationTimer(){
            @Override
            public void handle(long now){
                while(!dataQueue.isEmpty()) {
                    Point pt = dataQueue.remove();
                    for(StringSourceListener listener : listeners){
                        listener.onUpdateData(stringSource,pt);
                    }
                }
            }
        }.start();
    }

    @Override
    public String toString() {
        return "STRING: " + name.getValue();
    }

    public void addListner(StringSourceListener listener){
        listeners.add(listener);
    }

    public void removeListener(StringSourceListener toBeRemoved) {
        listeners.remove(toBeRemoved);
    }

    /**
     *
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
        dataQueue.add(new Point<>(TimeUtility.getUptimeSec(),value));
        dataLogger.write("UPTIME_SEC;"+TimeUtility.getUptimeSec() + ";" + getName() + value);

    }

    @Override
    public void insertTimedValue(byte[] bytes, long time) {
        //TODO implement
        insertValue(bytes);
    }
}
