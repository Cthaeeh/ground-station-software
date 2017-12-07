package main;

import data.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by Kai on 11.11.2017.
 * Handles the Main Application start up.
 * This happens after the logger and splash screen are initialized/shown.
 */
public class MyApplication extends Application{

    /**
     * The default interpretation file that is used, which defines available sources etc.
     */
    private static final String DEFAULT_INTERPRETATION_FILE = "configs/default_config.txt";

    private static final String MAIN_WINDOW_FXML = "/gui/main_window.fxml";
    private static final String CSS_STYLING = "/gui/darkTheme.css";
    private static final String MAIN_WINDOW_TITLE = "ground station software 0.1";
    private DataModel model;
    private boolean configLoadFail = false;

    @Override
    public void init() {
        model = loadConfig();
        //Fake some loading time
        try {
            Thread.sleep(1000);
        } catch (Exception e){
            // I don't care
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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

        if(configLoadFail) emptyConfigWarning();
    }

    /**
     * Attempts to load the default config.
     * If the default config is not found sets a flag.
     * Then an empty config is used.
     */
    private DataModel loadConfig() {
        DataModel newModel = new DataModel();
        try {
            Main.programLogger.log(Level.INFO,()->"Try to load default config from: " + DEFAULT_INTERPRETATION_FILE);
            newModel.loadConfigData(new File(DEFAULT_INTERPRETATION_FILE));
        } catch (Exception e) { // Why can I not catch an IOException here , goes beyond me ...
            Main.programLogger.log(Level.WARNING, () -> "Default config failed to load, will use default config.");
            configLoadFail = true;
            newModel.loadEmptyConfig();
        }
        return newModel;
    }

    private void emptyConfigWarning(){
        Alert alert = new Alert(Alert.AlertType.WARNING, "Empty config is used");
        alert.setHeaderText("Failed to load: " + DEFAULT_INTERPRETATION_FILE);
        alert.getDialogPane().getStylesheets().add("/gui/darkTheme.css");
        alert.showAndWait();
    }

}
