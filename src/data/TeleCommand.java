package data;

import data.params.Parameter;
import serial.TmTcUtil;

import java.util.ArrayList;

/**
 * Created by Kai on 09.06.2017.
 */
public class TeleCommand {

    private byte[] commandBasis;
    private int length = 0;
    private String name;
    private String description;
    private ArrayList<Parameter> parameters = new ArrayList<>();

    public byte[] getBytes() {
        if(hasParameters()){
            return assembleCommand();
        }else {
            return commandBasis;
        }
    }

    /**
     * Goes throug the params to assemble the whole command.
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

    public ArrayList<Parameter> getParameters(){
        return parameters;
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }
}
