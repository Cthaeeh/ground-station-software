package data;

/**
 * Created by kai on 3/6/18.
 */
public class ConfigChecker {
    public static void verification(Config config) throws InvalidConfig {
        timeVerification(config);
        // If the message length is fixed a lot of errors can be catched early.


    }

    private static void timeVerification(Config config) throws InvalidConfig {
        if (config.getReadMode() == Config.ReadMode.FIXED_MSG_LENGTH) {
            // If time is used
            if (config.getTimePosition() >= 0 && config.getTimeLength() > 0) {
                if (config.getTimePosition() + config.getTimeLength() > config.getMessageLength()) {
                    throw new InvalidConfig("Telemetry Time specified in the config is invalid : Pos: " +
                            config.getTimePosition() + " Length: " + config.getTimeLength() + " is greater than overall msg length: " +
                            config.getMessageLength());
                }
            }
        }
        if(config.getTimeLength()>8) throw new InvalidConfig("To many bytes for time");
    }
}
