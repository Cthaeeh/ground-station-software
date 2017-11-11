package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.StringPropertyAdapter;
import data.sources.BitFlag;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test for the Bitflag.
 * Unfortunately only the logic and name, description are tested.
 * I have no good idea how to test the AnimationTimer which informs the listeners.
 */
class BitFlagTest {

    //Json to create a Bitflag wth bitPos, name, description.
    private static String flagBit5 = "{\n"+
           		"\"name\": \"TEST-BITFLAG\",\n"+
           		"\"description\": \"test\",\n"+
                "\"bitPosition\": 5\n"+
           	    "}\n";
    private static String flagBit0 = "{\n"+
           		"\"name\": \"TEST-BITFLAG\",\n"+
           		"\"description\": \"test\",\n"+
                "\"bitPosition\": 0\n"+
           	    "}\n";

    private static String flagBit8 = "{\n"+
           		"\"name\": \"TEST-BITFLAG\",\n"+
           		"\"description\": \"test\",\n"+
                "\"bitPosition\": 8\n"+
           	    "}\n";

    private static String flagBit15 = "{\n"+
           		"\"name\": \"TEST-BITFLAG\",\n"+
           		"\"description\": \"test\",\n"+
                "\"bitPosition\": 15\n"+
           	    "}\n";

    private static BitFlag bitFlag5,bitFlag0,bitFlag8,bitFlag15;

    @BeforeAll
    static void setUp(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyAdapter());
        Gson gson = gsonBuilder.create();
        bitFlag5 =  gson.fromJson(flagBit5, BitFlag.class);
        bitFlag0 =  gson.fromJson(flagBit0, BitFlag.class);
        bitFlag8 =  gson.fromJson(flagBit8, BitFlag.class);
        bitFlag15 =  gson.fromJson(flagBit15, BitFlag.class);
    }

    @Test
    void bitAtPos5() {
        bitFlag5.insertValue(new byte[]{0,0,0});
        assertEquals(false, bitFlag5.pollLastValue().y);
        bitFlag5.insertValue(new byte[]{0b00000100,0b00000000});
        assertEquals(true, bitFlag5.pollLastValue().y);
    }

    @Test
    void bitAtPos8() {
        bitFlag8.insertValue(new byte[]{0,0,0});
        assertEquals(false, bitFlag8.pollLastValue().y);
        bitFlag8.insertValue(new byte[]{0b00000000,-127});
        assertEquals(true, bitFlag8.pollLastValue().y);
    }

    @Test
    void bitAtPos0() {
        bitFlag0.insertValue(new byte[]{0,0,0});
        assertEquals(false, bitFlag0.pollLastValue().y);

        //Note that 0b1000 0000 will not be accepted because it can not be casted to a byte without information loss
        // (256 is out of the range of a byte).
        bitFlag0.insertValue(new byte[]{-127,0b00000000});
        assertEquals(true, bitFlag0.pollLastValue().y);
    }

     @Test
    void bitAtPos15() {
        bitFlag15.insertValue(new byte[]{0,0,0});
        assertEquals(false, bitFlag15.pollLastValue().y);

        bitFlag15.insertValue(new byte[]{0b00000000,0b00000001});
        assertEquals(true, bitFlag15.pollLastValue().y);
    }

    @Test
    void nameDescription(){
        assertEquals("TEST-BITFLAG",bitFlag0.getName());
        assertEquals("test",bitFlag0.getDescription());
    }
}