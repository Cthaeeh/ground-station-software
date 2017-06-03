package data;

/**
 * A class that implements this Interface promises it can do something with updates from a DataSource, that publishes points from time to time.
 * This way various classes can register to a DataSource an get informed about new data.
 * Created by Kai on 18.05.2017.
 */
public interface UpdateDataListener {
    void onUpdateData(DataSource dataSource, Point point);
}