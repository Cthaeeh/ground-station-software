package data;

import main.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

/**
 * Created by Kai on 08.06.2017.
 * Simple class that allows to write continuously to a file.
 */
public class DataLogger {

    private FileWriter fileWriter;
    String timeStamp = new SimpleDateFormat("dd.MM.yyyy_HHmmss").format(Calendar.getInstance().getTime());
    private final String pathToFile = "logs/DataLog_"+ timeStamp +".log";

    public DataLogger(){
        try {
            fileWriter = new FileWriter(new File(pathToFile),true);
        }catch (IOException ex){
            Main.programLogger.log(Level.SEVERE,"Failed to create a FileWriter to write to: " + pathToFile );
        }
    }

    public void write(String message){
        System.out.println("Filewriter:"+ message);
        if(fileWriter != null){
            try {
                fileWriter.write(message + System.lineSeparator());
            }catch (IOException ex){
                Main.programLogger.log(Level.SEVERE,()->"Failed to write to data log" + ex);
            }
        }
    }

    public void append(String message){
        System.out.println("Filewriter:"+ message);
        if(fileWriter != null){
            try {
                fileWriter.write(message);
            }catch (IOException ex){
                Main.programLogger.log(Level.SEVERE,()->"Failed to write to data log" + ex);
            }
        }
    }
}
