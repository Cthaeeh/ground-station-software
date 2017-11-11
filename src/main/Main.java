package main;

import com.sun.javafx.application.LauncherImpl;
import data.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Sets up a logger and shows a splash screen while MyApplication is loading.
 */
public class Main {

    // Thinks that need to be done, before releasing 0.1:
    // Features:

        // TODO advanced logging of data of raw data.
        // TODO find a convenient way to edit a config file.
        // TODO add time functionality. (Watch out for time incontinuitity with the graphs)

    //Fixes

        // TODO go through all program logs and see that they make sense.
        // TODO fix the data logger.
        // TODO HELP button doesn't work on linux.
        // TODO Check if GBC really does the right thing. (Because I still see DataSourceSelectionDialog Objects even when the corresponding window is closed ???) Maybe the general way how I open and close windows is wrong.
        // TODO move this whole listener thing to the abstract class DataSource this is now ULTRA STUPID.
        // TODO NOTE that an uneven number of bytes for a value is discouraged.
        // TODO add junit test everywhere. U can even test the serial stuff with linux and virtual ports.
        // TODO somehow detect invalid configs.
        // TODO Make the whole serial comm stuff a lot more robust. What if the config file is bullshit.

    // later maybe, after 0.1

        // TODO terminal delete messages after a while, or add possibility to clear terminal.
        // TODO let the user select a default config if the hardcoded one is not found.
        // TODO add support for float and double and unsigned int maybe ??
        // TODO add an acceptable range of values to a dataSource. For example : A temperature should not exceed 100°C
        // TODO style everything consistently . with base 16 ez ez ez
        // TODO Add a way better management of visualization elements! Maybe someone wants the LineCHart to be 10 x bigger than a fucking bit flag.
        // TODO app logo
        // TODO let the user see last info for each source
        // TODO Multi Window support.

    public static Logger programLogger = Logger.getLogger("Program-Logger");
    private static FileHandler fileHandler;     //Needed for logging to file.

    public static  void main(String[] args){
        //First setup the logger before everything else, so anything that goes wrong can be logged.
        setupLogger();
        //Show a splash screen while MyApplication gets ready.
        LauncherImpl.launchApplication(MyApplication.class,MyPreloader.class,args);
    }

    /**
     * Sets up the programLogger
     * Credits: http://stackoverflow.com/questions/15758685/how-to-write-logs-in-text-file-when-using-java-util-logging-logger
     */
    private static void setupLogger() {
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy_HHmmss").format(Calendar.getInstance().getTime());
        try {
            // This block configures the programLogger with handler and formatter
            Files.createDirectories(Paths.get("logs"));     //Create folder if not existent.
            fileHandler = new FileHandler("logs/ProgramLog_"+ timeStamp +".log");
            programLogger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            // the following statement is used to log any messages
            programLogger.info("Logger initialized");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

}