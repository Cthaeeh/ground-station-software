package test;

import com.google.gson.Gson;
import data.TeleCommand;
import data.params.Parameter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kai on 28.10.2017.
 * This tests the Telecommands by constructing instances of this class from json.
 * Then the various class methods are executed, especially the getBytes() method to see
 * if everything works fine.
 */
class TeleCommandTest {

    private static String simpleCommand = "{\n"+
           		"\"name\": \"TEST-COMMAND\",\n"+
           		"\"description\": \"h\",\n"+
                "\"commandBasis\": [1,2],\n"+
                "\"length\": 4\n"+
           	    "}\n";

    private static String commandWithParams = "{\n"+
           		"\"name\": \"TEST-COMMAND\",\n"+
           		"\"description\": \"h\",\n"+
                "\"commandBasis\": [1,2],\n"+
                "\"length\": 8,\n"+
                "\"parameters\": [{\n"+
                "\"type\": \"INTEGER\",\n"+
                "\"start\": 2,\n"+
                "\"length\": 4\n"+
                "},{\n"+
                "\"type\": \"FLAG\",\n"+
                "\"start\": 6,\n"+
                "\"length\": 1\n"+
                "},{\n"+
                "\"type\": \"STATE\",\n"+
                "\"start\": 7,\n"+
                "\"length\": 1,\n"+
                "\"stateMap\": {\n"+
                   "\"option1\": [3],\n"+
                   "\"option2\": [5]\n"+
                "}\n"+
                "}]\n"+
           	    "}\n";

    private static TeleCommand simple;
    private static TeleCommand withParams;

    @BeforeAll
    static void setUp(){
        Gson gson = new Gson();
        simple =  gson.fromJson(simpleCommand, TeleCommand.class);
        withParams = gson.fromJson(commandWithParams,TeleCommand.class);
    }

    @Test
    void getBytesSimple() {
        assertArrayEquals(new byte[]{1,2,0,0},simple.getBytes());
    }


    @Test
    void getBytes() {
        List<Parameter> params = withParams.getParameters();
        assertEquals(3,params.size());
        //Set the parameters to some values.
        for(Parameter p : params){
            if(p.getType()== Parameter.Type.INTEGER) p.setInteger(7);
            if(p.getType()== Parameter.Type.FLAG) p.setFlag(false);
            if(p.getType()== Parameter.Type.STATE) p.setState("option2");
        }
        //Check if the constructed Telecommand is correct.
        assertArrayEquals(new byte[]{1,2,0,0,0,7,0,5},withParams.getBytes());
    }

    @Test
    void getName() {
        assertEquals("TEST-COMMAND",simple.getName());
        assertEquals("TEST-COMMAND",withParams.getName());
    }

    @Test
    void getParameters() {
        assertEquals(0,simple.getParameters().size());
        assertEquals(3,withParams.getParameters().size());
    }

    @Test
    void hasParameters() {
        assertFalse(simple.hasParameters());
        assertTrue(withParams.hasParameters());
    }

}