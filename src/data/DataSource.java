package data;

import javafx.beans.property.StringProperty;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Kai on 27.04.2017.
 * A data-source represents for example a temperature sensor or gyroscope etc.
 */
public class DataSource {

    private ConcurrentLinkedQueue<Number> dataQueque = new ConcurrentLinkedQueue<>();
    private StringProperty  name;
    private StringProperty description;

    //Fields for identifying this data-source (e.g a temp sensor or whatever) in a data package coming from the serial-port.
    private int packageId = 0;
    private int startOfValue = 0;
    private int lengthOfValue = 0;

    public DataSource(){
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

    @Override
    public String toString(){
        return name.getValue() + " package id:" + packageId;
    }

    //TODO make this so that multiple graphs can access the datasource.
    public Number getAndRemoveLastVal(){
        return dataQueque.remove();
    }

    public boolean isEmpty() {
        return dataQueque.isEmpty();
    }
}