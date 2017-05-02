package data;

import javafx.beans.property.StringProperty;

/**
 * Created by Kai on 27.04.2017.
 * A data-source represents for example a temperature sensor or gyroscope etc.
 */
public class DataSource {

    private StringProperty  name;
    private StringProperty description;

    private int currentValue = 0;

    //Fields for identifying this data-source (e.g a temp sensor or whatever) in a data package coming from the serial-port.

    private int packageId = 0;
    private int startOfValue = 0;
    private int lengthOfValue = 0;

    public DataSource(){
    }

    public StringProperty  getNameProperty(){
        return name;
    }

    public StringProperty  getDescriptionProperty() {
        return description;
    }

    @Override
    public String toString(){
        return name.getValue() + " current val: " + currentValue + " package id:" + packageId;
    }
}