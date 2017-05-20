package data;
import javafx.util.Pair;

/**
 * Created by Kai on 18.05.2017.
 */
public interface UpdateDataListener {
    void onUpdateData(DataSource dataSource, Pair<Number,Number> point);    //TODO replace Pair with something better, because it has nothing todo with key value, better would be time measurement.
}
