package data.sources;

import data.DataLogger;
import javafx.animation.AnimationTimer;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;



/**
 * A DataSource can be a simple temperature sensor, or a GPS sensor, or a bit flag or whatever.
 * But every data Source has a Name, Description, a messageId, a length of value, a start of value.
 * Created by Kai on 27.04.2017.
 * @param <T> what kind of Data does this source distribute ?
 */
public abstract class DataSource<T> {

    //Types of datasources
    public enum Type {
        SIMPLE_SENSOR, STATE, BIT_FLAG; //TODO add GNSS here // or delete it?

        /**
         * Returns an instance of the corresponding class.
         *
         * @return
         */
        public DataSource getInstance() {
            if (this == SIMPLE_SENSOR) return new SimpleSensor();
            if (this == STATE) return new State();
            if (this == BIT_FLAG) return new BitFlag();
            throw new IllegalStateException("There is no associated class to this enum" + this);
        }
    }

    /**
     * This allows for the thread safe data exchange between the
     * JavaFX Thread and the serial.SerialCommunicationThread.
     **/
    private ConcurrentLinkedQueue<T> dataQueue = new ConcurrentLinkedQueue<>();
    /**
     * Everyone that is listening to this DataSource.
     */
    private List<DataSourceListener<T>> listeners = new ArrayList<>();

    protected StringProperty name;
    protected StringProperty description;
    protected static final DataLogger dataLogger = new DataLogger();

    //TODO split the Json specfific stuff from the rest
    //Fields for identifying this data-source (e.g a temp sensor or whatever) in a data package coming from the serial-port.
    private byte[] messageId;
    private int startOfValue = 0;
    private int lengthOfValue = 0;

    public DataSource() {
        informListeners();
    }

    public void addListener(DataSourceListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListeners(DataSourceListener<T> toBeRemoved) {
        listeners.remove(toBeRemoved);
    }

    /**
     * gets called in the JavaFX Main thread.
     * Iterates over all Listeners and informs them.
     */
    private void informListeners() {
        DataSource dataSource = this;
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (!dataQueue.isEmpty()) {
                    T t = dataQueue.remove();
                    for (DataSourceListener listener : listeners) {
                        //Normally u would write this here, but we are in the AnimationTimer.
                        listener.onUpdateData(dataSource,t);
                    }
                }
            }
        }.start();
    }

    protected void addDataPoint(T t){
        if(! listeners.isEmpty()) dataQueue.add(t);
    }

    public StringProperty getNameProperty() {
        return name;
    }

    public String getName() {
        return name.getValue();
    }

    public StringProperty getDescriptionProperty() {
        return description;
    }

    public byte[] getMessageId() {
        return messageId;
    }

    public int getStartOfValue() {
        return startOfValue;
    }

    public int getLengthOfValue() {
        return lengthOfValue;
    }

    public abstract String toString();

    /**
     * Inserts a new value into this dataSource. Because no time is provided the program up time is used.
     * The reason it gets the raw bytes is that the dataSource itself will know best how to decode it.
     *
     * @param bytes the value encoded as bytes.
     */
    public abstract void insertValue(byte[] bytes);

    /**
     * Inserts a new value into this dataSource.
     * The reason it gets the raw bytes is that the dataSource itself will know best how to decode it.
     *
     * @param bytes the value encoded as bytes.
     */
    public abstract void insertTimedValue(byte[] bytes, long time);

    public String getDescription() {
        return description.getValue();
    }

    /**
     * For testing
     * @return
     */
    public T pollLastValue(){
        return dataQueue.poll();
    }
}