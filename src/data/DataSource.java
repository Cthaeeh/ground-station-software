package data;

/**
 * Created by Kai on 27.04.2017.
 * A data-source represents for example a temperature sensor or gyroscope etc.
 */
public class DataSource {

    private String name = "";

    private int currentValue = 0;

    //Fields for identifying this data-source (e.g a temp sensor or whatever) in a data package coming from the serial-port.

    private int packageId = 0;
    private int startOfValue = 0;
    private int lengthOfValue = 0;

    public DataSource(){

    }

    //TODO implement

    @Override
    public String toString(){
        return name + " current val: " + currentValue + " package id:" + packageId;
    }
}