package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.Config;
import data.ConfigChecker;
import data.InvalidConfig;
import data.StringPropertyAdapter;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by kai on 3/6/18.
 */
class ConfigCheckerTest {
    private static String fineConfig = "{\n"+
			 	"\"messageLength\": 3,\n"+
           		"\"timePosition\": 0,\n"+
           		"\"timeLength\": 1,\n"+
                "\"readMode\": \"FIXED_MSG_LENGTH\"\n"+
           	    "}\n";


    private static String timeToFarRight = "{\n"+
			 	"\"messageLength\": 1,\n"+
           		"\"timePosition\": 1,\n"+
           		"\"timeLength\": 1,\n"+
                "\"readMode\": \"FIXED_MSG_LENGTH\"\n"+
           	    "}\n";

    private static String timeToManyBytes = "{\n"+
			 	"\"messageLength\": 10,\n"+
           		"\"timePosition\": 1,\n"+
           		"\"timeLength\": 9,\n"+
                "\"readMode\": \"FIXED_MSG_LENGTH\"\n"+
           	    "}\n";

    @Test
    void timeToFarRight() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyAdapter());
        Gson gson = gsonBuilder.create();
        Config invalid =  gson.fromJson(timeToFarRight, Config.class);
        assertThrows(InvalidConfig.class,()->{ConfigChecker.verification(invalid);});
    }

    @Test
    void timeToManyBytes() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyAdapter());
        Gson gson = gsonBuilder.create();
        Config invalid =  gson.fromJson(timeToManyBytes, Config.class);
        assertThrows(InvalidConfig.class,()->{ConfigChecker.verification(invalid);});
    }

    @Test
    void fineConfig() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyAdapter());
        Gson gson = gsonBuilder.create();
        Config valid =  gson.fromJson(fineConfig, Config.class);
        assertAll(()->ConfigChecker.verification(valid));
    }
}