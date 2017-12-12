package data.sources;

import data.Point;
import data.TimeUtility;
import main.Main;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 * Created by Kai on 27.04.2017.
 * A simple Sensor, represents for example a temperature sensor or gyroscope etc.
 */
public class SimpleSensor extends DataSource<Point<Number>> {

    /** Most up to date value the dataSource has to offer **/
    private double lastValue;

    private String unit;
    /**
     * An Offset we add to the raw value we get from the telemetry.
     */
    private double offset = 0.0;
    /**
     *  A proportional constatnt the raw value is multiplied with to get the correct value ( in SI-Units, CGS or whatever )
     */
    private double proportionalFactor = 1.0;

    public String getUnit(){
        return unit;
    }

    public double getOffset() {
        return offset;
    }

    public double getPropotionalFactor() {
        return proportionalFactor;
    }

    public double getLastValue(){
        return lastValue;
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
        lastValue = value;
        addDataPoint(new Point<>(TimeUtility.getUptimeSec(),value));
        dataLogger.write("UPTIME_SEC;"+ TimeUtility.getUptimeSec() + ";" + getName() + ";" + value + ";"+ unit + ";" + rawValue + ";");
    }

    @Override
    public void insertTimedValue(byte[] bytes, long time) {
        //TODO implement
        Main.programLogger.log(Level.SEVERE,"INSERT TIMED VALUE NOT IMPLEMENTED YET");
        insertValue(bytes);
    }

}
