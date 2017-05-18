package data;

import javafx.animation.AnimationTimer;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Kai on 27.04.2017.
 * A data-source represents for example a temperature sensor or gyroscope etc.
 */
public class DataSource {

    private ConcurrentLinkedQueue<Number> dataQueue = new ConcurrentLinkedQueue<>();
    private List<UpdateDataListener> listeners = new ArrayList<>();
    private StringProperty  name;
    private StringProperty description;

    //Fields for identifying this data-source (e.g a temp sensor or whatever) in a data package coming from the serial-port.
    private int packageId = 0;
    private int startOfValue = 0;
    private int lengthOfValue = 0;

    private int fakeTime = 0;       //TODO replace with real time.

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

    @Override
    public String toString(){
        return name.getValue() + " package id:" + packageId;
    }

    public void addListener (UpdateDataListener listener) {
        listeners.add(listener);
    }

    //TODO make this so that multiple graphs can access the datasource.
    public Number getAndRemoveLastVal(){
        return dataQueue.remove();
    }

    /**
     * gets called in the JavaFX Main thread
     */
    private void informListeners() {    //TODO FIND A BETTER NAME:
        DataSource dataSource = this;   //TODO remove this ugliness.
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                for(UpdateDataListener listener : listeners){
                    while (!dataQueue.isEmpty()) {
                        listener.onUpdateData(dataSource,dataQueue.remove(),fakeTime++);
                    }
                }
            }
        }.start();
    }
}