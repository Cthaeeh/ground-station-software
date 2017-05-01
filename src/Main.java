import data.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/**
 * Created by Kai on 08.01.2017.
 *
 */
public class Main extends Application {

    public static Logger logger;
    private static FileHandler fileHandler;     //Needed for logging to file.

    private static final String CONNECTION_WINDOW_FXML = "gui/connection_window.fxml";
    private final String DEFAULT_INTERPRETATION_FILE = "interpretationFiles/test1.txt";

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

    /**
     * Will be called by Java it self.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public  void  start(Stage primaryStage) throws Exception{
        DataModel model = new DataModel();

        model.loadData(new File(DEFAULT_INTERPRETATION_FILE));

        FXMLLoader connectionWindowLoader = new FXMLLoader(getClass().getResource(CONNECTION_WINDOW_FXML));
        Scene scene = new Scene(connectionWindowLoader.load(), 600, 600);

        ConnectionWindowControl connectionWindowControl = connectionWindowLoader.getController();
        connectionWindowControl.initModel(model);

        scene.getStylesheets().add("gui/darkTheme.css");
        primaryStage.setTitle("COM-Port Selection");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
}