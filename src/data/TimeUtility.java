package data;

/**
 * Created by Kai on 08.07.2017.
 * Utility class for time stuff ( like program up time )
 */
//TODO in which package does this belong ?
public class TimeUtility {

    /**
     * Start Time of the program. //TODO FIXME this does not happen at program start up.
     */
    private static final long START_TIME = System.nanoTime();

    /**
     *
     * @return returns the up time of the program in seconds since program start.
     * With a precision of milliseconds.
     * for example 30.45 sec
     */
    public static double getUptimeSec(){
        long upTimeNs = (System.nanoTime() - START_TIME);
        long upTimeMs = upTimeNs/1000_000;
        return ((double)(upTimeMs))/1000.0;
    }
}
