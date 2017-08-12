package main;

import data.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/**
 * Created by Kai on 08.01.2017.
 * The Entry Point of this program.
 * Sets up a programLogger and a Main Window.
 */
public class Main extends Application {
//priority: // feature ?

                     // Big things that need to be done:
//     2       yes   // TODO advanced logging of data of raw data.
//     1       yes   // TODO find a convenient way to edit a config file.
//             yes   // TODO add optional params to tele-command functionality.
//                   // TODO somewhere make sure 2's complement is mentioned.
//     7       yes   // TODO add time functionality.
//                   // TODO NOTE that an uneven number of bytes for a value is discouraged.
                     // TODO add junit test everywhere. U can even test the serial stuff with linux and virtual ports.
                     // TODO think about the potential bug that when you open something like the Connection Window, does the other stuff still get updated.
//               yes // TODO Telecommand start stop bytes by config.
                     // TODO somehow detect invalid configs.
                     // TODO Make the whole serial comm stuff a lot more robust. What if the config file is bullshit.


                 // later maybe
                 // TODO let the user select a default config if the hardcoded one is not found.
//               // TODO add a way of exchanging. a config file while program is running. very difficult u have to restart the TM/TC thread and kill all the visualizations. Possible but ...
                 // TODO add support for float and double and unsigned int maybe ??
//               // TODO add an acceptable range of values to a dataSource. For example : A temperature should not exceed 100°C
                 // TODO style everything consistently . with base 16 ez ez ez
                 // TODO Add a way better management of visualization elements! Maybe someone wants the LineCHart to be 10 x bigger than a fucking bit flag.
//               // TODO app logo
                 // TODO let the user see last info for each source

    public static Logger programLogger = Logger.getLogger("Program-Logger");
    private static FileHandler fileHandler;     //Needed for logging to file.

    private static final String MAIN_WINDOW_FXML = "/gui/main_window.fxml";
    private static final String CSS_STYLING = "/gui/darkTheme.css";
    private static final String MAIN_WINDOW_TITLE = "ground station software 0.1";

    /**
     * The default interpretation file that is used, which defines available sources etc.
     */
    private static final String DEFAULT_INTERPRETATION_FILE = "configs/default_config.txt";

    public static  void main(String[] args){
        setupLogger();
        launch(args);
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
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public  void  start(Stage primaryStage) throws Exception{

        DataModel model = loadConfig();

        //Load fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
        Scene scene = new Scene(loader.load(), 1000, 850);

        //Inject model
        MainWindowControl mainWindowControl = loader.getController();
        mainWindowControl.initModel(model);

        //GUI-stuff
        scene.getStylesheets().add(CSS_STYLING);
        primaryStage.setTitle(MAIN_WINDOW_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(850);
        primaryStage.show();

    }

    /**
     * Attempts to load the default config.
     * If the default config is not found gives an error message to the user.
     * Then an empty config is used.
     */
    private DataModel loadConfig() {
        DataModel model = new DataModel();
        try {
            model.loadConfigData(new File(DEFAULT_INTERPRETATION_FILE));
        }catch (IOException e) {
            model.loadEmptyConfig();
            Alert alert = new Alert(Alert.AlertType.WARNING, "Default Interpretation File: " + DEFAULT_INTERPRETATION_FILE  + " not found.");
            alert.getDialogPane().getStylesheets().add("/gui/darkTheme.css");
            alert.showAndWait();
        }
        return model;
    }
}