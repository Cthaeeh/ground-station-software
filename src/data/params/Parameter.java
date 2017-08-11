package data.params;

/**
 * Created by Kai on 11.08.2017.
 * A class that represents a single parameter of a Telecommand
 */
public class Parameter {

    public enum Type{
        INTEGER,STATE,FLAG,UNDEFINED;
    }

    private Type type = Type.UNDEFINED;

    private String name;
    /**
     * Where in the Telecommand this parameter is located.
     */
    private int start;

    /**
     * How long is the Parameter in bytes.
     */
    private int length;

    public String getName(){
        return name;
    }

    public Type getType(){
        return type;
    }
}
