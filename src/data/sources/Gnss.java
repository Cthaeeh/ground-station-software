package data.sources;

import data.Config;
import data.Point;
import data.TimeUtility;
import javafx.animation.AnimationTimer;
import main.Main;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Created by kai on 12/11/17.
 */
public class Gnss extends DataSource{

    private ConcurrentLinkedQueue<Point<GnssFrame>> dataQueue = new ConcurrentLinkedQueue<>();
    private List<GnssListener> listeners = new ArrayList<>();

    private SimpleSensor latitude;
    private SimpleSensor longitude;
    private SimpleSensor height;
    private SimpleSensor gnssTime;
    private SimpleSensor satellites;
    private SimpleSensor fixQuality;

    //TODO get this form the config.
    private static final Config.ByteEndianity BYTE_ENDIANITY = Config.ByteEndianity.BIG_ENDIAN;

    public Gnss(){
        informListeners();
    }

    public void addListener (GnssListener listener) {
        listeners.add(listener);
    }

    public void removeListeners(GnssListener toBeRemoved) {
        listeners.remove(toBeRemoved);
    }

    /**
     * gets called in the JavaFX Main thread.
     * Iterates over all Listeners and informs them.
     */
    private void informListeners() {
        Gnss gnss = this;   //TODO remove this ugliness.
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (!dataQueue.isEmpty()) {
                    Point<GnssFrame> pt = dataQueue.remove();
                    for(GnssListener listener : listeners){
                        listener.onUpdateData(gnss,pt);
                    }
                }
            }
        }.start();
    }

    @Override
    public String toString() {
        return "GNSS:" + getName();
    }

    @Override
    public void insertValue(byte[] bytes) {
        //TODO do this with post serialization
        SimpleSensor[] sensors = {longitude,latitude};
        for(SimpleSensor sensor : sensors) {
            if (sensor != null) {
                byte[] value = Arrays.copyOfRange(bytes, sensor.getStartOfValue(), sensor.getStartOfValue() + sensor.getLengthOfValue());
                if (BYTE_ENDIANITY == Config.ByteEndianity.LITTLE_ENDIAN) ArrayUtils.reverse(value);
                sensor.insertValue(value);
            }
        }
        addGnssFrame(TimeUtility.getUptimeSec());
    }

    /**
     *  Adds a GnssFrame into the dataQueue by taking the last data from the simple Sensors this GNSS is made off.
     *  Note that this still happens in the SerialCommThread. I know that is not very clear ...
     * @param receiveTime the gnssTime the telemetry was received ( in difference to the gnssTime (time from GPS sat));
     */
    private void addGnssFrame(double receiveTime) {
        GnssFrame gnssFrame = new GnssFrame();
        gnssFrame.setLongitude(longitude != null ? longitude.getLastValue() : 0);
        gnssFrame.setLatitude(latitude != null ? latitude.getLastValue() : 0);
        //TODO create a DATASource for int values.
        // TODO log exceptions maybe before (if lat long are null)
        gnssFrame.setNumOfSatellites(satellites != null ? (int)satellites.getLastValue() : 0);
        gnssFrame.setFixQuality(fixQuality != null ? (int)fixQuality.getLastValue() : -1);
        gnssFrame.setTime(gnssTime != null ? (int) gnssTime.getLastValue(): -1);
        gnssFrame.setHeight(height != null ? height.getLastValue() : -1);

        //TODO log this to DataLogger
        if(! listeners.isEmpty()) dataQueue.add(new Point(receiveTime,gnssFrame));
    }

    @Override
    public void insertTimedValue(byte[] bytes, long time) {
        Main.programLogger.log(Level.SEVERE,"INSERT TIMED VALUE NOT IMPLEMENTED YET");
        insertValue(bytes);
    }
}
