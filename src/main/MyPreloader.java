package main;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Level;

/**
 * Created by Kai on 11.11.2017.
 * Preloader that shows undecorated window, with splash screen.
 * Here a progress bar could be implemented somehow.
 */
public class MyPreloader extends Preloader{

    private Stage preloaderStage;
    private static final String SPLASH_FXML = "/gui/splash.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(SPLASH_FXML));
        Scene scene = new Scene(loader.load(), 600, 400);

        //GUI-stuff
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
        Main.programLogger.log(Level.INFO,"Splash Screen initialized");
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            preloaderStage.hide();
        }
    }
}
