package data.sources;


/**
 * Created by kai on 12/11/17.
 * POJO for transfering a GNSS Frame
 */
public class GnssFrame {
    private double longitude = 0;
    private double latitude = 0;
    private int numOfSatellites = -1;
    private double height = -1;
    private long time = -1;
    private int fixQuality = -1;

    public int getNumOfSatellites() {
        return numOfSatellites;
    }

    public void setNumOfSatellites(int numOfSatellites) {
        this.numOfSatellites = numOfSatellites;
    }

    public double getFixQuality() {
        return fixQuality;
    }

    public void setFixQuality(int fixQuality) {
        this.fixQuality = fixQuality;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString(){
        return "Lat: " + latitude + "° Lon: " + longitude + "° Sats: " + numOfSatellites + " Quality: " + fixQuality;
    }
}
