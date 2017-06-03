import data.DataModel;
import data.DataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Kai on 08.01.2017.
 * The Entry Point of this program.
 * Sets up a logger and a Main Window.
 */
public class Main extends Application {
    // Big things that need to be done:
    // TODO logging of data / raw data.
    // TODO find a generic as possible way of giving a dataSource a formula on how to translate a raw value to some actual measurement.
    // TODO add units to dataSources.
    // TODO add an acceptable range of values to a dataSource. For example : A temperature should not exceed 100°C
    // TODO find a convinient way to edit a config file.
    // TODO fix the memory leak that occurs if someone removes a column or row or replaces a node inside a visualization element ( the thing is that gbc will not delete shit because they are still subscribed to a dataSource.
    // TODO style everything consistently .
    // TODO use vim.
    // TODO add telecommando functionality.
    // TODO add a way of visualizing flags. (Maybe add a new kind of dataSource)
    // TODO somewhere make sure 2's complement is mentioned.
    // TODO implement the status area ( at least basic functionality).
    // TODO add CRC 16 functionality.
    // TODO add time functionality.
    // TODO add a way of exchanging. a config file while program is running.


    public static Logger logger;
    private static FileHandler fileHandler;     //Needed for logging to file.

    /**
     * The location of the fxml that defines the look of the main window.
     */
    private static final String MAIN_WINDOW_FXML = "gui/main_window.fxml";

    /**
     * The default interpretation file that is used, which defines available dataSources etc.
     */
    private final String DEFAULT_INTERPRETATION_FILE = "interpretationFiles/test3.txt";

    public static  void main(String[] args){
        setupLogger();
        launch(args);
    }

    /**
     * Sets up the logger
     * Credits: http://stackoverflow.com/questions/15758685/how-to-write-logs-in-text-file-when-using-java-util-logging-logger
     */
    private static void setupLogger() {
        logger = Logger.getLogger("Data-Logger");
        try {
            // This block configures the logger with handler and formatter
            fileHandler = new FileHandler("logs/MyLogFile.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            // the following statement is used to log any messages
            logger.info("Logger initialized");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public  void  start(Stage primaryStage) throws Exception{
        DataModel model = new DataModel();
        model.loadConfigData(new File(DEFAULT_INTERPRETATION_FILE));

        //Load fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
        Scene scene = new Scene(loader.load(), 800, 600);

        //Inject model
        MainWindowControl mainWindowControl = loader.getController();
        mainWindowControl.initModel(model);

        //GUI-stuff
        scene.getStylesheets().add("gui/darkTheme.css");
        primaryStage.setTitle("COM-Port Selection");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        //startDemoDataThread(model);
    }

    private void startDemoDataThread(DataModel model1) {
        Thread thread = new Thread(){
            @Override public void run() {
                double fakeTime = 0;
                boolean isRunning = true;
                while(isRunning) {
                    double val = 0;
                    for(DataSource dataSource: model1.getDataSources()){
                        dataSource.addDataPoint(fakeTime,(val++)+Math.sin(fakeTime));
                    }
                    fakeTime += 0.1;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}