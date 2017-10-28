package data.params;

import main.Encoder;
import main.Main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Kai on 11.08.2017.
 * A class that represents a single parameter of a Telecommand
 * For some programming reasons a Parameter Object can be a flag param , a int param, a state param.
 * Reasons( If I use Parameter as a base class and derive Stateparam etc from it I would have to use special Serializer Deserializers for it)
 * Because gson doesnt handle polymorpy right away in the correct way ( the info about the class is lost ).
 * On the other hand I could use 3 extra classes for each param type but then I could not have them all in the same listView.
 */
public class Parameter {


    public enum Type{
        INTEGER,STATE,FLAG,UNDEFINED
    }

    private Type type = Type.UNDEFINED;

    private boolean flag;
    private String state;
    private int intValue;
    private Map<String,byte[]> stateMap = new HashMap<>();

    /**
     * Name of the parameter, z.B temp for calibration 1
     */
    private String name;
    /**
     * Where in the Telecommand this parameter is located.
     */
    private int start;
    /**
     * How long is the Parameter in bytes.
     */
    private int length;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setInteger(int integer) {
        this.intValue = integer;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }


    public String getName(){
        return name;
    }

    public Type getType(){
        return type;
    }

    public Map<String, byte[]> getStateMap() {
        return stateMap;
    }

    public byte[] getBytes(){   //TODO make this more robust to shitty config files.
        switch (type){
            case INTEGER:
                if(length != 4) Main.programLogger.log(Level.WARNING,"A int param with a length != 4, is discouraged. I hope u know what u do! (The param is shortend / enlarged to the desired length)");
                return Encoder.encodeInt(intValue,length);
            case FLAG:
                byte[] bytes= new byte[length];
                Arrays.fill(bytes,(byte) (flag ? 1 : 0));
                return bytes;
            case STATE:
                if(stateMap.get(state).length!= length) Main.programLogger.log(Level.WARNING, "A State param has a resulting byte array length != the specified length, undefined behavior");
                return stateMap.get(state);
            default:
                return new byte[]{};
        }
    }
}
