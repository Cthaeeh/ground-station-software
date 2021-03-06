package data.sources;

import data.DataLogger;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

/**
 * A DataSource can be a simple temperature sensor, or a GPS sensor, or a bit flag or whatever.
 * But every data Source has a Name, Description, a messageId, a length of value, a start of value.
 * Created by Kai on 27.04.2017.
 */
public abstract class DataSource {

    //Types of datasources
    public enum Type{
        SIMPLE_SENSOR, STATE, BIT_FLAG;

        /**
         * Returns an instance of the corresponding class.
         * @return
         */
        public DataSource getInstance() {
            if(this==SIMPLE_SENSOR) return new SimpleSensor();
            if(this==STATE) return new State();
            if(this==BIT_FLAG)return new BitFlag();
            throw new IllegalStateException("There is no associated class to this enum" + this);
        }
    }

    protected StringProperty name;
    protected StringProperty description;
    protected static final DataLogger dataLogger = new DataLogger();

    //TODO split the Json specfific stuff from the rest
    //Fields for identifying this data-source (e.g a temp sensor or whatever) in a data package coming from the serial-port.
    private byte[] messageId;
    private int startOfValue = 0;
    private int lengthOfValue = 0;

    public StringProperty  getNameProperty(){
        return name;
    }

    public String getName(){return name.getValue();}

    public StringProperty  getDescriptionProperty() {
        return description;
    }

    public byte[] getMessageId(){
        return messageId;
    }

    public int getStartOfValue(){
        return startOfValue;
    }

    public int getLengthOfValue() {
        return lengthOfValue;
    }

    public abstract String toString();

    /**
     * Inserts a new value into this dataSource. Because no time is provided the program up time is used.
     * The reason it gets the raw bytes is that the dataSource itself will know best how to decode it.
     * @param bytes the value encoded as bytes.
     */
    public abstract void insertValue(byte[] bytes);

    /**
     * Inserts a new value into this dataSource.
     * The reason it gets the raw bytes is that the dataSource itself will know best how to decode it.
     * @param bytes the value encoded as bytes.
     */
    public abstract void insertTimedValue(byte[] bytes, long time);

    public String getDescription() {
        return description.getValue();
    }
}