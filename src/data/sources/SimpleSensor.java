package data.sources;

import data.Point;
import javafx.animation.AnimationTimer;
import main.Main;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Created by Kai on 27.04.2017.
 * A simple Sensor, represents for example a temperature sensor or gyroscope etc.
 */
public class SimpleSensor extends DataSource {

    /**
     * This allows for the thread safe data exchange between the gui Thread and the serial.SerialCommunicationThread.
     */
    private ConcurrentLinkedQueue<Point<Number>> dataQueue = new ConcurrentLinkedQueue<>();
    private List<SimpleSensorListener> listeners = new ArrayList<>();

    private String unit;
    /**
     * An Offset we add to the raw value we get from the telemetry.
     */
    private double offset = 0.0;
    /**
     *  A proportional constatnt the raw value is multiplied with to get the correct value ( in SI-Units, CGS or whatever )
     */
    private double proportionalFactor = 1.0;

    private static final long START_TIME = System.nanoTime();

    //TODO think about the idea that a dataSource can have more than one messageId + startOfValue , because it could occur in different messages.

    public SimpleSensor(){
        informListeners();
    }

    public String getUnit(){
        return unit;
    }

    public void addListener (SimpleSensorListener listener) {
        listeners.add(listener);
    }

    public void removeListeners(SimpleSensorListener toBeRemoved) {
        listeners.remove(toBeRemoved);
    }

    /**
     * gets called in the JavaFX Main thread.
     * Iterates over all Listeners and informs them.
     */
    private void informListeners() {
        SimpleSensor sensor = this;   //TODO remove this ugliness.
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (!dataQueue.isEmpty()) {
                    Point<Number> pt = dataQueue.remove();
                    for(SimpleSensorListener listener : listeners){
                        listener.onUpdateData(sensor,pt);
                    }
                }
            }
        }.start();
    }

    private void addDataPoint(Number x, Number y){
        dataQueue.add(new Point(x,y));
    }

    @Override
    public String toString() {
        return "SIMPLE SENSOR: " + getName() + " unit: " + unit;
    }

    /**
     * Inserts a new value into this dataSource. Because no time is provided the program up time is used.
     * The reason it gets the raw bytes is that the dataSource itself will know best how to decode it.
     * It can apply a proportional constant and an offset.
     * @param bytes the value encoded as bytes.
     */
    @Override
    public void insertValue(byte[] bytes) {
        int rawValue;
        double upTimeSek = ((double)((System.nanoTime() - START_TIME)/1000000))/1000;   //simple way to get uptime in sec in the format of X.XXX sek.
        switch (bytes.length){
            case 1:
                rawValue = bytes[0];
                break;
            case 2:
                rawValue = ByteBuffer.wrap(bytes).getShort();
                break;
            case 3:
                rawValue = new BigInteger(bytes).intValue();
                break;
            case 4:
                rawValue = ByteBuffer.wrap(bytes).getInt();
                break;
            default:
                Main.programLogger.log(Level.WARNING,   ()->"Failed to insert raw byte value into Simple Sensor " + getName()  + System.lineSeparator() +
                                                            ",because the passed byte array was to big to put it into an integer. ");
                return;
        }
        double value = (((double) rawValue )* proportionalFactor) + offset;
        addDataPoint(upTimeSek,value);
        dataLogger.write("UPTIME_SEC;"+ upTimeSek + ";" + getName() + ";" + value + ";"+ unit + ";" + rawValue + ";");
    }

    @Override
    public void insertTimedValue(byte[] bytes, long time) {
        //TODO implement
        insertValue(bytes);
    }

}
