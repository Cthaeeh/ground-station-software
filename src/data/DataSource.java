package data;

import javafx.animation.AnimationTimer;
import javafx.beans.property.StringProperty;
import javafx.util.Pair;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Kai on 27.04.2017.
 * A data-source represents for example a temperature sensor or gyroscope etc.
 */
public class DataSource {

    private ConcurrentLinkedQueue<Point> dataQueue = new ConcurrentLinkedQueue<>();
    private List<UpdateDataListener> listeners = new ArrayList<>();
    private StringProperty  name;
    private StringProperty description;

    private static final long startTime = System.nanoTime();
    //Fields for identifying this data-source (e.g a temp sensor or whatever) in a data package coming from the serial-port.
    private int packageId = 0;
    private int startOfValue = 0;
    private int lengthOfValue = 0;

    //TODO think about the idea that a dataSource can have more than one messageId + startOfValue , because it could occur in different messages.

    public DataSource(){
        informListeners();
    }

    public StringProperty  getNameProperty(){
        return name;
    }

    public String getName() {
        return name.getValue();
    }

    public StringProperty  getDescriptionProperty() {
        return description;
    }

    public int getMessageId(){
        return packageId;
    }

    public int getStartOfValue(){
        return startOfValue;
    }

    public int getLengthOfValue() {
        return lengthOfValue;
    }

    @Override
    public String toString(){
        return name.getValue() + " message id:" + packageId;
    }

    public void addListener (UpdateDataListener listener) {
        listeners.add(listener);
    }

    /**
     * gets called in the JavaFX Main thread
     */
    private void informListeners() {
        DataSource dataSource = this;   //TODO remove this ugliness.
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (!dataQueue.isEmpty()) {
                    Point pt = dataQueue.remove();
                    for(UpdateDataListener listener : listeners){
                        listener.onUpdateData(dataSource,pt);
                    }
                }
            }
        }.start();
    }

    public void addDataPoint(Number x, Number y){
        dataQueue.add(new Point(x,y));
    }

    /**
     * Inserts a new value into this dataSource. Because no time is provided the program up time is used.
     * The reason it gets the raw bytes is that the dataSource itself will know best how to decode it.
     * @param bytes the value encoded as bytes.
     */
    public void insert(byte[] bytes) {
        int value = ByteBuffer.wrap(bytes).getShort();
        double upTime = ((double)((System.nanoTime() - startTime)/1000000))/1000;
        addDataPoint(upTime,value);
        System.out.println("added: " + value + " " + getName());
    }
}