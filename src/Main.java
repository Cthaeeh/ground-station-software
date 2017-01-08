import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.logging.SimpleFormatter;

/**
 * Created by Kai on 08.01.2017.
 * A simple example program for reading from the serial Port and writing to a log file.
 *
 * Also a test if i can use git ...
 */
public class Main {

    private static SerialPort serialPort;
    private static Logger logger;
    private static FileHandler fileHandler;     //Needed for logging to file.

    private static final String PORT_NAME = "COM7";
    private static boolean connected = false;

    public static  void main(String[] args){
        setupLogger();
        while (!connected){ //try to connect infinitely
            setupSerialPort();
            if(!connected){
                logger.log(Level.INFO,"Try again in 1 sek");
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        //Reading from COM-Port in other Thread so other code doesn´t lag.
        //Actually not needed here but important for later use.
        Thread thread = new Thread(){
            @Override public void run() {
                Scanner scanner = new Scanner(serialPort.getInputStream());
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    logger.log(Level.INFO,line);
                }
                scanner.close();
            }
        };
        thread.start();
    }

    private static void setupSerialPort() {
        logger.log(Level.INFO,"trying to initialize Serial Port");

        //TODO let the user choose which one he would like to use

        serialPort = SerialPort.getCommPort(PORT_NAME);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0,0);

        if (serialPort.openPort()) {
            logger.log(Level.INFO,"Connected to"+PORT_NAME);
            connected = true;
        }else {
            logger.log(Level.INFO,"Failed to connected to"+PORT_NAME);
        }
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
}
