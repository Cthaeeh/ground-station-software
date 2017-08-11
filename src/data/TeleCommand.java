package data;

import data.params.Parameter;

import java.util.ArrayList;

/**
 * Created by Kai on 09.06.2017.
 */
public class TeleCommand {

    private byte[] command;
    private String name;
    private String description;
    private ArrayList<Parameter> parameters = new ArrayList<>();

    public byte[] getBytes() {
        return command;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Parameter> getParameters(){
        return parameters;
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }
}
