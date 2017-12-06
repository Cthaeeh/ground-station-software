package data;

import data.params.Parameter;
import serial.TmTcUtil;
import java.util.ArrayList;

/**
 * Created by Kai on 09.06.2017.
 * The TeleCommand - Class can be viewed as a Template for a real Telecommand.
 * So lets say you want to have a Telecommand where you send some Temperature Calibration.
 * Then this Command would have for example a Name : "Calibrate Temp Sensor 3000"
 * And one Parameter ( The temperature ) . And since maybe the param would be 4 bytes the commandBasis ( or ID
 * if you want to call it that ) would be 2 bytes the length of the command would be 6 bytes.
 */
public class TeleCommand {

    /**
     * The never changing part of the Telecommand. So mainly used for the ID.
     */
    private byte[] commandBasis;
    private int length = 0;
    private String name;
    private String description;
    private ArrayList<Parameter> parameters = new ArrayList<>();

    /**
     * If the Command only has the commandBasis (and no parameters) returns
     * a byte array with the length of the command and the commandBasis bytes as content.
     * (Might be filled up with zeros, or some commandBasis bytes are thrown away.
     * @return the bytes of the command in the current state (These can change because the Parameters might be edited)
     */
    public byte[] getBytes() {
        if(hasParameters()){
            return assembleCommand();
        }else {
            byte[] command = new byte[length];
            //Copy the command basis over.
            for(int i = 0; i < command.length ; i++){
                if(i<commandBasis.length) command[i] = commandBasis[i];
            }
            return command;
        }
    }

    /**
     * Goes through the params to assemble the whole command.
     * @return the command as byte array.
     */
    private byte[] assembleCommand() {
        byte[] command = new byte[length];
        for(Parameter param : parameters){
            System.out.println(param.getName());
            System.out.println("Param true length" + param.getBytes().length);
            TmTcUtil.insertBytes(command,param.getBytes(),param.getStart(),param.getLength());
        }

        TmTcUtil.insertBytes(command,commandBasis,0,commandBasis.length);
        return command;
    }

    public String getName() {
        return name;
    }

    public String getDescription(){
        return description;
    }

    public ArrayList<Parameter> getParameters(){
        return parameters;
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }
}
